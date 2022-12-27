package ru.practicum.view.exception;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (409 == response.status()) {
            return new RetryableException(409, response.reason(), response.request().httpMethod(),
                    null, response.request());
        }
        return defaultErrorDecoder.decode(s, response);
    }
}
