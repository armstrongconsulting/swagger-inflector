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

package io.swagger.inflector.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CORSFilter implements javax.servlet.Filter {
 
    
    private Set<String> allowHeaders = new HashSet<String>();
    private Set<String> allowExposeHeaders = new HashSet<String>();
    
    public CORSFilter allowHeader(String name){
    	allowHeaders.add(name);
    	return this;
    }
   
    public CORSFilter allowExposeHeader(String name){
    	allowExposeHeaders.add(name);
    	return this;
    }
   
    public String allow_header;
    public String allow_expose_headers;
       
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", allow_header);
        if (allow_expose_headers != null)
        	res.addHeader("Access-Control-Expose-Headers", allow_expose_headers);
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	allowHeaders.add("Content-Type");
      	allowHeaders.add("api_key");
      	allowHeaders.add("Authorization");
      	
      	allow_header = "";
      	for (String h: allowHeaders){
      		if (!allow_header.isEmpty()) allow_header+= ", ";
      		allow_header += h;
      	}
      	
      	allow_expose_headers = "";
      	for (String h: allowExposeHeaders){
      		if (!allow_expose_headers.isEmpty()) allow_expose_headers+= ", ";
      		allow_expose_headers += h;
      	}
      	
    }
}