/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.my.company.custom.provider.api;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

import static java.util.Arrays.asList;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.extension.api.exception.IllegalModelDefinitionException;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Declares extension for Secure Properties Configuration module
 *
 * @since 1.0
 */
public class CustomConfigurationPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {

  // TODO replace with you extension name. This must be a meaningful name for this module.
  public static final String EXTENSION_NAME = "Custom Properties Provider";
  public static final String CONFIG_ELEMENT = "config";

  // We need to use reflection to avoid having to bump the minMuleVersion of this extension
  // If that is not an issue then this code can be simplified a lot by upgrading the modules-parent to at least 1.5.0
  private static final Method SUPPORTING_JAVA_VERSIONS_METHOD = getSupportingJavaVersionsMethod();

  private static Method getSupportingJavaVersionsMethod() {
    try {
      return ExtensionDeclarer.class.getMethod("supportingJavaVersions", Set.class);
    } catch (NoSuchMethodException e) {
      // The method doesn't exist in this version: the Runtime we are running with doesn't care about supported Java versions
      // declaration
      return null;
    }
  }

  @Override
  public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
    declareJavaVersionSupport(extensionDeclarer, new HashSet<>(asList(JAVA_8.version(), JAVA_11.version(), JAVA_17.version())));

    ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
        .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
        .withCategory(SELECT)
        .onVersion("1.0.0")
        // TODO replace with you company name
        .fromVendor("MyCompany")
        // This defines a global element in the extension with name config
        .withConfig(CONFIG_ELEMENT);

    ParameterGroupDeclarer defaultParameterGroup = configurationDeclarer.onDefaultParameterGroup();
    // TODO you can add/remove configuration parameter using the code below.
    defaultParameterGroup
        .withRequiredParameter("customParameter").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
        .withExpressionSupport(NOT_SUPPORTED)
        .describedAs(" Meaningful description of what customParameter is for");
  }

  private void declareJavaVersionSupport(ExtensionDeclarer extensionDeclarer, Set<String> javaVersions) {
    if (SUPPORTING_JAVA_VERSIONS_METHOD == null) {
      // Nothing to do, the Runtime we are running with doesn't care about supported Java versions declaration
      return;
    }

    try {
      SUPPORTING_JAVA_VERSIONS_METHOD.invoke(extensionDeclarer, javaVersions);
    } catch (IllegalAccessException e) {
      throw new IllegalModelDefinitionException(e);
    } catch (InvocationTargetException e) {
      throw new IllegalModelDefinitionException(e.getTargetException());
    }
  }

}
