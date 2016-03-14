How to run tests
================

1) Make sure your settings.xml includes the following repository:

   <repository>
     <id>mead-jdg7</id>
     <name>MEAD JDG7 Maven Repository</name>
     <url>http://download.eng.bos.redhat.com/brewroot/repos/jb-dg-7-rhel-7-build/latest/maven</url>
     <releases>
       <enabled>true</enabled>
       <updatePolicy>never</updatePolicy>
     </releases>
     <snapshots>
       <enabled>false</enabled>
     </snapshots>
   </repository>

   Alternatively, use build.sh from the root directory instead of "mvn" when running tests.

2) Download Red Hat JBoss Fuse

3) Install all Camel modules locally from the root directory of this repository

4) Run the tests with the following command:

   mvn verify -Dfuse.zip=/full/path/to/jboss-fuse-full-<version>.zip 

   When using build.sh instead of mvn, the following property has to be passed to the build:
   -Dmaven.repo.local=${user.home}/.m2/jdg-mead-repository

