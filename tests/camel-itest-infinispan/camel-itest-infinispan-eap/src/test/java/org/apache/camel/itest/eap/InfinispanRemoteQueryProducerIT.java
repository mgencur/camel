package org.apache.camel.itest.eap;

import org.apache.camel.component.infinispan.InfinispanAsyncConsumerTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentRemote;

@RunWith(Arquillian.class)
public class InfinispanRemoteQueryProducerIT extends org.apache.camel.component.infinispan.InfinispanRemoteQueryProducerIT {

   @Deployment
   public static Archive<?> deployment() {
      return baseDeploymentRemote()
            .addClass(InfinispanRemoteQueryProducerIT.class);
   }

}
