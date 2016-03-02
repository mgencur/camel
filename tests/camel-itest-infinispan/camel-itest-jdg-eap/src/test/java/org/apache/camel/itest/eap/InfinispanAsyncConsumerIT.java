package org.apache.camel.itest.eap;

import org.apache.camel.component.infinispan.InfinispanAsyncConsumerTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.runner.RunWith;

import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentEmbedded;

@RunWith(Arquillian.class)
public class InfinispanAsyncConsumerIT extends InfinispanAsyncConsumerTest {

   @Deployment
   public static Archive<?> deployment() {
      return baseDeploymentEmbedded()
            .addClass(InfinispanAsyncConsumerIT.class);
   }

}
