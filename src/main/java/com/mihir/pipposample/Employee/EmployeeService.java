package com.mihir.pipposample.Employee;

import com.google.inject.Inject;
import com.mihir.pipposample.Employee.EmployeeDao;
import org.bson.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmployeeService {
    @Inject
    EmployeeDao employeeDao;

    public void createEmployee(Document employee) {
        try{
            employeeDao.createEmployee(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void bulkCreateEmployees(Set<Document> employees) throws Exception{
        try{
            employeeDao.bulkCreateEmployee(employees);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String,Object> getEmployeeByEmail(String email) {
        Map<String,Object> result = new HashMap<>(0);
        try {
            result = employeeDao.getEmployeeByEmail(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public Set<Document> bulkGetEmployeeByEmail(Set<String> emails) {
        Set<Document> result = new HashSet<>(0);
        try {
            result = employeeDao.bulkGetEmployeeByEmail(emails);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void updateEmployeeByEmail(String email, Document updateData) {
        try {
            employeeDao.updateEmployeeByEmail(email,updateData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteEmployeeByEmail(String email) {
        try {
            employeeDao.deleteEmployeeByEmail(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void bulkDeleteEmployeeByEmail(Set<String> emails) {
        try {
            employeeDao.bulkDeleteEmployeeByEmail(emails);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
