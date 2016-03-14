package org.apache.camel.itest.osgi.infinispan.util;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.AbstractUrlProvisionOption;
import org.ops4j.pax.exam.options.RawUrlReference;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

public class IspnKarafOptions {

    private static final String PROP_VERSION_KARAF = "version.karaf";
    private static final String PROP_VERBOSE_KARAF = "verbose.karaf";
    private static final String PROP_FUSE_ZIP = "fuse.zip";
    private static final String RESOURCES_DIR = System.getProperty("resources.dir", System.getProperty("java.io.tmpdir"));

    public static Option commonOptions() throws Exception {
        return composite(fuseContainer(),
                vmOptions("-Djava.net.preferIPv4Stack=true", "-Djgroups.bind_addr=127.0.0.1", "-Dinfinispan.accurate.bulk.ops=true"),
                verboseFuse(),
                junitBundles(),
                keepRuntimeFolder(),
                localRepoForPAXUrl(),
                infinispanServerTestSuite(),
                queryDomainObjects()
        );
    }

    public static Option verboseFuse() {
        Option result = null;
        if (Boolean.parseBoolean(System.getProperty(PROP_VERBOSE_KARAF))) {
            result = logLevel(LogLevelOption.LogLevel.TRACE);
        }
        ;
        return result;
    }

    public static Option fuseContainer() throws Exception {
        String karafVersion = karafVersion();
        String zip = System.getProperty(PROP_FUSE_ZIP);
        if (zip == null) {
            throw new RuntimeException("Parameter -Dfuse.zip specifying a full path to Fuse distribution must be specified!");
        }
        String containerZipUrl = "file://" + zip;
        return karafDistributionConfiguration()
                .frameworkUrl(containerZipUrl)
                .useDeployFolder(false)
                .karafVersion(karafVersion)
                .unpackDirectory(new File("target/pax"));
    }

    /**
     * Specifies the internal Karaf version within Fuse.
     */
    public static String karafVersion() throws Exception {
        String karafVersion = System.getProperty(PROP_VERSION_KARAF);
        if (karafVersion == null) {
            InputStream ins = IspnKarafOptions.class.getResourceAsStream("/META-INF/maven/dependencies.properties");
            Properties p = new Properties();
            try {
                p.load(ins);
            } catch (Throwable t) {
            }
            karafVersion = p.getProperty("org.apache.karaf/apache-karaf/version");
        }
        if (karafVersion == null) {
            karafVersion = "2.3.0";
        }
        return karafVersion;
    }

    /**
     * PAX URL needs to know the location of the local maven repo to resolve mvn: URLs. When
     * running the tests on the CI machine TeamCity passes a custom local repo location using
     * -Dmaven.repo.local to isolate the build targets and PAX URL is not aware there's a
     * custom repo to be used and tries to load from the default local repo location.
     * <p/>
     * This option will pass the location specified using -Dmaven.repo.local to the appropriate
     * system property of the container.
     *
     * @return an Option or null if no custom repo location is specified by the maven build.
     * @throws Exception
     */
    public static Option localRepoForPAXUrl() throws Exception {
        String localRepo = MavenUtils.getLocalRepository();
        if (localRepo == null) {
            return null;
        }

        return composite(systemProperty(PaxURLUtils.PROP_PAX_URL_LOCAL_REPO).value(localRepo),
                         editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg", PaxURLUtils.PROP_PAX_URL_REPOSITORIES, PaxURLUtils.ALL_REPOS));
    }

    public static Option mvnFeature(String groupId, String artifactId, String feature, String version) {
        if (version == null) {
            return features(maven().groupId(groupId).artifactId(artifactId).type("xml")
                .classifier("features").versionAsInProject(), feature);
        } else {
            return features(maven().groupId(groupId).artifactId(artifactId).type("xml")
                .classifier("features").version(version), feature);
        }
    }

    public static Option camelTestOptions() {
        return composite(mavenBundle().groupId("org.apache.camel").artifactId("camel-test").type("jar").versionAsInProject(),
                         mavenBundle().groupId("org.apache.camel").artifactId("camel-core-osgi").type("jar").versionAsInProject());
    }

    public static Option queryDomainObjects() {
        return features(new RawUrlReference("file:///" + RESOURCES_DIR.replace("\\", "/")
                + "/test-features.xml"), "query-sample-domain");
    }

    public static Option infinispanServerTestSuite() {
        return features(new RawUrlReference("file:///" + RESOURCES_DIR.replace("\\", "/")
                + "/test-features.xml"), "infinispan-server-testsuite");
    }

    public static Option camelInfinispanAndTests() throws Exception {
        return composite(mvnFeature("org.apache.camel", "camel-infinispan", "camel-infinispan", null),
                mvnTestsAsFragmentBundle("org.apache.camel", "camel-infinispan", "org.apache.camel.camel-infinispan"),
                mvnTestsAsFragmentBundle("org.infinispan", "infinispan-core", "org.infinispan.core"));
    }

    public static Option camelHotRodClient() throws Exception {
        return mvnFeature("org.infinispan", "infinispan-remote", "infinispan-remote", null);
    }

    /**
     * Wraps the specified test jars as bundles fragments and attaches them to the specified host bundle.
     * The host bundle must be the one exporting the packages contained in the test jar.
     *
     * @param groupId
     * @param artifactId
     * @param hostBundle
     * @return
     * @throws Exception
     */
    public static WrappedUrlProvisionOption mvnTestsAsFragmentBundle(String groupId, String artifactId, String hostBundle, String... instructions) throws Exception {
        PaxURLUtils.registerURLHandlers();

        UrlProvisionOption testBundle = testJarAsStreamBundle(groupId, artifactId);

        String[] allInstructions = Arrays.copyOf(instructions, instructions.length + 1);
        allInstructions[instructions.length] = String.format("Fragment-Host=%s", hostBundle);

        return wrappedBundle(testBundle).instructions(allInstructions);
    }

    private static UrlProvisionOption testJarAsStreamBundle(String groupId, String artifactId) throws Exception {
        return asStreamBundle(maven().groupId(groupId).artifactId(artifactId).type("jar").classifier("tests").versionAsInProject().getURL());
    }

    public static UrlProvisionOption asStreamBundle(String url) throws MalformedURLException, IOException {
        return asStreamBundle(url, "%s");
    }

    /**
     * Some PAX-URL protocols are not supported by Karaf. This method can be used when one of the unsupported protocol
     * is required. The URLs are resolved outside Karaf and the bundles are provided as stream bundles.
     *
     * @param url
     * @param newURLFormat
     * @param args
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static UrlProvisionOption asStreamBundle(String url, String newURLFormat, String... args) throws MalformedURLException, IOException {
        InputStream in = new URL(String.format(newURLFormat, url, args)).openStream();
        try {
            return streamBundle(in);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
        }
    }
}
