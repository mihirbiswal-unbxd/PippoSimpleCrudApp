package com.mihir.pipposample.services;

import com.google.inject.Inject;
import com.mihir.pipposample.dao.EmployeeDao;

public class EmployeeService {
    @Inject
    EmployeeDao employeeDao;
}
