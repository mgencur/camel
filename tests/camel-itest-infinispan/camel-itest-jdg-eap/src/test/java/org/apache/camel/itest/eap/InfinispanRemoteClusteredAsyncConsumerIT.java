package org.apache.camel.itest.eap;

import java.io.IOException;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.camel.itest.eap.utils.DeploymentBuilder.baseDeploymentRemote;

@RunWith(Arquillian.class)
public class InfinispanRemoteClusteredAsyncConsumerIT extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    private RemoteCacheManager manager;

    @Deployment
    public static Archive<?> deployment() {
        return baseDeploymentRemote()
                .addClass(InfinispanRemoteClusteredAsyncConsumerIT.class);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("myCustomContainer", manager);
        return registry;
    }

    @Override
    protected void doPreSetup() throws IOException {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("localhost").port(11222)
            .addServer().host("localhost").port(11422);
        manager = new RemoteCacheManager(builder.build());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("infinispan://?cacheContainer=#myCustomContainer&sync=false")
                        .to("mock:result");
            }
        };
    }

    @Test
    public void remoteCreateEventReceived() throws Exception {
        for (int i = 1; i != 10; i++) {
            mockResult.expectedMessageCount(i);

            String key = generateKey();
            manager.getCache().put(key, "value" + i);

            mockResult.assertIsSatisfied();
            Exchange exchange = mockResult.getExchanges().get(i-1);
            Message out = exchange.getOut();

            assertEquals("CLIENT_CACHE_ENTRY_CREATED", out.getHeader(InfinispanConstants.EVENT_TYPE));
            assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
            assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
            assertEquals(key, out.getHeader(InfinispanConstants.KEY));
        }
    }

    @Test
    public void remoteModifyEventReceived() throws Exception {
        for (int i = 1; i != 10; i++) {
            mockResult.expectedMessageCount(i*2);

            String sameKey = generateKey();
            getDefaultCache().put(sameKey, "aValue");
            getDefaultCache().put(sameKey, "anotherValue");

            mockResult.assertIsSatisfied();
            Exchange exchange = mockResult.getExchanges().get(i*2-1);
            Message out = exchange.getOut();

            assertEquals("CLIENT_CACHE_ENTRY_MODIFIED", out.getHeader(InfinispanConstants.EVENT_TYPE));
            assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
            assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
            assertEquals(sameKey, out.getHeader(InfinispanConstants.KEY));
        }
    }

    @Test
    public void remoteRemovedEventReceived() throws Exception {
        for (int i = 1; i != 10; i++) {
            mockResult.expectedMessageCount(i*2);

            String sameKey = generateKey();
            getDefaultCache().put(sameKey, "aValue");
            getDefaultCache().remove(sameKey);

            mockResult.assertIsSatisfied();
            Exchange exchange = mockResult.getExchanges().get(i*2-1);
            Message out = exchange.getOut();

            assertEquals("CLIENT_CACHE_ENTRY_REMOVED", out.getHeader(InfinispanConstants.EVENT_TYPE));
            assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
            assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
            assertEquals(sameKey, out.getHeader(InfinispanConstants.KEY));
        }
    }

    private String generateKey() {
        return String.format("aKey%s", System.currentTimeMillis());
    }

    private RemoteCache<Object, Object> getDefaultCache() {
        return manager.getCache();
    }

}
