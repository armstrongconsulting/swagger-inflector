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

package io.swagger.inflector.processors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.swagger.inflector.SwaggerInflector;
import io.swagger.inflector.converters.ConversionException;
import io.swagger.inflector.models.ApiError;
import io.swagger.inflector.utils.ApiException;
import io.swagger.inflector.validators.ValidationError;
import io.swagger.inflector.validators.ValidationMessage;
import io.swagger.util.Yaml;

public class JacksonProcessor implements EntityProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonProcessor.class);

    public static MediaType APPLICATION_YAML_TYPE = new MediaType("application", "yaml");
    public static MediaType APPLICATION_MERGE_PATCH_JSON_TYPE = new MediaType("application","merge-patch+json");
    private static XmlMapper XML = new XmlMapper();
    private static List<MediaType> SUPPORTED_TYPES = new ArrayList<>();

    static {
        SUPPORTED_TYPES.add(MediaType.APPLICATION_JSON_TYPE);
        SUPPORTED_TYPES.add(APPLICATION_MERGE_PATCH_JSON_TYPE);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return new ArrayList(SUPPORTED_TYPES);
    }

    @Override
    public void enableType(MediaType type) {
        if(!SUPPORTED_TYPES.contains(type)) {
            SUPPORTED_TYPES.add(type);
        }
    }

    @Override
    public boolean supports(MediaType mediaType) {
        for (MediaType item : SUPPORTED_TYPES) {
            if (item.isCompatible(mediaType) && !mediaType.isWildcardType()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object process(MediaType mediaType, InputStream entityStream,
                          JavaType javaType) {
        try {
            if (APPLICATION_MERGE_PATCH_JSON_TYPE.isCompatible(mediaType) || MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
                return SwaggerInflector.mapper().readValue(entityStream, javaType);
            }
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                return XML.readValue(entityStream, javaType);
            }
            if (APPLICATION_YAML_TYPE.isCompatible(mediaType)) {
                return Yaml.mapper().readValue(entityStream, javaType);
            }
        } catch (IOException e) {
            LOGGER.error("unable to extract entity from content-type `" + mediaType + "` to " + javaType.toCanonical(), e);
        }

        return null;
    }

    @Override
    public Object process(MediaType mediaType, InputStream entityStream, Class<?> cls) throws ConversionException {
        try {
            if(String.class.equals(cls)) {
                OutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(entityStream, outputStream);
                return outputStream.toString();
            }
            if (APPLICATION_MERGE_PATCH_JSON_TYPE.isCompatible(mediaType) || MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
                return SwaggerInflector.mapper().readValue(entityStream, cls);
            }
            if (MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType)) {
                return XML.readValue(entityStream, cls);
            }
            if (APPLICATION_YAML_TYPE.isCompatible(mediaType)) {
                return Yaml.mapper().readValue(entityStream, cls);
            }
        } catch (JsonMappingException e) {
   
        	StringBuilder path = new StringBuilder();
        	for (Reference r : e.getPath()){
        		if (path.length()>0)path.append(".");
        		path.append(r.getFieldName());
        	}
            
        	throw new ConversionException()
                    .message(new ValidationMessage()
                    		.path(path.toString())
                            .code(ValidationError.UNACCEPTABLE_VALUE)
                            .message(e.getOriginalMessage()));
 
        } catch (JsonParseException e) {
            throw new ApiException(new ApiError().code(400).message("The data you provided cannot be parsed into json. " + e.getOriginalMessage()));
        } catch (Exception e) {
            LOGGER.trace("unable to extract entity from content-type `" + mediaType + "` to " + cls.getCanonicalName(), e);
            throw new ConversionException()
                    .message(new ValidationMessage()
                            .code(ValidationError.UNACCEPTABLE_VALUE)
                            .message("unable to convert input to " + cls.getCanonicalName()));
        }

        return null;
    }
}