package io.github.srdjanv.localgitdependency.property;

import io.github.srdjanv.localgitdependency.git.GitInfo;

//Property's that only a dependency can have
public class Property extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String target;
    private final GitInfo.TargetType targetType;
    public Property(Builder builder) {
        url = builder.url;
        name = builder.name;
        target = builder.target;
        targetType = builder.targetType;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public GitInfo.TargetType getTargetType() {
        return targetType;
    }

    public static class Builder extends CommonPropertyBuilder {
        private final String url;
        private String name;
        private String target;
        private GitInfo.TargetType targetType;

        public Builder(String url) {
            this.url = url;
        }

        public void name(String name) {
            this.name = name;
        }

        public void commit(String commit) {
            targetType = GitInfo.TargetType.COMMIT;
            this.target = commit;
        }

        public void branch(String branch) {
            targetType = GitInfo.TargetType.BRANCH;
            this.target = branch;
        }

        public void tag(String tag) {
            targetType = GitInfo.TargetType.TAG;
            this.target = tag;
        }
    }
}
