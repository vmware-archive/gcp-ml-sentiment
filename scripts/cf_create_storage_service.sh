#!/bin/bash

cf create-service google-storage standard gcp-storage -c '{"name" : "gcp-next-demo" }'

