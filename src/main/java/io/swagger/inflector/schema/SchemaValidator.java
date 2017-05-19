package io.swagger.inflector.schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.inflector.SwaggerInflector;
import io.swagger.inflector.utils.ApiErrorUtils;
import io.swagger.inflector.utils.ApiException;

public class SchemaValidator {
	static Map<String, JsonSchema> SCHEMA_CACHE = new HashMap<String, JsonSchema>();
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaValidator.class);

	public enum Direction {
		INPUT,
		OUTPUT
	}

	public static ProcessingReport validate(Object o, String schema, Direction direction) {
		try {
			JsonNode schemaObject = SwaggerInflector.mapper().readTree(schema);
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonNode content = SwaggerInflector.mapper().convertValue(o, JsonNode.class);
			com.github.fge.jsonschema.main.JsonSchema jsonSchema = factory.getJsonSchema(schemaObject);

			ProcessingReport report = jsonSchema.validate(content);
			if (!report.isSuccess()) {
				if (direction.equals(Direction.OUTPUT)) {
					LOGGER.error("response: " + content.toString() + "\n" + "does not match schema: \n" + schema);
				}
			}
			return report;
		} catch (Exception e) {
			throw new ApiException(ApiErrorUtils.createInternalError(), e);
		}
	}

	public static com.github.fge.jsonschema.main.JsonSchema getValidationSchema(String schema) throws IOException, ProcessingException {
		schema = schema.trim();

		JsonSchema output = SCHEMA_CACHE.get(schema);

		if (output == null) {
			JsonNode schemaObject = SwaggerInflector.mapper().readTree(schema);
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			com.github.fge.jsonschema.main.JsonSchema jsonSchema = factory.getJsonSchema(schemaObject);
			SCHEMA_CACHE.put(schema, jsonSchema);
			output = jsonSchema;
		}
		return output;
	}
}
