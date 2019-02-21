#!/bin/bash
for file in $(dirname $0)/sql/*.sql;
 do
   echo "$(basename $0): $(basename ${file}): loading..." &&
   until $1/bin/cqlsh -C -f "$file";
     do
       echo "$(basename $0): $(basename ${file}): waiting for DB startup..."; sleep 10;
     done &&
   echo "$(basename $0): $(basename ${file}): done"
 done &&
 echo "$(basename $0): completed"