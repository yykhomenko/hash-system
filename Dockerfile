FROM openjdk:11-jre AS packager

ENV SBT_VERSION 1.5.8

RUN curl -L -o sbt-$SBT_VERSION.zip https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops

WORKDIR /hash-system
COPY . /hash-system

RUN /ops/sbt/bin/sbt stage

FROM openjdk:11-jre AS runner

WORKDIR /hash-system
COPY --from=packager /hash-system/target/universal/stage ./
EXPOSE 8080
CMD ["./bin/hash-system"]