/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.odnoklassniki.api.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.odnoklassniki.api.Odnoklassniki;
import org.springframework.social.odnoklassniki.api.OdnoklassnikiErrorHandler;
import org.springframework.social.odnoklassniki.api.UsersOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Odnoklassniki template
 * @author Cackle
 */
public class OdnoklassnikiTemplate extends AbstractOAuth2ApiBinding implements Odnoklassniki {

    private UsersOperations usersOperations;

    private final String applicationKey;

    private final String clientSecret;

    private final String accessToken;

    public OdnoklassnikiTemplate(String applicationKey, String clientSecret, String accessToken) {
        super(accessToken);
        this.applicationKey = applicationKey;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        initialize();
    }

    private void initialize() {
        registerJsonModule();
        getRestTemplate().setErrorHandler(new OdnoklassnikiErrorHandler());
        initSubApis();
    }

    private void initSubApis() {
        usersOperations = new UsersTemplate(applicationKey, clientSecret, getRestTemplate(), accessToken, isAuthorized());
    }

    private void registerJsonModule() {
        List<HttpMessageConverter<?>> converters = getRestTemplate().getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
            	MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;

                List<MediaType> mTypes = new LinkedList<MediaType>(jsonConverter.getSupportedMediaTypes());
                mTypes.add(new MediaType("text", "javascript", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET));
                jsonConverter.setSupportedMediaTypes(mTypes);

                ObjectMapper objectMapper = new ObjectMapper();
                jsonConverter.setObjectMapper(objectMapper);
            }
        }
    }

    @Override
    public UsersOperations usersOperations() {
        return usersOperations;
    }
}
