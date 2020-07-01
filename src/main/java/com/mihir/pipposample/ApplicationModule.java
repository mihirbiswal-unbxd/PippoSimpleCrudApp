package com.mihir.pipposample;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mihir.pipposample.controllers.EmployeeController;
import ro.pippo.controller.Controller;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Controller.class).to(EmployeeController.class);
    }
}
