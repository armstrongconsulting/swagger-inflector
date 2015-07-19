package io.swagger.test;

import static org.testng.Assert.assertEquals;
import io.swagger.inflector.config.Configuration;
import io.swagger.inflector.utils.ReflectionUtils;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.RefProperty;
import io.swagger.sample.models.User;
import io.swagger.test.models.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BodyParamExtractionTest {
  ReflectionUtils utils = new ReflectionUtils();

  @BeforeClass
  public void setup() {
    Configuration config = new Configuration();
    config.setModelPackage("io.swagger.test.models");
    config.addModelMapping("User", User.class);
    
    utils.setConfiguration(config);
  }

  @Test
  public void testConvertComplexBodyParamWithConfigMapping() throws Exception {
    Map<String, Model> definitions = new HashMap<String, Model>();

    Parameter parameter = new BodyParameter().schema(new RefModel("#/definitions/User"));
    Class<?> cls = utils.getParameterSignature(parameter, definitions);
    
    assertEquals(cls, User.class);
  }

  @Test
  public void testConvertComplexBodyParamWithoutConfigMapping() throws Exception {
    Map<String, Model> definitions = new HashMap<String, Model>();

    Parameter parameter = new BodyParameter().schema(new RefModel("#/definitions/Person"));
    Class<?> cls = utils.getParameterSignature(parameter, definitions);

    // will look up from the config model package and ref.simpleName of Person
    assertEquals(cls, Person.class);
  }

  @Test
  public void testConvertArrayBodyParam() throws Exception {
    Map<String, Model> definitions = new HashMap<String, Model>();

    Parameter parameter = new BodyParameter()
      .schema(new ArrayModel()
        .items(new RefProperty("#/definitions/Person")));

    Class<?> cls = utils.getParameterSignature(parameter, definitions);

    // will look up from the config model package and ref.simpleName of Person
    assertEquals(cls, List.class);
  }
}
