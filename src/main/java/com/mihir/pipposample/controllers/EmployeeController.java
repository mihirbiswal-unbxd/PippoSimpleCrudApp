package com.mihir.pipposample.controllers;

import com.google.inject.Inject;
import com.mihir.pipposample.services.EmployeeService;
import ro.pippo.controller.Controller;

public class EmployeeController extends Controller {
    @Inject
    EmployeeService employeeService;
}
