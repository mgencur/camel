package org.apache.camel.itest.eap;

import org.apache.camel.component.infinispan.InfinispanClusteredConsumerTest;
import org.infinispan.test.fwk.TestResourceTracker;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentEmbedded;

@RunWith(Arquillian.class)
public class InfinispanClusteredConsumerIT extends InfinispanClusteredConsumerTest {

   @Deployment
   public static Archive<?> deployment() {
      return baseDeploymentEmbedded()
              .addClass(InfinispanClusteredConsumerTest.class);
   }

}
