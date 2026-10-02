package com.flodeb.payplug.resource.factory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.flodeb.payplug.exception.PayplugException;
import com.flodeb.payplug.exception.UnknownAPIResourceException;
import com.flodeb.payplug.model.Payment;
import com.flodeb.payplug.model.Refund;
import com.flodeb.payplug.model.Resource;
import com.flodeb.payplug.model.enums.Type;
import com.flodeb.payplug.resource.IAPIResource;
import com.flodeb.payplug.resource.PaymentResource;
import com.flodeb.payplug.resource.RefundResource;

import java.io.IOException;

public class APIResourceFactory {
    private final PaymentResource paymentResource;

    private final RefundResource refundResource;

    public APIResourceFactory(PaymentResource paymentResource, RefundResource refundResource) {
        this.paymentResource = paymentResource;
        this.refundResource = refundResource;
    }

    public IAPIResource getResource(String content) throws PayplugException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try {
            Resource resource = objectMapper.readValue(content, Resource.class);
            if (Type.PAYMENT.equals(resource.getObject())) {
                paymentResource.setPayment(objectMapper.readValue(content, Payment.class));
                return paymentResource;
            }
            else if (Type.REFUND.equals(resource.getObject())) {
                refundResource.setRefund(objectMapper.readValue(content, Refund.class));
                return refundResource;
            }
        } catch (IOException e) {
            throw new UnknownAPIResourceException("Invalid JSON request " + e.getMessage());
        }

        throw new UnknownAPIResourceException("Unknown object type");
    }
}
