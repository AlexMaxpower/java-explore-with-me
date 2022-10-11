package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForbiddenException extends IllegalArgumentException {
    public ForbiddenException(String message) {
        super(message);
        log.error(message);
    }
}