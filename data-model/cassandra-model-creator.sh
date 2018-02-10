#!/bin/bash
for file in /tmp/*.sql;
 do
   echo "$0: $file: running..." &&
   until cqlsh -C -f "$file";
     do
       echo "$0: $file: waiting for DB startup..."; sleep 10;
     done &&
   echo "$0: $file: done"
 done &&
 echo "$0: completed" &