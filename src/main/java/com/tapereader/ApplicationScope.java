package com.tapereader;

public class ApplicationScope {

    private final String[] args;
    
    private final String propertiesFileName;

    public ApplicationScope(String[] args, String propertiesFileName) {
        this.args = args;
        this.propertiesFileName = propertiesFileName;
    }

    public String[] getArgs() {
        return args;
    }
    
    public String getPropertiesFileName() {
        return propertiesFileName;
    }

}
