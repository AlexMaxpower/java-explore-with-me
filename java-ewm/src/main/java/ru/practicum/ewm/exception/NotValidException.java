package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotValidException extends IllegalArgumentException {
    public NotValidException(String message) {
        super(message);
        log.error(message);
    }
}