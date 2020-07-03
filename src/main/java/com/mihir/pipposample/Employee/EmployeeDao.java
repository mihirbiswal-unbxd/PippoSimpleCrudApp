package com.mihir.pipposample.Employee;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDao implements ServerMonitorListener {
    private enum HEALTH_STATUS{
        STARTED, SUCCEEDED, FAILED
    }

    private HEALTH_STATUS status;
    private MongoClient mongo;
    private String mongoHost = "127.0.0.1";
    private Integer mongoPort = 27017;
    private String database = "EmployeeDB";
    private String empCollection = "Employee";

    Object healthCheckLock = new Object();

    private ServerAddress servers;

    @Inject
    public EmployeeDao(){
        super();

        servers = new ServerAddress(mongoHost, mongoPort);
        MongoClientOptions clientOptions = MongoClientOptions.builder()
                .connectionsPerHost(10)
                .addServerMonitorListener(this)
                .build();

        mongo = new MongoClient(servers , clientOptions);
    }

    public void createEmployee(Document employee) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        if(!employee.containsKey("email")) throw new Exception("Email is a mandatory field.");
        String email = (String) employee.get("email");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            if(collection.find(new Document("email", email)).iterator().hasNext())
                throw new Exception("This employee already exists: " + email + ". Use update instead.");
            collection.insertOne(employee);
        } catch (MongoException e) {
            throw new Exception(e);
        }
    }

    public void bulkCreateEmployee(Set<Document> employees) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            List<Document> employeeList = employees.stream()
                    .filter(employee -> employee.containsKey("email")
                            && !(collection.find(new Document("email", employee.get("email"))).iterator().hasNext()))
                    .collect(Collectors.toList());
            collection.insertMany(employeeList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Document getEmployeeByEmail(String email) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        Document employee = new Document();
        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            Iterator iter = collection.find(new Document("email", email)).iterator();
            if(iter.hasNext()) { employee = (Document) iter.next(); }
        } catch (MongoException e) {
            throw new Exception(e);
        }
        return employee;
    }

    public Set<Document> bulkGetEmployeeByEmail(Set<String> emails) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        Set<Document> employees = new HashSet<>();
        List<Document> emailsToFind = new ArrayList<>();
        emails.forEach(e -> emailsToFind.add(new Document("email",e)));

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            Iterator iter = collection.find(new Document("$or", emailsToFind)).iterator();
            while(iter.hasNext()) {
                employees.add((Document) iter.next());
            }
        } catch (MongoException e) {
            throw new Exception(e);
        }
        return employees;
    }

    public void updateEmployeeByEmail(String email, Document updateData) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            collection.updateOne(new Document("email", email), new Document("$set", updateData));
        } catch (MongoException e) {
            throw new Exception(e);
        }
    }

    public void deleteEmployeeByEmail(String email) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            collection.deleteMany(new Document("email", email));
        } catch (MongoException e) {
            throw new Exception(e);
        }
    }

    public void bulkDeleteEmployeeByEmail(Set<String> emails) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        List<Document> emailsToDelete = new ArrayList<>();
        emails.forEach(e -> emailsToDelete.add(new Document("email",e)));
        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            collection.deleteMany(new Document("$or", emailsToDelete));
        } catch (MongoException e) {
            throw new Exception(e);
        }
    }

    // Check if mongo connection lives
    @Override
    public void serverHearbeatStarted(
            ServerHeartbeatStartedEvent serverHeartbeatStartedEvent
    ) {
        synchronized (healthCheckLock){ this.status = HEALTH_STATUS.STARTED; }
    }

    @Override
    public void serverHeartbeatSucceeded(
            ServerHeartbeatSucceededEvent serverHeartbeatSucceededEvent
    ) {
        synchronized (healthCheckLock){ this.status = HEALTH_STATUS.SUCCEEDED; }
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent serverHeartbeatFailedEvent) {
        synchronized (healthCheckLock){this.status = HEALTH_STATUS.FAILED;}
    }
}
