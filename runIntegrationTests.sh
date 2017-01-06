#!/bin/bash
set -x

VERSION="7.0.0"
MILESTONE=""

wget -q -N http://download-ipv4.eng.brq.redhat.com/released/JBossDG/7.0.0/jboss-datagrid-7.0.0-server.zip
wget -q -N http://download.eng.bos.redhat.com/rcm-guest/staging/jboss-dg/JDG-$VERSION-$MILESTONE/jboss-datagrid-$VERSION.$MILESTONE-eap-modules-library.zip
wget -q -N http://download.eng.bos.redhat.com/rcm-guest/staging/jboss-dg/JDG-$VERSION-$MILESTONE/jboss-datagrid-$VERSION.$MILESTONE-eap-modules-remote-java-client.zip

wget -q -N http://download.eng.brq.redhat.com/released/JBossFuse/6.3.0/jboss-fuse-karaf-6.3.0.redhat-187.zip
wget -q -N http://download-ipv4.eng.brq.redhat.com/released/JBEAP-7/7.0.3/jboss-eap-7.0.3-full-build.zip

rm -rf jboss-datagrid-7.0.0-server
unzip -q jboss-datagrid-7.0.0-server.zip

unzip -q -o jboss-datagrid-$VERSION.$MILESTONE-eap-modules-library.zip
unzip -q -o jboss-datagrid-$VERSION.$MILESTONE-eap-modules-remote-java-client.zip
unzip -q -o jboss-eap-7.0.3-full-build.zip

jboss-fuse-karaf-6.3.0.redhat-187.zip

mvn -f tests/pom.xml -s maven-settings.xml -Dmaven.test.failure.ignore=true -Dsurefire.timeout=600 clean install \
   -Dfuse.zip=`pwd`/jboss-fuse-full-6.2.1.redhat-084.zip \
   -Deap.home=`pwd`/jboss-eap-7.0 \
   -Djdg.home=`pwd`/jboss-datagrid-$VERSION-server \
   -Deap.modules.library=`pwd`/jboss-datagrid-$VERSION-eap-modules-library \
   -Deap.modules.hotrod=`pwd`/jboss-datagrid-$VERSION-eap-modules-remote-java-client \
   -Dmaven.repo.local=${HOME}/.m2/jdg-mead-repository 

