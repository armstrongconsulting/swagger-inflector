/*
 *  Copyright 2017 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.swagger.inflector.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.inflector.validators.ValidationMessage;

public class ApiError {
    private int code;
    private String message;

    public ApiError code(int code) {
        this.code = code;
        return this;
    }

   
    private List<ValidationMessage> constraintViolations;
    
    public ApiError message(String message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    @JsonProperty("constraint_violations")
    public List<ValidationMessage> getConstraintViolations(){
    	return constraintViolations;
    }
    
    public ApiError addConstraintViolation(ValidationMessage m){
    	if (constraintViolations == null)
    		constraintViolations = new ArrayList<ValidationMessage>();
    	constraintViolations.add(m);
    	return this;
    	
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
