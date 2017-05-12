package io.swagger.inflector.validators;

public class ValidationMessage {
    private ValidationError code;
    private String message;
    private String path;
    
    
    public ValidationMessage code(ValidationError code) {
        this.code = code;
        return this;
    }
    
    public ValidationMessage path(String path) {
        this.path = path;
        return this;
    }
    
    public ValidationMessage message(String message) {
        this.message = message;
        return this;
    }

    public ValidationError getCode() {
        return code;
    }
    
    public String getPath(){
    	return path;
    }
    
    public void setPath(String path){
    	this.path = path;
    }
    
    public void setCode(ValidationError code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}