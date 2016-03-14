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

2) Download Red Hat JBoss Enterprise Application Platform and unzip it.

3) Install all Camel modules of this Maven repository locally from the root directory

4) Run the tests with the following command, all libraries will be bundled inside the web archive:

   mvn verify -Deap.home=/full/path/to/eap/home/directory
              -Djdg.home=/full/path/to/jdg/home/directory

   Note: This option currently does not work due to https://bugzilla.redhat.com/show_bug.cgi?id=1175272

5) Run the tests using JDG modules for EAP.

   mvn verify -Deap.home=/full/path/to/eap/home/directory
              -Djdg.home=/full/path/to/jdg/home/directory
              -Deap.modules.library=/full/path/to/eap/modules/for/library/mode
              -Deap.modules.hotrod=/full/path/to/eap/modules/for/hotrod/client