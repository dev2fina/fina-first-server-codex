/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fina.first.ha.service;

import org.jboss.logging.Logger;
import org.jboss.msc.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.wildfly.clustering.group.Node;
//import org.wildfly.clustering.service.PassiveServiceSupplier;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * This service periodically looks up the singleton service. If the service is running on this node it will log value provided
 * by the service. For the sake of illustration, singleton service supplies a value which represents the node where it is
 * currently running.
 *
 * @author Radoslav Husar
 */
class QueryingService implements Service {

    private final Logger LOG = Logger.getLogger(this.getClass());
    private ScheduledExecutorService executor;

    private final int initialDelay;
    private final int period;
    private final TimeUnit timeUnit;

    public QueryingService(int initialDelay, int period, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    @Override
    public void start(StartContext context) {
//        LOG.info("Querying service is starting.");
//        org.wildfly.clustering.service.
//
//        executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleAtFixedRate(() -> {
//
//            Supplier<Node> node = new PassiveServiceSupplier<>(context.getController().getServiceContainer(), SingletonServiceActivator.SINGLETON_SERVICE_NAME);
//            if (node.get() != null) {
//                LOG.infof("Singleton service is running on this (%s) node.", node.get());
//            }
//
//        }, initialDelay, period, timeUnit);
    }

    @Override
    public void stop(StopContext context) {
        LOG.info("Querying service is stopping.");

        executor.shutdown();
    }

}
