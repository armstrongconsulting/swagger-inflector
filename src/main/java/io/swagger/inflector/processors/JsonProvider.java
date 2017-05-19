package io.swagger.inflector.processors;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.inflector.SwaggerInflector;

@Provider
public class JsonProvider implements ContextResolver<ObjectMapper> {
    private final ObjectMapper objectMapper;
    private boolean prettyPrint;

    public JsonProvider() {
        objectMapper = SwaggerInflector.mapper();
    }

    public JsonProvider(boolean prettyPrint) {
        this();
        this.prettyPrint = prettyPrint;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        if(this.prettyPrint) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        return objectMapper;
    }
}