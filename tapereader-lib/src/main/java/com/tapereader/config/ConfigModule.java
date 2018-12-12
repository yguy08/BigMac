package com.tapereader.config;

import java.io.FileInputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
      Properties properties = new Properties();
      String trprop = System.getProperty("tr.properties");
      try (FileInputStream fis = new FileInputStream(trprop)) {
          properties.load(new FileInputStream(trprop));
          Names.bindProperties(binder(), properties);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}
