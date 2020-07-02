package com.mihir.pipposample.Employee;

import com.google.inject.Inject;
import com.mihir.pipposample.Employee.EmployeeDao;

import java.util.HashMap;
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

    public Map<String,Object> getEmployeeByEmail(String email) {
        Map<String,Object> result = new HashMap<>(0);
        try {
            result = employeeDao.getEmployeeByEmail(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

//    public Set<Map<String,Object>> bulkGetEmployeeByEmail(Set<String> emails) {
//        Set<Map<String,Object>> result = new HashSet<>(0);
//        try {
//            result = employeeDao.bulkGetEmployeeByEmail(emails);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return result;
//    }

    public void updateEmployeeByEmail(String email, Map<String,Object> updateData) {
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
}
