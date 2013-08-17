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
