FROM insidius/alpine-oracle-jdk8-bash

COPY ./build/install/hash-system /hash-system
WORKDIR /hash-system

RUN chown -R daemon:daemon .
RUN mkdir -p logs
RUN chown -R daemon:daemon logs

EXPOSE 8079 8080

USER daemon

ENV JAVA_OPTS "-Xms10g -Xmx16g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8079"
CMD ["bin/hash-system"]