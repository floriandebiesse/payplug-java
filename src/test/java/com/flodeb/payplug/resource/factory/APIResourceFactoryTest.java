package com.flodeb.payplug.resource.factory;

import com.flodeb.payplug.exception.PayplugException;
import com.flodeb.payplug.exception.UnknownAPIResourceException;
import com.flodeb.payplug.resource.IAPIResource;
import com.flodeb.payplug.resource.PaymentResource;
import com.flodeb.payplug.resource.RefundResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class APIResourceFactoryTest {

    @InjectMocks
    private APIResourceFactory apiResourceFactory;

    @Mock
    private PaymentResource paymentResource;

    @Mock
    private RefundResource refundResource;

    @Test(expected = UnknownAPIResourceException.class)
    public void shouldNotTreatNotificationForInvalidRequest() throws PayplugException {
        // Given
        String content = "Invalid";

        // When
        apiResourceFactory.getResource(content);

        // Then (exception)
    }

    @Test
    public void shouldTreatPayment() throws PayplugException {
        // Given
        String content = "{\n" +
                "  \"id\": \"pay_3lcxJF5pPOonymu9Mxvhvu\",\n" +
                "  \"payment_id\": \"pay_\",\n" +
                "  \"object\": \"payment\",\n" +
                "  \"is_live\": false,\n" +
                "  \"amount\": 358,\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"created_at\": 1434012358,\n" +
                "  \"metadata\": {\n" +
                "    \"customer_id\": 42710,\n" +
                "    \"reason\": \"The delivery was delayed\"\n" +
                "  }\n" +
                "}";

        // When
        IAPIResource resource = apiResourceFactory.getResource(content);

        // Then
        Assert.assertTrue(resource instanceof PaymentResource);
    }

    @Test
    public void shouldTreatRefund() throws PayplugException {
        // Given
        String content = "{ \"amount\": 358,\n" +
                "  \"created_at\": 1434012358,\n" +
                "  \"currency\": \"EUR\",\n" +
                "  \"id\": \"re_3NxGqPfSGMHQgLSZH0Mv3B\",\n" +
                "  \"is_live\": true,\n" +
                "  \"metadata\": { \n" +
                "    \"customer_id\": 42,\n" +
                "    \"reason\": \"The delivery was delayed\"\n" +
                "  },\n" +
                "  \"object\": \"REFUND\",\n" +
                "  \"payment_id\": \"pay_5iHMDxy4ABR4YBVW4UscIn\"\n" +
                "}";

        // When
        IAPIResource resource = apiResourceFactory.getResource(content);

        // Then
        Assert.assertTrue(resource instanceof RefundResource);
    }
}