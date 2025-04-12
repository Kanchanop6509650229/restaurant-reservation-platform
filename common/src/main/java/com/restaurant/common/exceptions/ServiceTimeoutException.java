package com.restaurant.common.exceptions;

/**
 * Exception thrown when a service operation times out.
 * This exception is used when an asynchronous operation does not complete
 * within the expected time frame.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ServiceTimeoutException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ServiceTimeoutException with the specified detail message.
     *
     * @param message The detail message
     */
    public ServiceTimeoutException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceTimeoutException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public ServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
