/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.guice;

import io.tesla.aether.okhttp.OkHttpRepositoryConnectorFactory;

import javax.inject.Provider;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.io.FileProcessor;

public class RepositorySystemProvider implements Provider<RepositorySystem> {
  public RepositorySystem get() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
    locator.addService(RepositoryConnectorFactory.class, OkHttpRepositoryConnectorFactory.class);
    locator.addService(FileProcessor.class, DefaultFileProcessor.class);
    return locator.getService(RepositorySystem.class);
  }
}
