/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Workspace {

  // should probably make an adapter for a POM
  // coordinates
  // project directories
  //   - resolve the artifacts in the POM
  //   - resolve target/classes
  // directories
  // pom.xml files

  private List<File> projectDirectories;

  public Workspace() {
    this.projectDirectories = new ArrayList<File>();
  }

  public void addProjectDirectory(File directory) {
    projectDirectories.add(directory);    
  }
  
  public List<File> getProjectDirectories() {
    return projectDirectories;
  }
}
