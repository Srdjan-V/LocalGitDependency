package srki2k.localgitdependency.injection.model.imp;

import srki2k.localgitdependency.injection.model.LocalGitDependencyInfoModel;
import srki2k.localgitdependency.injection.model.PublicationObject;
import srki2k.localgitdependency.injection.model.TaskObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultPublicationObject implements PublicationObject, Serializable {
    public static long serialVersionUID = LocalGitDependencyInfoModel.serialVersionUID;
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
    public List<TaskObject> getTasks() {
        return tasks.stream().map(defaultTaskObject -> (TaskObject) defaultTaskObject).collect(Collectors.toList());
    }
}
