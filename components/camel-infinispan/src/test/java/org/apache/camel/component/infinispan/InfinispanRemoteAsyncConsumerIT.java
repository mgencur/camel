/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.infinispan;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.junit.Test;

public class InfinispanRemoteAsyncConsumerIT extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    private RemoteCacheManager manager;

    private static final long WAIT_TIMEOUT = 5000;

    protected int getHotRodPort() {
        return 11222;
    }

    @Override
    protected void doPreSetup() throws IOException {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("localhost").port(getHotRodPort());
        manager = new RemoteCacheManager(builder.build());
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("myCustomContainer", manager);
        return registry;
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
        mockResult.expectedMessageCount(1);

        String key = generateKey();
        getDefaultCache().put(key, "aValue");

        mockResult.assertIsSatisfied();
        Exchange exchange = mockResult.getExchanges().get(0);
        Message out = exchange.getOut();

        assertEquals("CLIENT_CACHE_ENTRY_CREATED", out.getHeader(InfinispanConstants.EVENT_TYPE));
        assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
        assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
        assertEquals(key, out.getHeader(InfinispanConstants.KEY));
    }

    @Test
    public void remoteModifyEventReceived() throws Exception {
        mockResult.expectedMessageCount(2);

        String sameKey = generateKey();
        getDefaultCache().put(sameKey, "aValue");
        getDefaultCache().put(sameKey, "anotherValue");

        mockResult.assertIsSatisfied();
        Exchange exchange = mockResult.getExchanges().get(1);
        Message out = exchange.getOut();

        assertEquals("CLIENT_CACHE_ENTRY_MODIFIED", out.getHeader(InfinispanConstants.EVENT_TYPE));
        assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
        assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
        assertEquals(sameKey, out.getHeader(InfinispanConstants.KEY));
    }

    @Test
    public void remoteRemovedEventReceived() throws Exception {
        mockResult.expectedMessageCount(2);

        String sameKey = generateKey();
        getDefaultCache().put(sameKey, "aValue");
        getDefaultCache().remove(sameKey);

        mockResult.assertIsSatisfied();
        Exchange exchange = mockResult.getExchanges().get(1);
        Message out = exchange.getOut();

        assertEquals("CLIENT_CACHE_ENTRY_REMOVED", out.getHeader(InfinispanConstants.EVENT_TYPE));
        assertEquals("", out.getHeader(InfinispanConstants.CACHE_NAME));
        assertEquals(false, out.getHeader(InfinispanConstants.IS_PRE));
        assertEquals(sameKey, out.getHeader(InfinispanConstants.KEY));
    }

    @Test
    public void remoteExpiredEventReceived() throws Exception {
        String sameKey = generateKey();

        mockResult.expectedMessageCount(2);

        mockResult.message(1).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CLIENT_CACHE_ENTRY_EXPIRED");
        mockResult.message(1).outHeader(InfinispanConstants.IS_PRE).isEqualTo(false);
        mockResult.message(1).outHeader(InfinispanConstants.CACHE_NAME).isEqualTo("");
        mockResult.message(1).outHeader(InfinispanConstants.KEY).isEqualTo(sameKey.toString());

        getDefaultCache().put(sameKey, "aValue", 200, TimeUnit.MILLISECONDS);

        Thread.sleep(300);

        assertNull(getDefaultCache().get(sameKey));
        mockResult.assertIsSatisfied(WAIT_TIMEOUT);
    }

    private String generateKey() {
        return String.format("aKey%s", System.currentTimeMillis());
    }

    private RemoteCache<Object, Object> getDefaultCache() {
        return manager.getCache();
    }
}