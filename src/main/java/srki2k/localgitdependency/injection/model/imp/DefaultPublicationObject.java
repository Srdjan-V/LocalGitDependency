package srki2k.localgitdependency.injection.model.imp;

import srki2k.localgitdependency.injection.model.PublicationObject;

import java.io.Serializable;
import java.util.List;

public class DefaultPublicationObject implements PublicationObject, Serializable {
    public static final long serialVersionUID = DefaultLocalGitDependencyInfoModel.serialVersionUID;
    private final String publicationName;
    private final List<DefaultTaskObject> tasks;

    public DefaultPublicationObject(String publicationName, List<DefaultTaskObject> tasks) {
        this.publicationName = publicationName;
        this.tasks = tasks;
    }

    @Override
    public String getPublicationName() {
        return publicationName;
    }

    @Override
    public List<DefaultTaskObject> getTasks() {
        return tasks;
    }
}
