package com.tapereader;

public class Application {
    
    public static void main(String[] args) {
        ApplicationScope scope = new ApplicationScope(args, "application.properties");
        MainHelper helper = ApplicationInjector.injectMainHelper(scope);
        helper.run();
    }

}
