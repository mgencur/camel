//package org.apache.camel.itest.eap;
//
//import java.io.IOException;
//
//import org.apache.camel.component.infinispan.InfinispanRemoteAsyncConsumerConverterFactoryIT;
//import org.infinispan.client.hotrod.RemoteCacheManager;
//import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
//import org.infinispan.commons.marshall.jboss.GenericJBossMarshaller;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//
//import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentRemote;
//
//@RunWith(Arquillian.class)
//public class InfinispanRemoteAsyncConsumerCustomIT extends org.apache.camel.component.infinispan.InfinispanRemoteAsyncConsumerConverterFactoryIT {
//
//    @Deployment
//    public static Archive<?> deployment() {
//        return baseDeploymentRemote()
//                .addClass(InfinispanRemoteAsyncConsumerCustomIT.class);
//    }
//
//    @Override
//    protected void doPreSetup() throws IOException {
//        ConfigurationBuilder builder = new ConfigurationBuilder();
//        builder.addServer().host("localhost").port(11222);
//        //uncomment when BZ1213834 is backported
//        builder.marshaller(new GenericJBossMarshaller(this.getClass().getClassLoader()));
//        manager = new RemoteCacheManager(builder.build());
//    }
//
//}
