#!/bin/bash

#app_name="gcp-next-demo"
app_name="landmarks"

# This should be sufficient to enable ML and BigQuery, along with GCS (Storage)
service="storage-mike"

# Additional configuration parameters: varies depending on which service
# Ref. https://cloud.google.com/iam/docs/understanding-roles
#
# ML, BigQuery:  -c '{"role": "viewer"}'
# Storage (GCS): -c '{"role": "editor"}'

cf bind-service $app_name $service -c '{"role": "editor"}'

