/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.aether.internal;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.slf4j.Logger;

class AetherTransferListener extends AbstractTransferListener {

  private final Logger log;

  private ThreadLocal<Long> last;

  public AetherTransferListener(Logger log) {
    this.log = log;
    last = new ThreadLocal<Long>();
  }

  @Override
  public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    log.info("Downloading {}{}...", event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
  }

  @Override
  public void transferSucceeded(TransferEvent event) {
    log.info("Downloaded [{} bytes] {}{}", new Object[] {
        event.getTransferredBytes(), event.getResource().getRepositoryUrl(), event.getResource().getResourceName()
    });
  }

  @Override
  public void transferFailed(TransferEvent event) {
    log.error("Failed to download {}{}", new Object[] {
        event.getResource().getRepositoryUrl(), event.getResource().getResourceName(), event.getException()
    });
  }

  @Override
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    Long last = this.last.get();
    if (last == null || last.longValue() < System.currentTimeMillis() - 5 * 1000) {
      String progress;
      if (event.getResource().getContentLength() > 0) {
        progress = (int) (event.getTransferredBytes() * 100.0 / event.getResource().getContentLength()) + "%";
      } else {
        progress = event.getTransferredBytes() + " bytes";
      }
      log.info("Downloading [{}] {}{}...", new Object[] {
          progress, event.getResource().getRepositoryUrl(), event.getResource().getResourceName()
      });
      this.last.set(new Long(System.currentTimeMillis()));
    }
  }

}
