package io.github.srdjanv.localgitdependency.util;

public final class ErrorUtil {
    private final String message;
    private StringBuilder errors;

    private ErrorUtil(String message) {
        this.message = message;
    }

    public static ErrorUtil create(String message) {
        return new ErrorUtil(message);
    }

    public StringBuilder getBuilder() {
        if (errors == null) {
            errors = new StringBuilder(message).append(System.lineSeparator());
        }
        return errors;
    }

    public ErrorUtil append(String s) {
        getBuilder().append(s).append(System.lineSeparator());
        return this;
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public String getMessage() {
        if (hasErrors())
            return errors.toString();
        return null;
    }
}
