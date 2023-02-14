package srki2k.localgitdependency.injection.model;

import srki2k.localgitdependency.injection.model.imp.DefaultTaskObject;

import java.util.List;

public interface PublicationObject {
    String getPublicationName();
    List<DefaultTaskObject> getTasks();
}
