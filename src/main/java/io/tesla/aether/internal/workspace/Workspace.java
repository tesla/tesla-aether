package io.tesla.aether.internal.workspace;

import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class Workspace {

  private MavenXpp3Reader reader = new MavenXpp3Reader();

  public Workspace(File... poms) {

    for (File pom : poms) {
      
    }
  }

  private void read(File pom) throws Exception {
    if(belongsToMultiModuleProject(pom)) {
      
    }
  }
  
  private boolean belongsToMultiModuleProject(File pom) throws Exception {
    Model model = reader.read(new FileInputStream(pom));
    Parent parent = model.getParent();
    if (parent != null) {
      return new File(pom.getParentFile(), parent.getRelativePath()).exists();
    }
    return false;
  }
}
