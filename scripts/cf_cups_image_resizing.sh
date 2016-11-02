#!/bin/bash

# NOTE: the url value correlates with the value shown for this app when
# you run `cf apps'
cf cups image-resizing-service -p '{ "url": "http://image-resizing-service.apps.pcf-on-gcp.com" }'

