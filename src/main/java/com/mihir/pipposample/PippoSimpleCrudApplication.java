package com.mihir.pipposample;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import ro.pippo.controller.Controller;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Pippo;
import ro.pippo.fastjson.FastjsonEngine;

import java.util.Set;

public class PippoSimpleCrudApplication extends ControllerApplication{
    @Inject
    public PippoSimpleCrudApplication(Set<Controller> controllerSet) {
        registerContentTypeEngine(FastjsonEngine.class);
        controllerSet.forEach(this::addControllers);
    }

    public static void main(String []args){
        Injector injector = Guice.createInjector(new ApplicationModule());
        ControllerApplication app = injector.getInstance(
                PippoSimpleCrudApplication.class
        );

        (new Pippo(app)).start();
    }
}
