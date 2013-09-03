/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
