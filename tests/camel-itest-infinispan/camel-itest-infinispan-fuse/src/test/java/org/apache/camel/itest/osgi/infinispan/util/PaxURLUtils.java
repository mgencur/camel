package org.apache.camel.itest.osgi.infinispan.util;

public class PaxURLUtils {
    public static final String PROP_PAX_URL_LOCAL_REPO = "org.ops4j.pax.url.mvn.localRepository";
    public static final String PROP_PAX_URL_REPOSITORIES = "org.ops4j.pax.url.mvn.repositories";
    public static final String SERVICEMIX_REPO = "http://svn.apache.org/repos/asf/servicemix/m2-repo@id=servicemix";
    public static final String CENTRAL_REPO = "http://repo1.maven.org/maven2@id=central";
    public static final String SPRING_REPO = "http://repository.springsource.com/maven/bundles/release@id=springsource.release";
    public static final String BREW_REPO = "http://download.eng.bos.redhat.com/brewroot/repos/jb-dg-7-rhel-7-build/latest/maven@id=redhat-jdg";
    public static final String BREW_REPO_FUSE = "http://download.eng.bos.redhat.com/brewroot/repos/jb-fuse-6-rhel-6-build/latest/maven@id=redhat-fuse";
    public static final String ALL_REPOS = BREW_REPO + "," + BREW_REPO_FUSE + "," + CENTRAL_REPO + "," + SERVICEMIX_REPO + "," + SPRING_REPO;
    private static final String JAVA_URL_HANDLERS_PROPERTY = "java.protocol.handler.pkgs";
    private static final String PAX_URL_PACKAGE = "org.ops4j.pax.url";

    public static void registerURLHandlers() {
        String protocolHandlers = System.getProperty(JAVA_URL_HANDLERS_PROPERTY);

        if (protocolHandlers == null) {
            System.setProperty(JAVA_URL_HANDLERS_PROPERTY, PAX_URL_PACKAGE);
        } else if (!protocolHandlers.contains(PAX_URL_PACKAGE)) {
            System.setProperty(JAVA_URL_HANDLERS_PROPERTY, String.format("%s|%s", protocolHandlers, PAX_URL_PACKAGE));
        }
    }

    public static void configureLocalMavenRepo() {
        String localRepo = null;
        try {
            localRepo = MavenUtils.getLocalRepository();
        } catch (Exception ex) {
        }
        if (localRepo == null) {
            return;
        }
        System.setProperty(PROP_PAX_URL_LOCAL_REPO, localRepo);
    }
}
