/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.internal;

import io.tesla.aether.Repository;
import io.tesla.aether.TeslaAether;
import io.tesla.aether.Workspace;
import io.tesla.aether.guice.DefaultModelCache;
import io.tesla.aether.guice.DefaultModelResolver;
import io.tesla.aether.guice.RepositorySystemSessionProvider;
import io.tesla.aether.guice.maven.MavenBehaviourRepositoryProvider;
import io.tesla.aether.okhttp.OkHttpRepositoryConnectorFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.version.Version;
import org.eclipse.sisu.Nullable;

import com.google.common.collect.ImmutableList;

@Named
public class DefaultTeslaAether implements TeslaAether {

  //private Logger logger = LoggerFactory.getLogger(TeslaAether.class);

  private RepositorySystem system;
  private RepositorySystemSession session;
  private ModelBuilder modelBuilder;
  private ArtifactResolver artifactResolver;
  private RemoteRepositoryManager remoteRepositoryManager;
  private List<RemoteRepository> remoteRepositories;

  public DefaultTeslaAether() {
    this(new File(System.getProperty("user.home"), ".m2/repository"), "http://repo1.maven.org/maven2/");
  }

  public DefaultTeslaAether(String localRepository, List<String> remoteRepositoryUris) {    
    List<Repository> repositories = new ArrayList<Repository>();
    for (String remoteRepositoryUri : remoteRepositoryUris) {
      repositories.add(new Repository(remoteRepositoryUri));
    }
    init(new File(localRepository), repositories);
  }
  
  public DefaultTeslaAether(File localRepository, String... remoteRepositoryUris) {
    List<Repository> repositories = new ArrayList<Repository>();
    for (String remoteRepositoryUri : remoteRepositoryUris) {
      repositories.add(new Repository(remoteRepositoryUri));
    }
    init(localRepository, repositories);
  }

  private void init(File localRepository, List<Repository> repositories) {
    remoteRepositories = new ArrayList<RemoteRepository>();
    for (Repository r : repositories) {
      RemoteRepository.Builder builder = new RemoteRepository.Builder(r.getId(), "default", r.getUrl());
      if (r.getUsername() != null && r.getPassword() != null) {
        Authentication auth = new AuthenticationBuilder().addUsername(r.getUsername()).addPassword(r.getPassword()).build();
        builder.setAuthentication(auth);
      }
      this.remoteRepositories.add(builder.build());
    }

    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
    locator.addService(RepositoryConnectorFactory.class, OkHttpRepositoryConnectorFactory.class);
    locator.addService(FileProcessor.class, DefaultFileProcessor.class);

    this.system = locator.getService(RepositorySystem.class);
    this.session = new RepositorySystemSessionProvider(localRepository).get();
    this.modelBuilder = new DefaultModelBuilderFactory().newInstance();
    this.artifactResolver = locator.getService(ArtifactResolver.class);
    this.remoteRepositoryManager = locator.getService(RemoteRepositoryManager.class);
  }

