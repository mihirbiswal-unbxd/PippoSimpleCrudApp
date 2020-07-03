package com.mihir.pipposample.Employee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.bson.Document;
import ro.pippo.controller.*;

import java.util.*;

@Path("/")
public class EmployeeController extends Controller {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Inject
    EmployeeService employeeService;

    @GET("")
    @Produces(Produces.JSON)
    public String getEmployeeByEmail() {
        System.out.println("executing get.");
        String email_id = getRequest().getQueryParameter("email_id").toString();
        String resultStr = "";
        try{
            Map<String,Object> result = new HashMap<>();
            Set<Document> results = new HashSet<>();
            if(email_id.contains(",")) {
                String[] emails = email_id.split(",");
                results = employeeService.bulkGetEmployeeByEmail(new HashSet<String>(Arrays.asList(emails)));
                resultStr = mapper.writeValueAsString(results);
            } else {
                result = employeeService.getEmployeeByEmail(email_id);
                resultStr = mapper.writeValueAsString(result);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return resultStr;
    }

    @POST("/create")
    public void createEmployee() {
        System.out.println("executing post.");
        String requestBody = getRequest().getBody();
        try{
            JsonNode body = mapper.readTree(requestBody);
            if(body.isArray()) {
                Set<Document> employees = mapper.readValue(requestBody, new TypeReference<HashSet<Document>>(){});
                employeeService.bulkCreateEmployees(employees);
            } else {
                Document employee = mapper.readValue(requestBody, new TypeReference<Document>(){});
                employeeService.createEmployee(employee);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @PUT("/update")
    public void updateEmployee() {
        System.out.println("executing put.");
        String email_id = getRequest().getQueryParameter("email_id").toString();
        String requestBody = getRequest().getBody();
        try {
            Document updateData = mapper.readValue(requestBody, new TypeReference<Document>() {});
            employeeService.updateEmployeeByEmail(email_id, updateData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @DELETE("/delete")
    public void deleteEmployee() {
        System.out.println("executing delete.");
        String email_id = getRequest().getQueryParameter("email_id").toString();
        if(email_id.contains(",")) {
            String[] emails = email_id.split(",");
            employeeService.bulkDeleteEmployeeByEmail(new HashSet<String>(Arrays.asList(emails)));
        } else {
            employeeService.deleteEmployeeByEmail(email_id);
        }
    }
}
