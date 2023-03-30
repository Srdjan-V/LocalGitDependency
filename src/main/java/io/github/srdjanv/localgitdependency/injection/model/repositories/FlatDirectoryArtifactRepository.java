package io.github.srdjanv.localgitdependency.injection.model.repositories;

import java.io.File;
import java.util.Set;

public interface FlatDirectoryArtifactRepository extends ArtifactRepository {
    Set<File> getDirs();
}
