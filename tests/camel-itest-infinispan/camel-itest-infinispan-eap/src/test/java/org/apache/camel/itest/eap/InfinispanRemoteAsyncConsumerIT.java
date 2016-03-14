//package org.apache.camel.itest.eap;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.junit.runner.RunWith;
//
//import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentRemote;
//
//@RunWith(Arquillian.class)
//public class InfinispanRemoteAsyncConsumerIT extends org.apache.camel.component.infinispan.InfinispanRemoteAsyncConsumerIT {
//
//   @Deployment
//   public static Archive<?> deployment() {
//      return baseDeploymentRemote()
//            .addClass(InfinispanRemoteAsyncConsumerIT.class);
//   }
//
//}
