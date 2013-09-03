/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether;

import io.tesla.aether.internal.DefaultTeslaAether;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.sisu.containers.InjectedTestCase;

//
// For our tests we have a file based remote repository that has an intact transitive hull where everythign
// can be resolved with the the file based remote repository.
//
public class TeslaAetherTest extends InjectedTestCase {

  @Inject
  @Named("${basedir}/target/localRepository")
  private File localRepository;

  @Inject
  @Named("${basedir}/src/test/maven2")
  private File remoteRepository;

  private TeslaAether aether;

  public void setUp() throws Exception {
    super.setUp();
    aether = new DefaultTeslaAether(localRepository, remoteRepository.toURI().toURL().toExternalForm());
  }

  public void testResolvingPomFileWhichHasAParentInARemoteRepository() throws Exception {
    File modelFile = new File(remoteRepository, "io/tesla/maven/maven-core/3.1.0/maven-core-3.1.0.pom");
    Model model = aether.resolveModel(modelFile);
    assertEquals("users-subscribe@maven.apache.org", model.getMailingLists().get(0).getSubscribe());
  }

  public void testResolvingArtifactsUsingCoordinate() throws Exception {
    List<Artifact> artifacts = aether.resolveArtifacts("io.tesla.maven:maven-core:3.1.0");
    assertEquals(29, artifacts.size());
  }

  public void testResolvingArtifactsUsingArtifact() throws Exception {
    List<Artifact> artifacts = aether.resolveArtifacts(new DefaultArtifact("io.tesla.maven:maven-core:3.1.0"));
    assertEquals(29, artifacts.size());
  }

  public void testResolvingDependenciesOfAModel() throws Exception {
    File modelFile = new File(remoteRepository, "io/tesla/maven/maven-core/3.1.0/maven-core-3.1.0.pom");
    List<Artifact> artifacts = aether.resolveArtifacts(modelFile);
    // This is the same because right now the model itself it added to result which is not what will
    // happen if we're doing workspace resolution
    assertEquals(29, artifacts.size());
    for (Artifact artifact : artifacts) {
      assertNotNull(artifact.getFile());
    }
  }

//  public void testResolvingModelFromCoordinate() throws Exception {
//    Model model = aether.resolveModel("");
//    for (Dependency d : model.getDependencyManagement().getDependencies()) {
//      System.out.println(d.getArtifactId());
//    }
//  }

  public void testFindingAllVersionsWithRange() throws Exception {
    List<String> versions = aether.findAllVersions("org.codehaus.redback:redback-struts2-integration:[0,1.2.4)");
    //
    // 1.2-beta-1
    // 1.2-beta-2
    // 1.2
    // 1.2.1
    // 1.2.2
    // 1.2.3
    //
    assertTrue(versions.contains("1.2-beta-1"));
    assertTrue(versions.contains("1.2-beta-2"));
    assertTrue(versions.contains("1.2"));
    assertTrue(versions.contains("1.2.1"));
    assertTrue(versions.contains("1.2.2"));
    assertTrue(versions.contains("1.2.3"));
  }

  public void testFindingAllVersions() throws Exception {
    List<String> versions = aether.findAllVersions("org.codehaus.redback:redback-struts2-integration:[0,)");
    assertTrue(versions.contains("1.2-beta-1"));
    assertTrue(versions.contains("1.2-beta-2"));
    assertTrue(versions.contains("1.2"));
    assertTrue(versions.contains("1.2.1"));
    assertTrue(versions.contains("1.2.2"));
    assertTrue(versions.contains("1.2.3"));
    assertTrue(versions.contains("1.2.4"));
    assertTrue(versions.contains("1.2.5"));
    assertTrue(versions.contains("1.2.6"));
    assertTrue(versions.contains("1.2.7"));
    assertTrue(versions.contains("1.2.8"));
    assertTrue(versions.contains("1.2.9"));
    assertTrue(versions.contains("1.3-M1"));
    assertTrue(versions.contains("1.3-M2"));
    assertTrue(versions.contains("1.3-M2"));
    assertTrue(versions.contains("1.3"));
    assertTrue(versions.contains("1.4"));
  }

  //
  //
  //public void testResolutionWithRelocations() throws Exception {
  //  File modelFile = new File(remoteRepository, "acegisecurity/acegi-security/1.0.0/acegi-security-1.0.0.pom");
  //  List<Artifact> artifacts = aether.resolveArtifacts(modelFile);
  //}
}
