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
package org.apache.camel.itest.osgi.infinispan;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.component.infinispan.InfinispanRemoteAsyncConsumerIT;
import org.apache.camel.itest.osgi.infinispan.util.CamelContextFactory;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import static org.apache.camel.itest.osgi.infinispan.util.IspnKarafOptions.camelHotRodClient;
import static org.apache.camel.itest.osgi.infinispan.util.IspnKarafOptions.camelInfinispanAndTests;
import static org.apache.camel.itest.osgi.infinispan.util.IspnKarafOptions.camelTestOptions;
import static org.apache.camel.itest.osgi.infinispan.util.IspnKarafOptions.commonOptions;
import static org.ops4j.pax.exam.CoreOptions.options;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class OSGiInfinispanRemoteAsyncConsumerTest extends InfinispanRemoteAsyncConsumerIT {

    @Inject
    protected BundleContext bundleContext;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return new CamelContextFactory(bundleContext, createRegistry()).createContext();
    }

    @Override
    protected int getHotRodPort() {
        return 11322;
    }

    @Configuration
    public static Option[] config() throws Exception {
        return options(
                commonOptions(),
                camelTestOptions(),
                camelInfinispanAndTests(),
                camelHotRodClient()
        );
    }
}