package com.flodeb.payplug;

import com.flodeb.payplug.exception.PayplugException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateForNoContent() throws PayplugException {
        // Given

        // When
        paymentService.create(null, null);

        // Then
    }
}