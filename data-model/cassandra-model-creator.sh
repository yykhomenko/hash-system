#!/bin/bash
for f in /tmp/*.sql;
 do
  case "$f" in
   *.sql)
    echo "$0: running $f" && until cqlsh -f "$f"; do >&2 echo "Cassandra is unavailable - sleeping"; sleep 2; done & ;;
  esac
   echo
done