/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.my.company.custom.provider.api;

import static java.util.Collections.emptyList;
import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.runtime.api.meta.model.parameter.ParameterRole.BEHAVIOUR;
import org.mule.metadata.api.annotation.TypeAliasAnnotation;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.java.api.annotation.ClassInformationAnnotation;
import org.mule.runtime.api.meta.model.ParameterDslConfiguration;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.LayoutModel;
import org.mule.runtime.extension.api.declaration.type.annotation.LayoutTypeAnnotation;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

/**
 * Declares extension for Secure Properties Configuration module
 *
 * @since 1.0
 */
public class CustomConfigurationPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {

  // TODO replace with you extension name. This must be a meaningful name for this module.
    public static final String EXTENSION_NAME = "Custom Properties Provider";
    public static final String CONFIG_ELEMENT = "config";

  @Override
  public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
    ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
        .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
        .withCategory(SELECT)
        .onVersion("1.0.0")
        // TODO replace with you company name
        .fromVendor("WellsFargo")
        // This defines a global element in the extension with name config
        .withConfig(CONFIG_ELEMENT);

    ParameterGroupDeclarer defaultParameterGroup = configurationDeclarer.onDefaultParameterGroup();

    // TODO you can add/remove configuration parameter using the code below.
    defaultParameterGroup
        .withRequiredParameter("dataGridUrl").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
        .withExpressionSupport(NOT_SUPPORTED)
        .describedAs("Data Grid connection URL");

    defaultParameterGroup
            .withRequiredParameter("env").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
            .withExpressionSupport(NOT_SUPPORTED)
            .describedAs("The environment to use for the configuration properties group. For development purposes the default value can be defined using a global-property component and then override at deployment time with a system property or environment variable.");

    ParameterGroupDeclarer customPropertiesGroup = configurationDeclarer
            .onParameterGroup("Custom Properties Groups")
            .withLayout(LayoutModel.builder().order(2).build());

    ObjectTypeBuilder customProperty = BaseTypeBuilder.create(JAVA).objectType()
            .id(CustomProperty.class.getName())
            .with(new ClassInformationAnnotation(CustomProperty.class, emptyList()))
            .with(new TypeAliasAnnotation("CustomProperty"));

    customProperty.addField()
            .key("key")
            .description("The key to use to reference the custom property.")
            .value(BaseTypeBuilder.create(JAVA).stringType()).required(true)
            .build();

    customProperty.addField()
            .key("value")
            .description("The value associated to the key.")
            .value(BaseTypeBuilder.create(JAVA).stringType()).required(true)
            .with(new LayoutTypeAnnotation(LayoutModel.builder()
                                                   .build()))
            .build();

    ObjectTypeBuilder defaultCustomPropertyValue = BaseTypeBuilder.create(JAVA).objectType()
            .id(DefaultCustomPropertyValue.class.getName())
            .with(new ClassInformationAnnotation(DefaultCustomPropertyValue.class, emptyList()))
            .with(new TypeAliasAnnotation("DefaultValue"));

    defaultCustomPropertyValue.addField()
            .key("key")
            .description("The key to use to reference the custom property.")
            .value(BaseTypeBuilder.create(JAVA).stringType()).required(true)
            .build();

    defaultCustomPropertyValue.addField()
            .key("defaultValue")
            .description("The default value to use if there's no environment specific value.")
            .value(BaseTypeBuilder.create(JAVA).stringType()).required(true)
            .with(new LayoutTypeAnnotation(LayoutModel.builder()
                                                   .build()))
            .build();

      ObjectTypeBuilder customPropertiesTypeBuilder = BaseTypeBuilder.create(JAVA).objectType()
              .id(CustomPropertiesList.class.getName())
              .with(new ClassInformationAnnotation(CustomPropertiesList.class, emptyList()))
              .with(new TypeAliasAnnotation("CustomPropertiesGroup"));
      customPropertiesTypeBuilder
              .addField()
              .key("env")
              .description("The id of the environment that enables this set of custom properties.")
              .value(BaseTypeBuilder.create(JAVA).stringType()).required(true)
              .build();
      customPropertiesTypeBuilder
              .addField()
              .key("customProperties")
              .description("The id of the environment that enables this set of custom properties.")
              .value(BaseTypeBuilder.create(JAVA).arrayType().of(customProperty.build())).required(false)
              .build();

    customPropertiesGroup.withOptionalParameter("defaultValues")
            .ofType(BaseTypeBuilder.create(JAVA).arrayType().of(defaultCustomPropertyValue.build())
                            .build())
            .withDsl(ParameterDslConfiguration.builder()
                             .allowsInlineDefinition(true)
                             .allowsReferences(false)
                             .allowTopLevelDefinition(false)
                             .build())
            .withExpressionSupport(NOT_SUPPORTED)
            .withRole(BEHAVIOUR);

    customPropertiesGroup.withOptionalParameter("customPropertiesGroups")
            .ofType(BaseTypeBuilder.create(JAVA).arrayType().of(customPropertiesTypeBuilder.build())
                            .build())
            .withDsl(ParameterDslConfiguration.builder()
                             .allowsInlineDefinition(true)
                             .allowsReferences(false)
                             .allowTopLevelDefinition(false)
                             .build())
            .withExpressionSupport(NOT_SUPPORTED)
            .withRole(BEHAVIOUR);


  }

}
