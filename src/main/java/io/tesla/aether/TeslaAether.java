/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;

public interface TeslaAether {

  static final String LOCAL_REPOSITORY = "tesla.aether:local.repository";
  static final String LOCAL_REPOSITORY_CONF = "${" + LOCAL_REPOSITORY + "}";

  static final String REMOTE_REPOSITORY = "tesla.aether:remote.repository";
  static final String REMOTE_REPOSITORY_CONF = "${" + REMOTE_REPOSITORY + "}";

  static final File DEFAULT_LOCAL_REPOSITORY = new File(System.getProperty("user.home"), ".m2/repository"); 
  static final String DEFAULT_REMOTE_REPOSITORY = "http://repo1.maven.org/maven2";
  
  //
  // Model Resolution
  //
  Model resolveModel(File modelFile)
      throws ModelBuildingException;

  Model resolveModel(String coordinate)
      throws ModelBuildingException, ArtifactResolutionException;

  //
  // Single Artifact Resolution
  //
  ArtifactResult resolveArtifact(String coordinate) throws ArtifactResolutionException;

  ArtifactResult resolveArtifact(Artifact artifact) throws ArtifactResolutionException;

  //
  // Transitive Resolution
  //
  List<Artifact> resolveArtifacts(String coordinate)
      throws DependencyResolutionException;

  List<Artifact> resolveArtifacts(Artifact artifact)
      throws DependencyResolutionException;

  List<Artifact> resolveArtifacts(DependencyRequest dependencyRequest)
      throws DependencyResolutionException;

  //
  // Workspace Resolution
  //
  List<Artifact> resolveArtifacts(File modelFile)
      throws ModelBuildingException, DependencyResolutionException;

  List<File> resolveWorkspace(Workspace workspace)
      throws ModelBuildingException, DependencyResolutionException;

  ArtifactType getArtifactType(String typeId);

  List<String> findAllVersions(String ga) 
      throws VersionRangeResolutionException;

}