  @Inject
  public DefaultTeslaAether(RepositorySystem system, RepositorySystemSession session, ModelBuilder modelBuilder, ArtifactResolver artifactResolver, RemoteRepositoryManager remoteRepositoryManager,
      @Nullable RemoteRepository remoteRepository) {
    this.system = system;
    this.session = session;
    this.modelBuilder = modelBuilder;
    this.artifactResolver = artifactResolver;
    this.remoteRepositoryManager = remoteRepositoryManager;

    // A bunch of defensive hacking to make an initial integration with Maven. Far from perfect

    // After a bunch of POM parsing the MavenProject ends up with a list of remote repositories, there's no decent way to really get at this
    // without passing it in as a parameter which I'm trying to avoid.
    if (remoteRepository == null) {

      this.remoteRepositories = ImmutableList.of(new MavenBehaviourRepositoryProvider().get());

      /*
       * String teslaAetherRemoteRepositories = System.getProperty(TeslaAether.REMOTE_REPOSITORY); if(teslaAetherRemoteRepositories == null) { throw new
       * RuntimeException("There are no remote repositories specified in the " + TeslaAether.REMOTE_REPOSITORY + " property."); }
       * 
       * String[] remoteRepos = StringUtils.split(teslaAetherRemoteRepositories, ","); for (String remoteRepositoryUri : remoteRepos) { remoteRepositories.add(getRemoteRepository(new
       * Repository(remoteRepositoryUri))); }
       */
    } else {
      this.remoteRepositories = ImmutableList.of(remoteRepository);
    }

    // Another hack because the MavenRepositoryUtils.newSession() doesn't really setup a useful session because there is no
    // repository manager
    if (session.getLocalRepositoryManager() == null) {
      LocalRepository localRepo = new LocalRepository(new File(System.getProperty("user.home"), ".m2/repository"));
      //
      // We are not concerned with checking the _remote.repositories files
      //
      try {
        ((DefaultRepositorySystemSession) session).setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(session, localRepo));
      } catch (NoLocalRepositoryManagerException e) {
        //
        // This should never happen
        //
      }

    }

  }

  // Model

  public Model resolveModel(File pom) throws ModelBuildingException {

    RequestTrace trace = new RequestTrace(pom);
    ModelBuildingRequest modelRequest = new DefaultModelBuildingRequest();
    modelRequest.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
    modelRequest.setProcessPlugins(false);
    modelRequest.setTwoPhaseBuilding(false);
    modelRequest.setSystemProperties(toProperties(session.getUserProperties(), session.getSystemProperties()));
    //
    // The model cache and default model resolver should be injected
    //
    modelRequest.setModelCache(new DefaultModelCache());
    modelRequest.setModelResolver(new DefaultModelResolver(session, trace.newChild(modelRequest), "bithub", artifactResolver, remoteRepositoryManager, remoteRepositories));
    modelRequest.setPomFile(pom);
    return modelBuilder.build(modelRequest).getEffectiveModel();
  }

  // <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
  public Model resolveModel(String coordinate) throws ModelBuildingException, ArtifactResolutionException {
    File pom = resolveArtifact(coordinate).getArtifact().getFile();
    return resolveModel(pom);
  }

  // Resolve single artifact

  public ArtifactResult resolveArtifact(String coordinate) throws ArtifactResolutionException {
    Artifact artifact = new DefaultArtifact(coordinate);
    return resolveArtifact(artifact);
  }

  public ArtifactResult resolveArtifact(Artifact artifact) throws ArtifactResolutionException {
    ArtifactRequest artifactRequest = new ArtifactRequest();
    artifactRequest.setArtifact(artifact);
    for (RemoteRepository remoteRepository : remoteRepositories) {
      artifactRequest.addRepository(remoteRepository);
    }
    return system.resolveArtifact(session, artifactRequest);
  }

  // Resolve transitive hulls

  public List<Artifact> resolveArtifacts(String coordinate) throws DependencyResolutionException {
    return resolveArtifacts(new DefaultArtifact(coordinate));
  }

  public List<Artifact> resolveArtifacts(Artifact artifact) throws DependencyResolutionException {
    DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME);
    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRoot(new Dependency(artifact, JavaScopes.RUNTIME));
    for (RemoteRepository remoteRepository : remoteRepositories) {
      collectRequest.addRepository(remoteRepository);
    }
    DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
    return resolveArtifacts(dependencyRequest);
  }

  public List<Artifact> resolveArtifacts(DependencyRequest request) throws DependencyResolutionException {
    //
    // We are attempting to encapsulate everything about resolution with this library. The dependency request requires
    // the collect request to have repositories set but this is all injected within this component so we have to set them.
    //
    CollectRequest collectRequest = request.getCollectRequest();
    if (collectRequest.getRepositories() == null || collectRequest.getRepositories().isEmpty()) {
      for (RemoteRepository remoteRepository : remoteRepositories) {
        collectRequest.addRepository(remoteRepository);
      }
    }

    DependencyResult result = system.resolveDependencies(session, request);

    List<Artifact> artifacts = new ArrayList<Artifact>();
    for (ArtifactResult ar : result.getArtifactResults()) {
      artifacts.add(ar.getArtifact());
    }
    return artifacts;
  }

  //
  // Workspace related
  //

  // There are two modes of reading a pom.xml. 
  //
  // - Reading the pom.xml where we are just trying to extract the coordinates of a release project to resolve released things
  // - Reading the pom.xml where we are working with something in process and we resolve the dependencies but not the artifact for the pom itself
  //
  // Here we really want to read the POM and resolve all the dependencies in the POM
  //
  public List<Artifact> resolveArtifacts(File modelFile) throws ModelBuildingException, DependencyResolutionException {
    Model model = resolveModel(modelFile);
    Artifact artifact = new DefaultArtifact(model.getGroupId(), model.getArtifactId(), model.getPackaging(), model.getVersion());
    return resolveArtifacts(artifact);
  }

  private Dependency toAetherDependency(org.apache.maven.model.Dependency dependency) {
    Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), dependency.getType(), dependency.getVersion());
    ImmutableList.Builder<Exclusion> exclusions = ImmutableList.builder();
    for (org.apache.maven.model.Exclusion exclusion : dependency.getExclusions()) {
      exclusions.add(new Exclusion(exclusion.getGroupId(), exclusion.getArtifactId(), null, "*"));
    }
    return new Dependency(artifact, dependency.getScope(), dependency.isOptional(), exclusions.build());
  }

  public ArtifactType getArtifactType(String typeId) {
    return session.getArtifactTypeRegistry().get(typeId);
  }

  // Util

  private Properties toProperties(Map<String, String> dominant, Map<String, String> recessive) {
    Properties props = new Properties();
    if (recessive != null) {
      props.putAll(recessive);
    }
    if (dominant != null) {
      props.putAll(dominant);
    }
    return props;
  }

  public List<String> findAllVersions(String gaVersionRange) throws VersionRangeResolutionException {

    //    Artifact artifact = new DefaultArtifact( "org.codehaus.redback:redback-struts2-integration:[0,1.2.4)" );
    //
    //    RemoteRepository repo = Booter.newCentralRepository();
    //
    //    VersionRangeRequest rangeRequest = new VersionRangeRequest();
    //    rangeRequest.setArtifact( artifact );
    //    rangeRequest.addRepository( repo );
    //
    //    VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
    //
    //    List<Version> versions = rangeResult.getVersions();    

    Artifact artifact = new DefaultArtifact(gaVersionRange);
    VersionRangeRequest rangeRequest = new VersionRangeRequest();
    rangeRequest.setArtifact(artifact);
    for (RemoteRepository remoteRepository : remoteRepositories) {
      rangeRequest.addRepository(remoteRepository);
    }

    VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
    List<Version> versions = rangeResult.getVersions();
    List<String> result = new ArrayList<String>();
    for (Version v : versions) {
      result.add(v.toString());
    }
    return result;
  }

  public List<File> resolveWorkspace(Workspace workspace) throws ModelBuildingException, DependencyResolutionException {
    List<File> classpath = new ArrayList<File>();
    for (File projectDirectory : workspace.getProjectDirectories()) {
      //
      // Find the POM in each directory and resolve that
      //
      File pom = new File(projectDirectory, "pom.xml");
      Model model = resolveModel(pom);
      List<Artifact> artifacts = resolveArtifacts(new DefaultArtifact(model.getGroupId(), model.getArtifactId(), model.getPackaging(), model.getVersion()));
      for (Artifact artifact : artifacts) {
        classpath.add(artifact.getFile());        
      }
      //
      // We also want the ${build.directory}/classes directory
      //
      File classes = new File(model.getBuild().getDirectory(), "classes");
      classpath.add(classes);
    }
    return classpath;
  }
}
