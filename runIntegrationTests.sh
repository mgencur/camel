#!/bin/bash
set -x

VERSION="7.0.0"
MILESTONE="DR2"

wget -q -N http://download.eng.bos.redhat.com/rcm-guest/staging/jboss-dg/JDG-$VERSION-$MILESTONE/jboss-datagrid-$VERSION.$MILESTONE-server.zip
wget -q -N http://download.eng.bos.redhat.com/rcm-guest/staging/jboss-dg/JDG-$VERSION-$MILESTONE/jboss-datagrid-$VERSION.$MILESTONE-eap-modules-library.zip
wget -q -N http://download.eng.bos.redhat.com/rcm-guest/staging/jboss-dg/JDG-$VERSION-$MILESTONE/jboss-datagrid-$VERSION.$MILESTONE-eap-modules-remote-java-client.zip

wget -q -N http://download.eng.brq.redhat.com/released/JBossFuse/6.2.1/jboss-fuse-full-6.2.1.redhat-084.zip
wget -q -N http://download.eng.brq.redhat.com/released/JBEAP-7/7.0.0-Beta/jboss-eap-7.0.0.Beta.zip

unzip -q -o jboss-datagrid-$VERSION.$MILESTONE-server.zip
unzip -q -o jboss-datagrid-$VERSION.$MILESTONE-eap-modules-library.zip
unzip -q -o jboss-datagrid-$VERSION.$MILESTONE-eap-modules-remote-java-client.zip
unzip -q -o jboss-eap-7.0.0.Beta.zip

mvn -f tests/pom.xml -s maven-settings.xml -Dmaven.test.failure.ignore=true -Dsurefire.timeout=600 clean install \
   -Dfuse.zip=`pwd`/jboss-fuse-full-6.2.1.redhat-084.zip \
   -Deap.home=`pwd`/jboss-eap-7.0 \
   -Djdg.home=`pwd`/jboss-datagrid-$VERSION-server \
   -Deap.modules.library=`pwd`/jboss-datagrid-$VERSION-eap-modules-library \
   -Deap.modules.hotrod=`pwd`/jboss-datagrid-$VERSION-eap-modules-remote-java-client \
   -Dmaven.repo.local=${HOME}/.m2/jdg-mead-repository 

