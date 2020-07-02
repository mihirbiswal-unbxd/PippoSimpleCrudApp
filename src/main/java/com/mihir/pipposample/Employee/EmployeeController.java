package com.mihir.pipposample.Employee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
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
        Map<String,Object> result = employeeService.getEmployeeByEmail(email_id);
        String resultStr = "";
        try{
            resultStr = mapper.writeValueAsString(result);
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
            Map<String,Object> employee = mapper.readValue(requestBody, new TypeReference<HashMap<String,Object>>(){});
            employeeService.createEmployee(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //TODO there is some issue with update. Fix it.
    @PUT("/update")
    public void updateEmployee() {
        System.out.println("executing put.");
        String email_id = getRequest().getQueryParameter("email_id").toString();
        String requestBody = getRequest().getBody();
        try {
            Map<String, Object> updateData = mapper.readValue(requestBody, new TypeReference<HashMap<String, Object>>() {
            });
            employeeService.updateEmployeeByEmail(email_id, updateData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @DELETE("/delete")
    public void deleteEmployee() {
        System.out.println("executing delete.");
        String email_id = getRequest().getQueryParameter("email_id").toString();
        employeeService.deleteEmployeeByEmail(email_id);
    }
}
