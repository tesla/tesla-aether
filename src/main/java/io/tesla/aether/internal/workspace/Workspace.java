/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
