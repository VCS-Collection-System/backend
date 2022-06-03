#!/bin/bash

###################################################
# This script installs the kjars specified in
# additional_kjars.txt. Currently this script
# assumes that any kjars specified conform to
# "legacy" fromat, meaning their packages use
# the com.redhat convention.
###################################################

set -e

MAVEN_REPO="${MAVEN_REPO:-https://nexus.corp.redhat.com/repository/rhvcs-maven}"

function install_kjar() {
    VERSION=$1
    TAG=$2
    printf "Installing kjar as version %s from tag %s\n" $VERSION $TAG
    KJAR_POM_PATH=META-INF/maven/com.redhat/vcs-kjar
    mkdir -p $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION 
    curl -k $MAVEN_REPO/com/redhat/vcs-kjar/$TAG/vcs-kjar-$TAG.jar -o $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.jar 
    curl -k $MAVEN_REPO/com/redhat/vcs-kjar/$TAG/vcs-kjar-$TAG.pom -o $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.pom 
    sed -i "s/$TAG/$VERSION/g" $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.pom

    cd /tmp ; mkdir -p $KJAR_POM_PATH

    # Update the kie deployment descriptor in legacy kjars to use a local persistence factory instead of the spring managed one
    jar xf $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.jar  META-INF/kie-deployment-descriptor.xml
    sed -i "s|<resolver>spring</resolver>|<resolver>mvel</resolver>|g" META-INF/kie-deployment-descriptor.xml
    sed -i "s|<identifier>JPAPlaceholderResolverStrategy</identifier>|<identifier>new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(\"rhvcs-${VERSION}\", classLoader)</identifier>|g" META-INF/kie-deployment-descriptor.xml

    cat <<EOT > /tmp/META-INF/persistence.xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" xmlns:orm="http://java.sun.com/xml/ns/persistence/orm" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd">
    <persistence-unit name="rhvcs-${VERSION}" transaction-type="JTA">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <jta-data-source>java:/comp/env/jdbc/rhvcs-ds</jta-data-source>
        <class>com.redhat.vcs.model.Attachment</class>
        <class>com.redhat.vcs.model.CovidTestResultDocument</class>
        <class>com.redhat.vcs.model.Document</class>
        <class>com.redhat.vcs.model.DocumentReview</class>
        <class>com.redhat.vcs.model.DocumentTaskMapping</class>
        <class>com.redhat.vcs.model.Employee</class>
        <class>com.redhat.vcs.model.EmployeeLog</class>
        <class>com.redhat.vcs.model.PositiveTestResult</class>
        <class>com.redhat.vcs.model.VaccineCardDocument</class>
        <class>com.redhat.vcs.model.VaccineDocumentDump</class>
        <class>com.redhat.vcs.model.VaccineDocumentList</class>
        <properties>
          <property name="hibernate.hbm2ddl.auto" value="none"/>
          <property name="hibernate.show_sql" value="false"/>
        </properties>
      </persistence-unit>
</persistence>
EOT

    # Replace the pom.xml file
    cp $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.pom ${KJAR_POM_PATH}/pom.xml 

    jar uf $M2_HOME/repository/com/redhat/vcs-kjar/$VERSION/vcs-kjar-$VERSION.jar ${KJAR_POM_PATH}/pom.xml \
           META-INF/kie-deployment-descriptor.xml \
           META-INF/persistence.xml
    rm -fr $KJAR_POM_PATH 

    mkdir -p $M2_HOME/repository/com/redhat/vcs-parent/$VERSION 
    curl -k $MAVEN_REPO/com/redhat/vcs-parent/$TAG/vcs-parent-$TAG.pom -o $M2_HOME/repository/com/redhat/vcs-parent/$VERSION/vcs-parent-$VERSION.pom 
    sed -i "s/$TAG/$VERSION/g" $M2_HOME/repository/com/redhat/vcs-parent/$VERSION/vcs-parent-$VERSION.pom 
    mkdir -p $M2_HOME/repository/com/redhat/vcs-model/$VERSION 
    curl -k $MAVEN_REPO/com/redhat/vcs-model/$TAG/vcs-model-$TAG.pom -o $M2_HOME/repository/com/redhat/vcs-model/$VERSION/vcs-model-$VERSION.pom 
    curl -k $MAVEN_REPO/com/redhat/vcs-model/$TAG/vcs-model-$TAG.jar -o $M2_HOME/repository/com/redhat/vcs-model/$VERSION/vcs-model-$VERSION.jar 
    sed -i "s/$TAG/$VERSION/g" $M2_HOME/repository/com/redhat/vcs-model/$VERSION/vcs-model-$VERSION.pom 
}

function main {
    printf "Maven Repository set to %s\n\n" $MAVEN_REPO
    while read kjar_spec ; do
        [[ $kjar_spec =~ ^#.* ]] && continue
        V=$(echo $kjar_spec | cut -f 1 -d '=')
        T=$(echo $kjar_spec | cut -f 2 -d '=')
        install_kjar $V $T
    done < additional_kjars.txt
}

main
