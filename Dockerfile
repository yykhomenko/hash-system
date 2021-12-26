FROM openjdk:11

ENV SBT_VERSION 1.5.8

RUN curl -L -o sbt-$SBT_VERSION.zip https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.zip
RUN unzip sbt-$SBT_VERSION.zip -d ops

WORKDIR /hash-system
COPY . /hash-system

RUN /ops/sbt/bin/sbt stage

FROM openjdk:11

WORKDIR /hash-system
COPY --from=0 /hash-system/target/universal/stage ./
CMD ["./bin/hash-system"]