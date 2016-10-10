#!/bin/bash

#service="gcp-ml"
service="gcp-ml-cups" # User-provided service (interim solution)

# roles/viewer: Permissions for read-only actions that preserve state.
# Ref. https://cloud.google.com/iam/docs/understanding-roles

cf bind-service gcp-next-demo $service -c '{"role": "viewer"}'

