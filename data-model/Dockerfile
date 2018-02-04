FROM cassandra:3.11.1

COPY sql/*.sql /tmp/
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY cassandra-model-creator.sh /

# make my modified enTrypoinT execuTable
RUN chmod a+x docker-entrypoint.sh
RUN chmod a+x cassandra-model-creator.sh