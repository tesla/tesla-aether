package io.tesla.aether.internal.workspace;

import java.io.File;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;

public class DefaultWorkspaceReader implements WorkspaceReader {

  // - main()
  //   - plugin manager
  
  public WorkspaceRepository getRepository() {
    return new WorkspaceRepository();
  }

  public File findArtifact(Artifact artifact) {
    return null;
  }

  public List<String> findVersions(Artifact artifact) {
    return null;
  }

}
