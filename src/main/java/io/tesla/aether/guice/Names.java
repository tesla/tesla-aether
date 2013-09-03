/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.guice;

public interface Names {  
  static final String LOCAL_REPOSITORY = "local.repository";
  static final String LOCAL_REPOSITORY_CONF = "${" + LOCAL_REPOSITORY + "}";

  static final String REMOTE_REPOSITORY = "remote.repository";
  static final String REMOTE_REPOSITORY_CONF = "${" + REMOTE_REPOSITORY + "}";
}
