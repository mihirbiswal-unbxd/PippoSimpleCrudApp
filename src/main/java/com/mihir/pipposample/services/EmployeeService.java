package com.mihir.pipposample.services;

import com.google.inject.Inject;
import com.mihir.pipposample.dao.EmployeeDao;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmployeeService {
    @Inject
    EmployeeDao employeeDao;

    public void createEmployee(Map<String,Object> employee) {
        try{
            employeeDao.createEmployee(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void bulkCreateEmployees(Set<Map<String,Object>> employees) throws Exception{
            employees.forEach(this::createEmployee);
    }

    public Set<Map<String,Object>> bulkGetEmployeeByEmail(Set<String> emails) {
        Set<Map<String,Object>> result = new HashSet<>();
        try {
            employeeDao.bulkGetEmployeeByEmail(emails);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void updateEmployeeByEmail(String email, Map<String,Object> updateData) {
        try {
            employeeDao.updateEmployeeByEmail(email,updateData);
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
