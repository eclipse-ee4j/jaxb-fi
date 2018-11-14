/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;

/** Resource bundle implementation for localized messages.
 */
public class CommonResourceBundle extends AbstractResourceBundle {

    public static final String BASE_NAME = "com.sun.xml.fastinfoset.resources.ResourceBundle";
    private static volatile CommonResourceBundle instance = null;
    private static Locale locale = null;
    private ResourceBundle bundle = null;
    
    protected CommonResourceBundle() {
        // Load the resource bundle of default locale
        bundle = ResourceBundle.getBundle(BASE_NAME);
    }

    protected CommonResourceBundle(Locale locale) {
        // Load the resource bundle of specified locale
        bundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static CommonResourceBundle getInstance() {
        if (instance == null) {
            synchronized (CommonResourceBundle.class) {
                instance = new CommonResourceBundle();
                //**need to know where to get the locale
                //String localeString = CommonProperties.getInstance()
                //                  .getProperty("omar.common.locale");
                locale = parseLocale(/*localeString*/null);
            }
        }

        return instance;
    }
    
    public static CommonResourceBundle getInstance(Locale locale) {
        if (instance == null) {
            synchronized (CommonResourceBundle.class) {
                instance = new CommonResourceBundle(locale);
            }
        } else {
            synchronized (CommonResourceBundle.class) {
                if (CommonResourceBundle.locale != locale) {
                    instance = new CommonResourceBundle(locale);
                }
            }
	}
        return instance;
    }


    public ResourceBundle getBundle() {
        return bundle;
    }
    public ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BASE_NAME, locale);
    }
    
}
