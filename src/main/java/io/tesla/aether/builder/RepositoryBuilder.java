package io.tesla.aether.builder;

import java.io.File;
import java.util.List;

import io.tesla.aether.TeslaAether;
import io.tesla.aether.internal.DefaultTeslaAether;

/**
 * Use to retrieve sets of real dependencies from real repositories in order to make self-contained repositories where the artifacts
 * can selective contain no real content. So you can easily build up test sets without taking up a lot of space. We really want
 * the graph of artifacts and the metadata associated with them.
 * 
 * @author jvanzyl
 *
 */
public class RepositoryBuilder {

  // we need to fetch a dependency chain and all the metadata associated with so that it appears like a subset of
  // a proper remote repository
  //
  // - artifacts
  // - metadata
  // - classified artifactsm
  
  
  private TeslaAether aether;
  
  public RepositoryBuilder() {
    aether = new DefaultTeslaAether(new File("/tmp/repository"), "http://repo1.maven.org/maven2");
  }
  
  // Resolve and store in a standard way
  
  public void execute() throws Exception {

    // retrieve all versions
    // optionally all metadata
    // optinally all classified artifacts
    
    List<String> versions = aether.findAllVersions("org.codehaus.redback:redback-struts2-integration:[0,1.2.4)");
    for(String version : versions) {
      System.out.println(version);
    }
    
    aether.resolveArtifact("io.tesla.maven:maven-core:3.1.2");
    
  }
  
  public static void main(String[] args) throws Exception {
    
    RepositoryBuilder rb = new RepositoryBuilder();
    rb.execute();
    
  }
}
