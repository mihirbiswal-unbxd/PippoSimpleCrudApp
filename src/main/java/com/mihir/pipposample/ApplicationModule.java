package com.mihir.pipposample;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mihir.pipposample.Employee.EmployeeController;
import ro.pippo.controller.Controller;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Controller> controllers = Multibinder.newSetBinder(
                binder(), Controller.class
        );
        controllers.addBinding().to(EmployeeController.class);
    }
}
