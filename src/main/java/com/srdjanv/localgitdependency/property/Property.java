package com.srdjanv.localgitdependency.property;

//Property's that only a dependency can have
public class Property extends CommonPropertyGetters {
    private final String url;
    private final String name;
    private final String commit;

    public Property(Builder builder) {
        url = builder.url;
        name = builder.name;
        commit = builder.commit;
        PropertyManager.instantiateCommonPropertyFieldsInstance(this, builder);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getCommit() {
        return commit;
    }

    public static class Builder extends CommonPropertyBuilder {
        private final String url;
        private String name;
        private String commit;

        public Builder(String url) {
            this.url = url;
        }

        public void name(String name) {
            this.name = name;
        }



        public void commit(String commit) {
            this.commit = commit;
        }

        public void branch(String branch) {
            this.commit = branch;
        }

        public void tag(String tag) {
            this.commit = tag;
        }
    }
}
