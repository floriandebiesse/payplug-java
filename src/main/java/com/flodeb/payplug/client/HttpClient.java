package com.flodeb.payplug.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.flodeb.payplug.core.PayplugConfiguration;
import com.flodeb.payplug.exception.BadRequestException;
import com.flodeb.payplug.exception.ForbiddenException;
import com.flodeb.payplug.exception.HttpException;
import com.flodeb.payplug.exception.NotAllowedException;
import com.flodeb.payplug.exception.NotFoundException;
import com.flodeb.payplug.exception.PayplugException;
import com.flodeb.payplug.exception.ServerException;
import com.flodeb.payplug.exception.UnauthorizedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private final RestTemplate restTemplate;

    public HttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        restTemplate.setRequestFactory(factory);
        restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonMessageConverter);

        restTemplate.setMessageConverters(messageConverters);
    }

    public <T> T get(PayplugConfiguration configuration, String url, Class<T> className) throws PayplugException {
        return get(configuration, url, null, className);
    }

    public <T> T get(PayplugConfiguration configuration,
                     String url,
                     Map<String, String> params,
                     Class<T> className) throws PayplugException {
        HttpHeaders headers = getHeaders(configuration.getToken());
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            if (params != null) {
                return restTemplate.exchange(url, HttpMethod.GET, request, className, params).getBody();
            }

            return restTemplate.exchange(url, HttpMethod.GET, request, className).getBody();
        }
        catch (RestClientException e) {
            return manageException(e);
        }
    }

    public <T> T post(PayplugConfiguration configuration, String url, T data, Class<T> className) throws PayplugException {
        HttpHeaders headers = getHeaders(configuration.getToken());
        HttpEntity<T> request = new HttpEntity<>(data, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, request, className).getBody();
        }
        catch (RestClientException e) {
            return manageException(e);
        }
    }

    HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return headers;
    }

    <T> T manageException(RestClientException e) throws PayplugException {
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) e;

            if (HttpStatus.BAD_REQUEST.equals(httpEx.getStatusCode())) {
                throw new BadRequestException();
            }
            if (HttpStatus.UNAUTHORIZED.equals(httpEx.getStatusCode())) {
                throw new UnauthorizedException();
            }
            if (HttpStatus.FORBIDDEN.equals(httpEx.getStatusCode())) {
                throw new ForbiddenException();
            }
            if (HttpStatus.NOT_FOUND.equals(httpEx.getStatusCode())) {
                throw new NotFoundException();
            }
            if (HttpStatus.METHOD_NOT_ALLOWED.equals(httpEx.getStatusCode())) {
                throw new NotAllowedException();
            }
        }
        else if (e instanceof HttpServerErrorException) {
            throw new ServerException(e.getMessage());
        }

        throw new HttpException(e.getMessage());
    }
}
