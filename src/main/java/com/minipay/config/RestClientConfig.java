package com.minipay.config;

import com.minipay.config.error.CustomResponseErrorHandler;
import com.minipay.config.interceptors.HeaderRequestInterceptor;
import com.minipay.config.interceptors.RequestLogInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RestClientConfig {
    @Value(("${rest.read-timeout:60000}"))
    private int readTimeout;

    @Value(("${rest.connect-timeout:60000}"))
    private int connectTimeout;


    @Bean
    public RestTemplate restTemplate() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RequestLogInterceptor());
        interceptors.add(new HeaderRequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        final SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(readTimeout);
        simpleClientHttpRequestFactory.setConnectTimeout(connectTimeout);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.ALL));

        final RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        restTemplate.setInterceptors(interceptors);
        restTemplate.getMessageConverters().add(0, converter);
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());

        return restTemplate;
    }
}
