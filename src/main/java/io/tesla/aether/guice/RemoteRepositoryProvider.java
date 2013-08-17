package io.tesla.aether.guice;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.eclipse.aether.repository.RemoteRepository;

public class RemoteRepositoryProvider implements Provider<RemoteRepository> {

  // This can be a url in the form of http:// or file:/
  private String repositoryUrl;

  @Inject
  public RemoteRepositoryProvider(@Named(Names.REMOTE_REPOSITORY_CONF) String repositoryUrl) {
    if (repositoryUrl.startsWith("/")) {
      this.repositoryUrl = "file:" + repositoryUrl;
    } else {
      this.repositoryUrl = repositoryUrl;
    }
  }

  public RemoteRepository get() {
    return new RemoteRepository.Builder("bithub", "default", repositoryUrl).build();
  }
}
