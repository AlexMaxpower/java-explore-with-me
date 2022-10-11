package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlreadyExistsException extends IllegalArgumentException {

    public AlreadyExistsException(String message) {
        super(message);
        log.error(message);
    }
}
