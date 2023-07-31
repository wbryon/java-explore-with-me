package ru.practicum.ewm.exception;

public class WrongRequestException extends RuntimeException {
    public WrongRequestException(final String message) {
        super(message);
    }
}
