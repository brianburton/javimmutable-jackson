#!/bin/bash

for jack in 2.10.5.1 2.11.4 2.12.1 2.12.2 2.12.3 ; do 
  for jim in 3.0.0 3.0.1 3.0.2 3.1.0 3.2.0 3.2.1 ; do
    echo
    echo '****' version.databind=$jack  version.javimmutable=$jim
    echo
    mvn -Dversion.databind=$jack -Dversion.javimmutable=$jim clean test
  done
done
