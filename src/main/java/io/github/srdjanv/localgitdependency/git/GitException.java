package io.github.srdjanv.localgitdependency.git;

import io.github.srdjanv.localgitdependency.Constants;

import java.util.List;
import java.util.Map;

public class GitException extends RuntimeException {
    private final Map<String, List<Exception>> exceptions;

    public GitException(Map<String, List<Exception>> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<Exception>> exceptionEntry : exceptions.entrySet()) {
            builder.append(exceptionEntry.getKey()).append(":");
            for (Exception exception : exceptionEntry.getValue()) {
                builder.append(Constants.TAB_INDENT).append(exception);
            }
        }

        return builder.toString();
    }
}
