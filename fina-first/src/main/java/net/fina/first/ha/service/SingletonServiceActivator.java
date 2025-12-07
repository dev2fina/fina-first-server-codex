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

import net.fina.common.server.util.ConfigurationUtil;
import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;
//import org.wildfly.clustering.service.ActiveServiceSupplier;
import org.wildfly.clustering.singleton.SingletonDefaultRequirement;
import org.wildfly.clustering.singleton.service.SingletonPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SingletonServiceActivator implements ServiceActivator {

    private final Logger LOG = Logger.getLogger(SingletonServiceActivator.class);

    static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.parse("net.fina.server.ha.singleton.service");
    private static final ServiceName QUERYING_SERVICE_NAME = ServiceName.parse("net.fina.server.ha.singleton.service.querying");


    @Override
    public void activate(ServiceActivatorContext serviceActivatorContext) {

//        SingletonPolicy policy = new ActiveServiceSupplier<SingletonPolicy>(
//                serviceActivatorContext.getServiceRegistry(),
//                ServiceName.parse(SingletonDefaultRequirement.POLICY.getName())
//        ).get();
//
//        // Install singleton service
//
//        ServiceTarget target = serviceActivatorContext.getServiceTarget();
//        ServiceBuilder<?> builder = policy.createSingletonServiceConfigurator(SINGLETON_SERVICE_NAME).build(target);
//        Consumer<Node> member = builder.provides(SINGLETON_SERVICE_NAME);
//        Supplier<Group> group = builder.requires(ServiceName.parse("org.wildfly.clustering.default-group"));
//        builder.setInstance(new SingletonService(group, member)).install();
//
//        // Install querying service
//
//        serviceActivatorContext.getServiceTarget().addService(QUERYING_SERVICE_NAME).setInstance(new QueryingService(5, 1, TimeUnit.MINUTES)).install();
//
//        LOG.info("Singleton and querying services activated.");


    }
}
