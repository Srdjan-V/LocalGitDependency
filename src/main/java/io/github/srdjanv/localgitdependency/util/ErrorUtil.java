package io.github.srdjanv.localgitdependency.util;

import io.github.srdjanv.localgitdependency.Constants;
import org.gradle.api.GradleException;

import java.util.List;

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

    public ErrorUtil append(String error) {
        getBuilder().append(Constants.TAB_INDENT).append(error).append(System.lineSeparator());
        return this;
    }

    public ErrorUtil append(List<String> errors) {
        StringBuilder builder = null;
        if (!errors.isEmpty()) builder = getBuilder();

        for (String error : errors) {
            builder.append(Constants.TAB_INDENT).append(error).append(System.lineSeparator());
        }
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

    public RuntimeException toRuntimeException() {
        if (hasErrors()) return new RuntimeException(errors.toString());
        throw new IllegalStateException("This should not be possible");
    }

    public GradleException toGradleException() {
        if (hasErrors()) return new GradleException(errors.toString());
        throw new IllegalStateException("This should not be possible");
    }

}
