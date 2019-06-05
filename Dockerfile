FROM openjdk:8-jdk-alpine

ARG user=alexey-egorov
ARG group=alexey-egorov
ARG uid=1000
ARG gid=1000
ARG home=/var/alexey-egorov

ENV ALEXEY_EGOROV_TOKEN_TYPE=""
ENV ALEXEY_EGOROV_TOKEN=""
ENV XMX="20M"

COPY build/libs/alexey-egorov-*-all.jar /alexey-egorov.jar

RUN mkdir -p $home \
  && chown ${uid}:${gid} $home \
  && addgroup -g ${gid} ${group} \
  && adduser -h "$home" -u ${uid} -G ${group} -s /bin/bash -D ${user} \
  && apk --update add openjdk8-jre

USER ${user}

WORKDIR ${home}

VOLUME ${home}

ENTRYPOINT java "-Xmx$XMX" -jar /alexey-egorov.jar