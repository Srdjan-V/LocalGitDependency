package io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata;

import io.github.srdjanv.localgitdependency.Constants;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.common.Repository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.flatdir.FlatDirRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.ivy.IvyRepository;
import io.github.srdjanv.localgitdependency.persistence.data.probe.repositorydata.maven.MavenRepository;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class RepositoryDataParser {
    private RepositoryDataParser() {
    }

    public static List<Repository> create(Project project) {
        List<RepositoryWrapper> repositoryWrapperList = RepositoryWrapper.transform(project);

        List<Repository> repositoryList = new ArrayList<>();
        for (RepositoryWrapper repositoryWrapper : repositoryWrapperList) {
            switch (repositoryWrapper.getType()) {
                case Constants.Maven:
                    repositoryList.add(new MavenRepository(repositoryWrapper));
                    break;

                case Constants.Ivy:
                    repositoryList.add(new IvyRepository(repositoryWrapper));
                    break;

                case Constants.FlatDir:
                    repositoryList.add(new FlatDirRepository(repositoryWrapper));
                    break;

                default:
                    throw new IllegalStateException("Unknown repository type");
            }
        }

        return repositoryList;
    }

}
