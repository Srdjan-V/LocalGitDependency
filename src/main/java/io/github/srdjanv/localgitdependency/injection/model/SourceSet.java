package io.github.srdjanv.localgitdependency.injection.model;

import java.util.List;

public interface SourceSet {
    String getName();
    List<String> getSources();
}