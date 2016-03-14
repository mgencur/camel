package org.apache.camel.itest.eap.utils;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public final class DeploymentBuilder {

    private static final String EAP_MODULES_EMBEDDED = "eap.modules.library";
    private static final String EAP_MODULES_REMOTE = "eap.modules.remote";

    public static WebArchive baseDeploymentEmbedded() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        if (System.getProperty(EAP_MODULES_EMBEDDED) != null) {
            addCommonLibrariesForEmbeddedAndRemote(war);
            war.addAsWebInfResource(new File("target/test-classes/embedded/jboss-deployment-structure.xml"));
        } else {
            addCommonLibrariesForEmbeddedAndRemote(war);
            war.addAsLibraries(new File("target/test-libs/infinispan-embedded.jar"));
        }
        return war;
    }

    public static WebArchive baseDeploymentRemote() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        if (System.getProperty(EAP_MODULES_REMOTE) != null) {
            addCommonLibrariesForEmbeddedAndRemote(war);
            addTestLibrariesRemote(war);
            war.addAsWebInfResource(new File("target/test-classes/remote/jboss-deployment-structure.xml"));
        } else {
            addCommonLibrariesForEmbeddedAndRemote(war);
            addTestLibrariesRemote(war);
            war.addAsLibraries(new File("target/test-libs/infinispan-remote.jar"));
        }
        return war;
    }

    public static void addCommonLibrariesForEmbeddedAndRemote(WebArchive war) {
        addCamelLibraries(war);
        addTestLibraries(war);
        war.addAsLibraries(new File("target/test-libs/cache-api.jar")); //includes classes such as CacheResult
    }

    public static void addCamelLibraries(WebArchive war) {
        war.addAsLibraries(
                new File("target/test-libs/camel-jbossdatagrid.jar"),
                new File("target/test-libs/camel-jbossdatagrid-tests.jar"),
                new File("target/test-libs/camel-core.jar"),
                new File("target/test-libs/camel-test.jar"));
    }

    public static void addTestLibraries(WebArchive war) {
        war.addAsLibraries(
                new File("target/test-libs/infinispan-core-tests.jar"),
                new File("target/test-libs/infinispan-server-testsuite-tests.jar"));
    }

    public static void addTestLibrariesRemote(WebArchive war) {
        war.addAsLibraries(new File("target/test-libs/sample-domain-implementation.jar"));
        war.addAsLibraries(new File("target/test-libs/sample-domain-definition.jar"));
    }

}
