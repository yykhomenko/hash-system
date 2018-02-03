FROM insidius/alpine-oracle-jdk8-bash

COPY ./build/install/hash-system /hash-system
WORKDIR /hash-system

RUN chown -R daemon:daemon .
RUN mkdir -p logs
RUN chown -R daemon:daemon logs

EXPOSE 9079 9080

USER daemon

ENV JAVA_OPTS "-Xms8g -Xmx10g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8079"
CMD ["bin/hash-system"]