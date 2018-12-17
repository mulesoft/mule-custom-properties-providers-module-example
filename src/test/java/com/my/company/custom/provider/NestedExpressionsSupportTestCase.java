package com.my.company.custom.provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

public class NestedExpressionsSupportTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test
   * resources.
   */
  @Override
  protected String getConfigFile() {
    return "nested-expressions-support-mule-config.xml";
  }

  @Inject @Named("testObject")
  private TestObject testObject;

  @Inject @Named("testObject2") TestObject testObject2;

  @Test
  public void customPropertyProviderSuccessfullyConfigured() {
    assertThat(testObject.getValueFromProperty(), is("myPropertyValue-myProperty2Value"));
    assertThat(testObject2.getValueFromProperty(), is("customPropAValue-customPropBValue"));
  }

}
