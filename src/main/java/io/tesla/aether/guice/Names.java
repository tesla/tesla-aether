package io.tesla.aether.guice;

public interface Names {  
  static final String LOCAL_REPOSITORY = "local.repository";
  static final String LOCAL_REPOSITORY_CONF = "${" + LOCAL_REPOSITORY + "}";

  static final String REMOTE_REPOSITORY = "remote.repository";
  static final String REMOTE_REPOSITORY_CONF = "${" + REMOTE_REPOSITORY + "}";
}
