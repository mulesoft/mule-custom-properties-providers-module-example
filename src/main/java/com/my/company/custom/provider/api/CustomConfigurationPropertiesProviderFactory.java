/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.my.company.custom.provider.api;

import static com.my.company.custom.provider.api.CustomConfigurationPropertiesExtensionLoadingDelegate.CONFIG_ELEMENT;
import static com.my.company.custom.provider.api.CustomConfigurationPropertiesExtensionLoadingDelegate.EXTENSION_NAME;
import static org.mule.runtime.api.component.ComponentIdentifier.builder;
import static org.mule.runtime.extension.api.util.NameUtils.defaultNamespace;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.util.Optional;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class CustomConfigurationPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

  public static final String EXTENSION_NAMESPACE = defaultNamespace(EXTENSION_NAME);
  private static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER =
      builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();
  // TODO change to meaningful prefix
  private final static String CUSTOM_PROPERTIES_PREFIX = "custom-properties-provider::";
  private static final String TEST_KEY = "testKey";

  @Override
  public ComponentIdentifier getSupportedComponentIdentifier() {
    return CUSTOM_PROPERTIES_PROVIDER;
  }

  @Override
  public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
                                                        ResourceProvider externalResourceProvider) {

    // This is how you can access the configuration parameter of the <custom-properties-provider:config> element.
    String customParameterValue = parameters.getStringParameter("customParameter");

    return new ConfigurationPropertiesProvider() {

      @Override
      public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        // TODO change implementation to discover properties values from your custom source
        if (configurationAttributeKey.startsWith(CUSTOM_PROPERTIES_PREFIX)) {
          String effectiveKey = configurationAttributeKey.substring(CUSTOM_PROPERTIES_PREFIX.length());
          if (effectiveKey.equals(TEST_KEY)) {
            return Optional.of(new ConfigurationProperty() {

              @Override
              public Object getSource() {
                return "custom provider source";
              }

              @Override
              public Object getRawValue() {
                return customParameterValue;
              }

              @Override
              public String getKey() {
                return TEST_KEY;
              }
            });
          }
        }
        return Optional.empty();
      }

      @Override
      public String getDescription() {
        // TODO change to a meaningful name for error reporting.
        return "Custom properties provider";
      }
    };
  }

}
