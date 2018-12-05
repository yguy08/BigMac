package com.tapereader.boot;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class BootModule extends AbstractModule {

    @Override
    protected void configure() {
        Properties properties = new Properties();
        
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        String trprop = System.getProperty("tr.properties");
        
        if (is == null && trprop == null) {
            throw new RuntimeException("No properties file specified!");
        }
        
        try {
            properties.load(is);
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded application.properties from resources");
            return;
        } catch (Exception e) {
            System.out.println("No properties file in resources..");
        }
        
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(trprop));
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded application.properties from system properties as resource");
        } catch (Exception e) {
            
        }
        
        try (FileInputStream fis = new FileInputStream(trprop)) {
            properties.load(new FileInputStream(trprop));
            Names.bindProperties(binder(), properties);
            System.out.println("Loaded application.properties from system properties as a file");
        } catch (Exception e) {
            
        }
    }
}
