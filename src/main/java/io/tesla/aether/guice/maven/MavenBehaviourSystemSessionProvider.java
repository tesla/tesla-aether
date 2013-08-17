package io.tesla.aether.guice.maven;

import io.tesla.aether.internal.ConsoleRepositoryListener;
import io.tesla.aether.internal.ConsoleTransferListener;

import java.io.File;

import javax.inject.Provider;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

public class MavenBehaviourSystemSessionProvider implements Provider<RepositorySystemSession> {
  //
  // Need to pass in the local repository path
  //
  public RepositorySystemSession get() {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    LocalRepository localRepo = new LocalRepository(new File(System.getProperty("user.home"), ".m2/repository"));
    //
    // We are not concerned with checking the _remote.repositories files
    //
    try {
      session.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(session, localRepo));
    } catch (NoLocalRepositoryManagerException e) {
      //
      // This should never happen
      //
    }
   
    // This controls whether you want to repositories in pom.xml files
    //session.setIgnoreArtifactDescriptorRepositories(true);
    session.setTransferListener(new ConsoleTransferListener());
    session.setRepositoryListener(new ConsoleRepositoryListener());

    return session;
  }
}
