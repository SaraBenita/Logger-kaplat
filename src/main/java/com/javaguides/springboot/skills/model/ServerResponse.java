package com.javaguides.springboot.skills.model;

public class ServerResponse {
    private Object result;
    private String errorMessage;
    public ServerResponse(Object result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public Object getResult() {
        return result;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public void setResult(Object result) {
        this.result = result;
    }
}
