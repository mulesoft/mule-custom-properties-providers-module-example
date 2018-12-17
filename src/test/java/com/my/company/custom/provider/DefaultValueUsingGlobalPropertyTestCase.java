package com.my.company.custom.provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

public class DefaultValueUsingGlobalPropertyTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in the tests, this file lives in the test
   * resources.
   */
  @Override
  protected String getConfigFile() {
    return "default-values-mule-config.xml";
  }

  @Inject
  @Named("testObject")
  private TestObject testObject;

  @Inject
  @Named("testObjectUsingDefaultValueNotRedefinedInDevEnv")
  private TestObject testObjectUsingDefaultValue;

  @Test
  public void customPropertyProviderSuccessfullyConfigured() {
    assertThat(testObject.getValueFromProperty(), is("myPropertyValue"));
    assertThat(testObjectUsingDefaultValue.getValueFromProperty(), is("cDefaultValue"));
  }

}
