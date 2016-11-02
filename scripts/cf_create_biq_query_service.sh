#!/bin/bash

cf create-service google-bigquery default gcp-big-query -c '{ "name" : "pcf_on_gcp" }'

