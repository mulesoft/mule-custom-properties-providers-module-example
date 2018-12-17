/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.my.company.custom.provider.api;

import java.util.List;

public class CustomPropertiesList
{

    private String env;

    private List<CustomProperty> customProperties;

    public String getEnv()
    {
        return env;
    }

    public void setEnv(String env)
    {
        this.env = env;
    }

    public List<CustomProperty> getCustomProperties()
    {
        return customProperties;
    }

    public void setCustomProperties(List<CustomProperty> customProperties)
    {
        this.customProperties = customProperties;
    }
}
