package com.flodeb.payplug.client;

import com.flodeb.payplug.core.PayplugConfiguration;
import com.flodeb.payplug.exception.BadRequestException;
import com.flodeb.payplug.exception.ForbiddenException;
import com.flodeb.payplug.exception.HttpException;
import com.flodeb.payplug.exception.NotAllowedException;
import com.flodeb.payplug.exception.NotFoundException;
import com.flodeb.payplug.exception.PayplugException;
import com.flodeb.payplug.exception.ServerException;
import com.flodeb.payplug.exception.UnauthorizedException;
import com.flodeb.payplug.model.Payment;
import com.flodeb.payplug.model.Refund;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpClientTest {

    @InjectMocks
    private HttpClient httpClient;

    @Mock
    private RestTemplate restTemplate;

    private PayplugConfiguration conf;
    
    @Before
    public void setup() {
        conf = new PayplugConfiguration("8gf1fg7e1erg");
    }

    @Test
    public void shouldGetHeaders() {
        // Given

        // When
        HttpHeaders headers = httpClient.getHeaders("MyToken");

        // Then
        Assert.assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), headers.getAccept());
        Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        Assert.assertEquals("Bearer MyToken", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void shouldGetPayment() throws PayplugException {
        // Given
        String url = "https://payplug.com/payments/96281";
        Payment payment = new Payment();
        payment.setId("JFDFUND");

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Payment.class)))
                .thenReturn(ResponseEntity.ok(payment));

        // When
        Payment foundPayment = httpClient.get(conf, url, Payment.class);

        // Then
        Assert.assertEquals(payment, foundPayment);
    }

    @Test
    public void shouldGetRefund() throws PayplugException {
        // Given
        String url = "https://payplug.com/payments/96281/refunds";
        Refund refund = new Refund();
        refund.setId("PAMWNDY");

        Map<String, String> params = new HashMap<>();
        params.put("paymentId", "96281");

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Refund.class), eq(params)))
                .thenReturn(ResponseEntity.ok(refund));

        // When
        Refund foundRefund = httpClient.get(conf, url, params, Refund.class);

        // Then
        Assert.assertEquals(refund, foundRefund);
    }

    @Test
    public void shouldPostPayment() throws PayplugException {
        // Given
        String url = "https://payplug.com/payments/96281/refunds";
        Payment payment = new Payment();

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Payment.class)))
                .thenReturn(ResponseEntity.ok(payment));

        // When
        Payment createdPayment = httpClient.post(conf, url, payment, Payment.class);

        // Then
        Assert.assertEquals(payment, createdPayment);
    }

    @Test(expected = HttpException.class)
    public void shouldManageGetException() throws PayplugException {
        // Given
        String url = "https://payplug.com/payments/96281";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Payment.class)))
                .thenThrow(new RestClientException("Error"));

        // When
        httpClient.get(conf, url, Payment.class);

        // Then (exception)
    }

    @Test(expected = HttpException.class)
    public void shouldManagePostException() throws PayplugException {
        // Given
        String url = "https://payplug.com/payments/96281";
        Payment payment = new Payment();

        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(Payment.class)))
                .thenThrow(new RestClientException("Error"));

        // When
        httpClient.post(conf, url, payment, Payment.class);

        // Then (exception)
    }

    @Test(expected = BadRequestException.class)
    public void shouldManage400error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Then (exception)
    }

    @Test(expected = UnauthorizedException.class)
    public void shouldManage401error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        // Then (exception)
    }

    @Test(expected = ForbiddenException.class)
    public void shouldManage403error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        // Then (exception)
    }

    @Test(expected = NotFoundException.class)
    public void shouldManage404error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Then (exception)
    }

    @Test(expected = NotAllowedException.class)
    public void shouldManage405error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));

        // Then (exception)
    }

    @Test(expected = ServerException.class)
    public void shouldManage500error() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Then (exception)
    }

    @Test(expected = HttpException.class)
    public void shouldManageUnexceptedHttpError() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT));

        // Then (exception)
    }


    @Test(expected = HttpException.class)
    public void shouldManageOtherError() throws PayplugException {
        // Given

        // When
        httpClient.manageException(new RestClientException("Error"));

        // Then (exception)
    }
}