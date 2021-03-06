package io.swagger.test.schema;

import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.inflector.schema.SchemaValidator;
import io.swagger.util.Json;

public class SchemaValidationTest {
    @Test
    public void testValidPayload() {
        User user = new User();
        user.id = 9873432343L;
        user.name = "Fred";

        String schema = "{\n" +
                "  \"required\": [\n" +
                "    \"id\"\n" +
                "  ],\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertTrue(SchemaValidator.validate(user, schema, SchemaValidator.Direction.INPUT).isSuccess());
    }

    public void testInvalidPayload() {
        User user = new User();
        user.name = "Fred";

        String schema = "{\n" +
                "  \"required\": [\n" +
                "    \"id\"\n" +
                "  ],\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertFalse(SchemaValidator.validate(user, schema, SchemaValidator.Direction.INPUT).isSuccess());
    }

    public void testInvalidPayloadWithRange() {
        User user = new User();
        user.id = 0L;
        user.name = "Fred";

        String schema = "{\n" +
                "  \"required\": [\n" +
                "    \"id\"\n" +
                "  ],\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\",\n" +
                "      \"minimum\": 123,\n" +
                "      \"maximum\": 400\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertFalse(SchemaValidator.validate(user, schema, SchemaValidator.Direction.INPUT).isSuccess());
    }

    @Test
    public void testValidation() throws Exception {
        String schemaAsString =
                "{\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode schemaObject = Json.mapper().readTree(schemaAsString);
        JsonNode content = Json.mapper().readValue("{\n" +
                "  \"id\": 123\n" +
                "}", JsonNode.class);

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        com.github.fge.jsonschema.main.JsonSchema schema = factory.getJsonSchema(schemaObject);

        ProcessingReport report = schema.validate(content);
        assertTrue(report.isSuccess());
    }

    static class User {
        public Long id;
        public String name;
    }
}
