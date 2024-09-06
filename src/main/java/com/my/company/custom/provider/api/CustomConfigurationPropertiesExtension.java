/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.my.company.custom.provider.api;

import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.sdk.api.annotation.JavaVersionSupport;

/**
 * Declares extension for Custom Properties Configuration module
 *
 * @since 1.0
 */
@Extension(name = CustomConfigurationPropertiesExtension.EXTENSION_NAME,
           category = SELECT,
           vendor = "MyCompany")
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
@Export(classes = CustomConfigurationPropertiesProviderFactory.class,
        resources = {"META-INF/services/org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory"})
public class CustomConfigurationPropertiesExtension {

  // TODO replace with you extension name. This must be a meaningful name for this module.
  public static final String EXTENSION_NAME = "Custom Properties Provider";

  // TODO you can add/remove configuration parameter using the code below.
  @Parameter
  @Alias(value="customParameter", description = "Meaningful description of what customParameter is for")
  @Expression(NOT_SUPPORTED)
  private String customParameter;
}
