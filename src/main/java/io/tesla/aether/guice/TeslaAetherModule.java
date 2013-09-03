/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.guice;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.MavenAetherModule;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import com.google.inject.Binder;
import com.google.inject.Module;

public class TeslaAetherModule implements Module {

  public void configure(Binder binder) {
    binder.install(new MavenAetherModule());    
    binder.bind(ModelBuilder.class).toInstance(new DefaultModelBuilderFactory().newInstance());
    binder.bind(RemoteRepository.class).toProvider(RemoteRepositoryProvider.class);
    binder.bind(RepositorySystem.class).toProvider(RepositorySystemProvider.class);
    binder.bind(RepositorySystemSession.class).toProvider(RepositorySystemSessionProvider.class);    
  }
}
