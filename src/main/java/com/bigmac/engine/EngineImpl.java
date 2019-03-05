package com.bigmac.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import com.bigmac.Subscriber;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.deploy.DeploymentException;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.DeploymentResult;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.ParseException;

public class EngineImpl implements Engine {

    @SuppressWarnings("unused")
    private EPServiceProvider epService;

    private EPRuntime epRuntime;

    private EPAdministrator epAdministrator;

    public EngineImpl() {

    }

    public EngineImpl(EPServiceProvider epService) {
        this.epService = epService;
        this.epRuntime = epService.getEPRuntime();
        this.epAdministrator = epService.getEPAdministrator();
    }

    @Override
    public EPStatement createEPL(String expression, String statementName) {
        EPStatement stmt = epAdministrator.createEPL(expression, statementName);
        return stmt;
    }

    @Override
    public void sendEvent(Object event) {
        epRuntime.sendEvent(event);
    }
    
    public void loadStatements(String source, Object subscriber) {
        EPDeploymentAdmin deploymentAdmin = epAdministrator.getDeploymentAdmin();
        com.espertech.esper.client.deploy.Module module;
        String filename = source + ".epl";
        try {
            module = deploymentAdmin.read(filename);
            DeploymentResult deployResult;
            try {
                deployResult = deploymentAdmin.deploy(module, new DeploymentOptions());
                List<EPStatement> statements = deployResult.getStatements();
                for (EPStatement statement : statements) {
                    try {
                        processAnnotations(statement, subscriber);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                System.out.println("deployed module " + filename);
            } catch (DeploymentException e) {
                System.out.println(e);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        } catch (FileNotFoundException ignored) {
            // it is not neccessary for every module to have an EPL file
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e);
        }
    }

    public void processAnnotations(EPStatement statement, Object obj) throws Exception {
        Annotation[] annotations = statement.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Subscriber) {
                Subscriber subscriber = (Subscriber) annotation;
                statement.setSubscriber(obj, subscriber.methodName());
            }
        }
    }

}
