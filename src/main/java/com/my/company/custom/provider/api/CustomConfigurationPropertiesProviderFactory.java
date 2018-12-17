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
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class CustomConfigurationPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory
{

    public static final String EXTENSION_NAMESPACE =
            EXTENSION_NAME.toLowerCase().replace(" ", "-");
    private static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER =
            builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();
    // TODO change to meaningful prefix
    private final static String CUSTOM_PROPERTIES_NAMESPACE = "custom-properties-provider";
    private final static String CUSTOM_PROPERTIES_PREFIX = CUSTOM_PROPERTIES_NAMESPACE + "::";
    private static final String TEST_KEY = "testKey";

    @Override
    public ComponentIdentifier getSupportedComponentIdentifier()
    {
        return CUSTOM_PROPERTIES_PROVIDER;
    }

    @Override
    public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
                                                          ResourceProvider externalResourceProvider)
    {

        // This is how you can access the configuration parameter of the <custom-properties-provider:config> element.
        String dataGridUrl = parameters.getStringParameter("dataGridUrl");
        String env = parameters.getStringParameter("env");

        // For instance, I could use the customParameter value as configuration to access the DataGrid.
        // If you need additional parameters in the Mule XML configuration you can add the in CustomConfigurationPropertiesExtensionLoadingDelegate
        DataGridCluster dataGridCluster = new DataGridCluster(dataGridUrl);

        //Extracts the values of the custom properties defined in the XML configuration
        Map<String, String> customProperties = getCustomPropertiesDefinedInMuleXml(parameters, env);


        return new ConfigurationPropertiesProvider()
        {

            @Override
            public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey)
            {
                // TODO change implementation to discover properties values from your custom source
                if (configurationAttributeKey.startsWith(CUSTOM_PROPERTIES_PREFIX))
                {
                    String effectiveKey = configurationAttributeKey.substring(CUSTOM_PROPERTIES_PREFIX.length());
                    String propertyValue;
                    // This should be modified to use the different sources and also defined precedence between them.
                    // 1-APP CONFIG 2-CUSTOM PROPS 3-TRADESOURCE CONFIG
                    propertyValue = customProperties.get(effectiveKey);
                    if (propertyValue == null)
                    {
                        propertyValue = dataGridCluster.getValue(effectiveKey);
                    }
                    if (propertyValue != null)
                    {
                        String finalPropertyValue = propertyValue;
                        return Optional.of(new ConfigurationProperty()
                        {

                            @Override
                            public Object getSource()
                            {
                                return "custom provider source";
                            }

                            @Override
                            public Object getRawValue()
                            {
                                return finalPropertyValue;
                            }

                            @Override
                            public String getKey()
                            {
                                return effectiveKey;
                            }
                        });
                    }
                }
                return Optional.empty();
            }

            @Override
            public String getDescription()
            {
                // TODO change to a meaningful name for error reporting.
                return "Custom properties provider";
            }
        };
    }

    private Map<String, String> getCustomPropertiesDefinedInMuleXml(ConfigurationParameters parameters, String selectedEnvironment)
    {
        Map<String, String> customProperties = new HashMap<>();

        //process default values
        List<ConfigurationParameters> defaultValuesConfigParams = parameters.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("default-values").build());
        if (defaultValuesConfigParams != null && !defaultValuesConfigParams.isEmpty())
        {
            ConfigurationParameters defauleValuesConfigParam = defaultValuesConfigParams.get(0);
            List<ConfigurationParameters> defaultValuesListConfigParam = defauleValuesConfigParam.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("default-value").build());
            for (ConfigurationParameters defaultValueConfigParam : defaultValuesListConfigParam)
            {
                customProperties.put(defaultValueConfigParam.getStringParameter("key"), defaultValueConfigParam.getStringParameter("defaultValue"));
            }
        }

        //process env specific properties
        List<ConfigurationParameters> customPropertiesGroupsConfigParams = parameters.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("custom-properties-groups").build());
        if (customPropertiesGroupsConfigParams != null && !customPropertiesGroupsConfigParams.isEmpty())
        {
            ConfigurationParameters customPropertiesGroupsConfigParam = customPropertiesGroupsConfigParams.get(0);
            List<ConfigurationParameters> customPropertyGroupsGroupsParam = customPropertiesGroupsConfigParam.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("custom-properties-group").build());
            for (ConfigurationParameters customPropertyGroup : customPropertyGroupsGroupsParam)
            {
                if (customPropertyGroup.getStringParameter("env").equalsIgnoreCase(selectedEnvironment))
                {
                    List<ConfigurationParameters> customPropertiesConfigParam = customPropertyGroup.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("custom-properties").build());
                    if (customPropertiesConfigParam != null && !customPropertiesConfigParam.isEmpty())
                    {
                        ConfigurationParameters configurationParameters = customPropertiesConfigParam.get(0);
                        List<ConfigurationParameters> customPropertiesConfParams = configurationParameters.getComplexConfigurationParameter(builder().namespace(CUSTOM_PROPERTIES_NAMESPACE).name("custom-property").build());
                        for (ConfigurationParameters customPropertiesConfParam : customPropertiesConfParams)
                        {
                            customProperties.put(customPropertiesConfParam.getStringParameter("key"), customPropertiesConfParam.getStringParameter("value"));
                        }
                    }
                }
            }
        }
        return customProperties;
    }


    /**
     * This class mimics the behaviour of the data grid.
     */
    public static class DataGridCluster
    {

        public DataGridCluster(String dataGridUrl)
        {
            //Code to connect to the DataGrid
        }

        public String getValue(String key)
        {
            if (key.startsWith("cacheProperty"))
            {
                return key + "Value";
            }
            return null;
        }

    }

}
