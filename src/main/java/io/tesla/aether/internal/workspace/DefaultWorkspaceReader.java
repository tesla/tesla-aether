/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
