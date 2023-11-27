package edu.carroll.bankapp.service;

/**
 * This class serves to provide more feedback from a service by adding a message field.
 *
 * @param <T>
 */
public class ServiceResponse<T> {
    // Whatever the service returns, a boolean, a model, some other object
    private T result;
    // A message to the user about how the operation succeeded or failed
    private String message;

    /**
     * Create a ServiceResponse
     *
     * @param result  - Any object the service should return as a result of it's operation
     * @param message - A message to the user about that operation
     */
    public ServiceResponse(T result, String message) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
