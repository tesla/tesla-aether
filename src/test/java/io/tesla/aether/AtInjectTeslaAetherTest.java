/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether;

import io.tesla.aether.guice.maven.MavenBehaviourModule;

import javax.inject.Inject;

import org.eclipse.sisu.containers.InjectedTestCase;

import com.google.inject.Binder;

public class AtInjectTeslaAetherTest extends InjectedTestCase {
  
  @Inject
  private TeslaAether aether;
  
  @Override
  public void configure(Binder binder) {
    binder.install(new MavenBehaviourModule());
  }
  
  public void testTeslaAether() throws Exception {
    
  }
}
