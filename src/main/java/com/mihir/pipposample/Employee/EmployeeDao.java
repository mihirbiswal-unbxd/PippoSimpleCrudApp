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

    public void createEmployee(Map<String,Object> employee) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        if(!employee.containsKey("email")) throw new Exception("Email is a mandatory field.");
        String email = (String) employee.get("email");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            if(collection.find(new Document("email", email)).iterator().hasNext())
                throw new Exception("This employee already exists: " + email + ". Use update instead.");
            Document employeeDoc = new Document();
            employee.entrySet().stream().forEach(entry -> employeeDoc.put(entry.getKey(),entry.getValue()));
            collection.insertOne(employeeDoc);
        } catch (MongoException e) {
            throw new Exception(e);
        }
    }

    public Map<String,Object> getEmployeeByEmail(String email) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        Map<String,Object> employee = new HashMap<>();
        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            Iterator iter = collection.find(new Document("email", email)).iterator();
            if(iter.hasNext()) { employee = (Map) iter.next(); }
        } catch (MongoException e) {
            throw new Exception(e);
        }
        return employee;
    }

//    public Set<Map<String,Object>> bulkGetEmployeeByEmail(Set<String> emails) throws Exception{
//        if(status == HEALTH_STATUS.FAILED || mongo == null)
//            throw new Exception("Unable to connect to MongoDao");
//
//        Set<Map<String,Object>> employees = new HashSet<>();
//        String email = "mihir.biswal@unbxd.com";
//        try{
//            MongoDatabase db = mongo.getDatabase(database);
//            MongoCollection<Document> collection = db.getCollection(empCollection);
//            Iterator iter = collection.find(new Document("email", email)).iterator();
//            while(iter.hasNext()) {
//                employees.add((Map)iter.next());
//            }
//        } catch (MongoException e) {
//            throw new Exception(e);
//        }
//        return employees;
//    }

    public void updateEmployeeByEmail(String email, Map<String,Object> updateData) throws Exception{
        if(status == HEALTH_STATUS.FAILED || mongo == null)
            throw new Exception("Unable to connect to MongoDao");

        try{
            MongoDatabase db = mongo.getDatabase(database);
            MongoCollection<Document> collection = db.getCollection(empCollection);
            Document updateDoc = new Document();
            updateData.entrySet().stream().forEach(entry -> updateDoc.put(entry.getKey(),entry.getValue()));
            collection.updateOne(new Document("email", email), updateDoc);
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
