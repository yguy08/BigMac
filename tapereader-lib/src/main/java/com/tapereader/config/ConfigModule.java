package com.tapereader.config;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
      Properties properties = new Properties();
      try {
          properties.load(ConfigModule.class.getResourceAsStream("/application.properties"));
          Names.bindProperties(binder(), properties);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}
