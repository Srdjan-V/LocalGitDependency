package io.github.srdjanv.localgitdependency.config.dependency;

public interface SourceSetMapperBuilder {
    /**
     * This is used to map SourceSet's of a dependency to a SourceSet on the current project
     * <pre>
     * mapSourceSets({
     *    map "main", "example"
     *    }, {
     *    map "test", "example", "example2"
     * })
     * </pre>
     *
     * @param projectSet    SourceSet name of the current project
     * @param dependencySet SourceSet's names to inherit from
     */
    void map(String projectSet, String... dependencySet);

    /**
     * If the specified dependencySet are going to get added recursively
     * <p>
     * Default true
     *
     * @param recursive if it should be handled recursively
     */
    void recursive(Boolean recursive);
}
