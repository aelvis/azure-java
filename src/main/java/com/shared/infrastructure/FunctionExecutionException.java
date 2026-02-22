package com.shared.infrastructure;

public class FunctionExecutionException extends Exception {
    
    private final String functionName;
    private final transient Object input;
    
    public FunctionExecutionException(String message) {
        super(message);
        this.functionName = null;
        this.input = null;
    }
    
    public FunctionExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.functionName = null;
        this.input = null;
    }
    
    public FunctionExecutionException(String message, String functionName, Object input) {
        super(message);
        this.functionName = functionName;
        this.input = input;
    }
    
    public FunctionExecutionException(String message, Throwable cause, String functionName, Object input) {
        super(message, cause);
        this.functionName = functionName;
        this.input = input;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public Object getInput() {
        return input;
    }
    
    @Override
    public String toString() {
        return String.format("FunctionExecutionException{functionName='%s', input=%s, message=%s}",
            functionName, input, getMessage());
    }
}