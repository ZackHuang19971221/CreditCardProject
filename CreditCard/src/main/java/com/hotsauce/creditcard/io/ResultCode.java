package com.hotsauce.creditcard.io;

/**
 * Enumerates the various result codes for credit card operations.
 */
public enum ResultCode {
    SUCCESS("0000", "Operation completed successfully."),
    INPUT_ERROR("1000", "Input error, check request format."),
    TIME_OUT("2000","time out"),
    NOT_SUPPORTED("6000", "Operation not supported."),
    NETWORK_ERROR("7000", "Network error, failed to connect."),
    PROVIDER_ERROR("8000", "Error returned by the provider."),
    SYSTEM_ERROR("9999", "System error, contact support.");

    private final String code;
    private final String description;

    /**
     * Constructs a ResultCode enum instance.
     *
     * @param code        the code representing the result.
     * @param description a description of what the code means.
     */
    ResultCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the result code as a string.
     *
     * @return the result code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the description of the result code.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }
}
