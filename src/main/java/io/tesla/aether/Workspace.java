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
