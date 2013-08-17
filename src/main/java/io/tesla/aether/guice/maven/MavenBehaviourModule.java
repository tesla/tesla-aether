package io.tesla.aether.guice.maven;

import org.apache.maven.repository.internal.MavenAetherModule;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import com.google.inject.Binder;
import com.google.inject.Module;

public class MavenBehaviourModule implements Module {

  public void configure(Binder binder) {
    binder.install(new MavenAetherModule());
    binder.bind(RemoteRepository.class).toProvider(MavenBehaviourRepositoryProvider.class);
    binder.bind(RepositorySystemSession.class).toProvider(MavenBehaviourSystemSessionProvider.class);        
  }
}
