package com.tapereader.server;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import com.google.common.reflect.ClassPath;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tapereader.annotation.Boot;
import com.tapereader.boot.BootLoader;
import com.tapereader.boot.BootModule;
import com.tapereader.boot.BootStarter;

public class ServerLoader implements BootLoader {

    public static void load(Class<?> clazz, String[] args) {
        // Load application.properties first
        List<AbstractModule> modules = new ArrayList<>();
        modules.add(new BootModule());
        
        // Find profile properties
        String trprofile = System.getProperty("tr.profile");
        String[] names = trprofile.split(",");
        
        // Get Modules
        modules.addAll(getModules(names));
        
        Injector injector = Guice.createInjector(modules);
        BootStarter starter = (BootStarter) injector.getInstance(clazz);
        starter.run();
    }

    private static List<AbstractModule> getModules(String[] moduleNames) {
        List<AbstractModule> modules = new ArrayList<>(moduleNames.length);
        try {
            ClassPath classPath = ClassPath.from(ServerLoader.class.getClassLoader());
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses("com.tapereader.server.module")) {
                Class<?> clazz = classInfo.load();
                for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                    if (annotation instanceof Boot) {
                        Boot boot = (Boot) annotation;
                        String name = boot.moduleName();
                        for (String moduleStr : moduleNames) {
                            if (moduleStr.trim().equalsIgnoreCase(name)) {
                                modules.add((AbstractModule) clazz.newInstance());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading modules!");
        }
        return modules;
    }

}
