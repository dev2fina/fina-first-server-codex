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

import net.fina.first.ha.ejb.ClusteringSingleton;
import org.jboss.logging.Logger;
import org.jboss.msc.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.group.Node;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.function.Consumer;
import java.util.function.Supplier;

class SingletonService implements Service {

    private final Logger LOG = Logger.getLogger(this.getClass());

    private Node node;

    private final Supplier<Group> groupSupplier;
    private final Consumer<Node> nodeConsumer;

    SingletonService(Supplier<Group> groupSupplier, Consumer<Node> nodeConsumer) {
        this.groupSupplier = groupSupplier;
        this.nodeConsumer = nodeConsumer;
    }

    @Override
    public void start(StartContext context) {
        this.node = this.groupSupplier.get().getLocalMember();

        this.nodeConsumer.accept(this.node);
        ClusteringSingleton clusteringSingleton = getClusterSingleton();

        if (clusteringSingleton != null) {
            clusteringSingleton.putCache("SINGLETON_SERVICE_NODE", node.getName());
        } else {
            LOG.error("Cannot Find ClusteringSingleton Singleton bean in JNDI!!!");
        }

        LOG.infof("Singleton service is started on node '%s'.", this.node);
    }

    @Override
    public void stop(StopContext context) {
        LOG.infof("Singleton service is stopping on node '%s'.", this.node);

        this.node = null;
    }

    private ClusteringSingleton getClusterSingleton() {
        try {
            InitialContext ic = new InitialContext();

            String appName = (String) ic.lookup("java:app/AppName");
            String module = "fina-first";

            String clusteringSingletonJNDI = "java:global/" + appName + "/" + module + "/ClusteringSingleton";

            Object object = ic.lookup(clusteringSingletonJNDI);
            return (ClusteringSingleton) object;
        } catch (NamingException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

}
