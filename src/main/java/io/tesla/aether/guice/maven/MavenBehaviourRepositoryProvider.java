/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.guice.maven;

import java.io.File;

import io.tesla.aether.Repository;

import javax.inject.Provider;

import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

// <mirrors>                                                                                                                                                                                                         
//   <mirror>                                                                                                                                                                                                        
//     <id>central</id>                                                                                                                                                                                              
//     <name>Nexus Mirror</name>                                                                                                                                                                                     
//     <url>http://localhost:8081/nexus/content/groups/public</url>                                                                                                                                                  
//     <mirrorOf>external:*</mirrorOf>                                                                                                                                                                               
//   </mirror>                                                                                                                                                                                                       
// </mirrors>                                                                                                                                                                                                        
//                                                                                                                                                                                                                
// <profiles>                                                                                                                                                                                                        
//   <profile>                                                                                                                                                                                                       
//     <id>development</id>                                                                                                                                                                                          
//     <repositories>                                                                                                                                                                                                
//       <repository>                                                                                                                                                                                                
//         <id>central</id>                                                                                                                                                                                          
//         <url>http://central</url>                                                                                                                                                                                 
//       </repository>                                                                                                                                                                                               
//     </repositories>                                                                                                                                                                                               
//     <pluginRepositories>                                                                                                                                                                                          
//       <pluginRepository>                                                                                                                                                                                          
//         <id>central</id>                                                                                                                                                                                          
//         <url>http://central</url>                                                                                                                                                                                 
//       </pluginRepository>                                                                                                                                                                                         
//     </pluginRepositories>                                                                                                                                                                                         
//   </profile>                                                                                                                                                                                                      
// </profiles>                                                                                                                                                                                                       
//                                                                                                                                                                                                                
// <activeProfiles>                                                                                                                                                                                                  
//   <activeProfile>development</activeProfile>                                                                                                                                                                      
// </activeProfiles>             

public class MavenBehaviourRepositoryProvider implements Provider<RemoteRepository> {

  public RemoteRepository get() {
    return getRemoteRepository();
  }

  public RemoteRepository getRemoteRepository() {

    String repositoryId = "central";
    String repositoryUrl = "http://repo1.maven.org/maven2";
    String repoUser = null;
    String repoPass = null;

    Settings settings = readMavenSettings();
    if (settings != null) {
      //
      // There is a settings.xml file present so we delegate purely to the settings.xml file for mirrors and authentication.
      // TODO: We ultimately need to construct a proper mirror selector which we should borrow from Maven itself.
      //
      if (settings.getMirrors() != null && settings.getMirrors().size() == 1) {
        Mirror mirror = settings.getMirrors().get(0);
        repositoryUrl = mirror.getUrl();
        repositoryId = mirror.getId();
        if (settings.getServer(repositoryId) != null) {
          repoUser = settings.getServer(repositoryId).getUsername();
          repoPass = settings.getServer(repositoryId).getPassword();
        }
      }
    }

    RemoteRepository.Builder builder = new RemoteRepository.Builder(repositoryId, "default", repositoryUrl);
    if (repoUser != null && repoPass != null) {
      Authentication auth = new AuthenticationBuilder().addUsername(repoUser).addPassword(repoPass).build();
      builder.setAuthentication(auth);
    }

    return builder.build();
  }

  public Settings readMavenSettings() {
    File userSettingsFile = new File(System.getProperty("user.home"), ".m2/settings.xml");
    if (userSettingsFile.exists() == false) {
      return null;
    }
    SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
    SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
    request.setSystemProperties(System.getProperties());
    request.setUserSettingsFile(userSettingsFile);

    Settings settings;
    try {
      settings = settingsBuilder.build(request).getEffectiveSettings();
    } catch (SettingsBuildingException e) {
      settings = new Settings();
    }

    return settings;
  }
}
