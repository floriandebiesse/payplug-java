package com.flodeb.payplug;

import com.flodeb.payplug.exception.PayplugException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotTreatForNotContent() throws PayplugException {
        // Given

        // When
        notificationService.treat(null, null);

        // Then
    }
}