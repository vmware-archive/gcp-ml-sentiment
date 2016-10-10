#!/bin/bash

cf push gcp-next-demo -n gcp-next -b java_buildpack_offline -p ./target/gcp-ml-nlp-0.0.1-SNAPSHOT.jar --no-start
echo "Once this has been pushed, you can bind it to the service, then run 'cf start gcp-next-demo'"

