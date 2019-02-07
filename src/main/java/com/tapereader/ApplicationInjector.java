package com.tapereader;

public class ApplicationInjector {
    
    public static MainHelper injectMainHelper(ApplicationScope scope) {
        return new MainHelper(
                injectArgs(scope), 
                injectPropertiesFileName(scope));
    }
    
    public static String[] injectArgs(ApplicationScope scope) {
        return scope.getArgs();
    }
    
    public static String injectPropertiesFileName(ApplicationScope scope) {
        return scope.getPropertiesFileName();
    }

}
