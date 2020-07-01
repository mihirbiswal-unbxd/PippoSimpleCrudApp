package com.mihir.pipposample.dao;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EmployeeDao implements ServerMonitorListener {
    private enum HEALTH_STATUS{
        STARTED, SUCCEEDED, FAILED
    }

    private HEALTH_STATUS status;
    private MongoClient mongo;
    private String mongoHost = "mongo";
    private Integer mongoPort = 27010;
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
                throw new Exception("This employee already exists. Use update instead.");
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
            collection.deleteOne(new Document("email", email));
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
