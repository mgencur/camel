package org.apache.camel.itest.eap;

import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentRemote;

import org.apache.camel.component.infinispan.InfinispanRemoteProducerTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InfinispanRemoteProducerIT extends InfinispanRemoteProducerTest {

   @Deployment
   public static Archive<?> deployment() {
      return baseDeploymentRemote()
            .addClass(InfinispanRemoteProducerTest.class);
   }

}
