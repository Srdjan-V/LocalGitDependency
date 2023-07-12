package io.github.srdjanv.localgitdependency.git;

import java.util.List;
import java.util.stream.Collectors;

public class GitException extends RuntimeException {
    private final List<List<Exception>> exceptions;

    public GitException(List<List<Exception>> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public String getMessage() {
        return exceptions.stream().
                flatMap(List::stream).
                map(Throwable::getMessage).
                collect(Collectors.joining(
                        System.lineSeparator(),
                        System.lineSeparator(),
                        ""));
    }
}
