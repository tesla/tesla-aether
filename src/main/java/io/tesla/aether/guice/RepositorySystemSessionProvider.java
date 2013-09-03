/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.guice;

import io.tesla.aether.internal.ConsoleRepositoryListener;
import io.tesla.aether.internal.ConsoleTransferListener;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.internal.impl.EnhancedLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

public class RepositorySystemSessionProvider implements Provider<RepositorySystemSession> {

  private File localRepositoryDirectory;

  @Inject
  public RepositorySystemSessionProvider(@Named(Names.LOCAL_REPOSITORY_CONF) File localRepository) {
    this.localRepositoryDirectory = localRepository;
  }

  //
  // Need to pass in the local repository path
  //
  public RepositorySystemSession get() {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    LocalRepository localRepo = new LocalRepository(localRepositoryDirectory);
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
   
    //
    // Don't follow remote repositories in POMs
    //
    //session.setIgnoreArtifactDescriptorRepositories(true);
    session.setTransferListener(new ConsoleTransferListener());
    session.setRepositoryListener(new ConsoleRepositoryListener());

    return session;
  }
}
