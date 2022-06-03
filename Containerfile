FROM registry.access.redhat.com/ubi8/openjdk-11

USER 0

##############################
# vulenerability remediation #
##############################
# IMPORTANT: need to exclude filesystem due to https://bugzilla.redhat.com/show_bug.cgi?id=1708249#c31
RUN printf "[main]\nexcludepkgs=filesystem" > /etc/dnf/dnf.conf && \
    microdnf update -y && \
    microdnf clean all && \
    rm -rf /var/cache /var/log/dnf* /var/log/yum.*

# NOTE / WARNING / IMPORTANT:
#   work around for https://bugzilla.redhat.com/show_bug.cgi?id=1798685
RUN rm -f /var/log/lastlog

###############
# install app #
###############
ENV M2_HOME=$HOME/.m2
RUN mkdir /app $HOME/config
ADD service/target/*.jar /app/app.jar
ADD kjar/target/*.jar /app/kjar.jar
ADD model/target/*.jar /app/model.jar
ADD pom.xml /app
ADD model/pom.xml /app/pom-model.xml
ADD kjar/pom.xml /app/pom-kjar.xml
ADD install_additional_kjars.sh additional_kjars.txt /tmp

RUN chown -R jboss:0 /app && chmod -R 775 /app && \
    chown -R jboss:0 $HOME && \
    chmod -R 775 $HOME
USER jboss
ADD service/rhvcs-service.xml $HOME

ENV KJAR_POM_PATH=META-INF/maven/com.redhat/vcs-kjar
RUN cd /tmp ; mkdir -p $KJAR_POM_PATH ; cp /app/pom-kjar.xml ${KJAR_POM_PATH}/pom.xml && \
    jar uf /app/kjar.jar ${KJAR_POM_PATH}/pom.xml && \
    rm -fr $KJAR_POM_PATH && \
    mvn install:install-file \
        -Dfile=/app/kjar.jar \
        -DgroupId=com.redhat \
        -DartifactId=vcs-kjar \
        -Dversion=2.0.0 \
        -Dpackaging=jar \
        -DpomFile=/app/pom-kjar.xml \
        -DgeneratePom=true && \   
    ./install_additional_kjars.sh && \
    mvn install:install-file \
        -Dfile=/app/model.jar \
        -DpomFile=/app/pom-model.xml && \
    mvn install:install-file \
        -DpomFile=/app/pom.xml \
        -Dfile=/app/pom.xml && \
    chown -R jboss:0 $HOME/.m2 && \
    chmod -R 775 $HOME/.m2 && \
    rm /app/kjar.jar /app/model.jar /app/pom.xml /app/pom-model.xml /app/pom-kjar.xml

EXPOSE 8090
VOLUME /$HOME/config

###########
# run app #
###########
USER 1001
ENTRYPOINT ["java", "-jar","-Dorg.kie.persistence.postgresql.useBytea=true","-Duser.home=/home/jboss"]
CMD ["/app/app.jar"]
