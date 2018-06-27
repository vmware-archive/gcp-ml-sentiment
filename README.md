* Create the necessary services, names are important since we look a service by its name inside our code:
```
    cf create-service google-storage standard gcp-storage
    cf create-service google-ml-apis default gcp-ml
    cf create-service google-bigquery default gcp-bigquery
```
* Push the application with --no-start parameter
```
    cf push --no-start
```
* Bind the services with right roles
```
    cf bind-service vanguard gcp-bigquery -c '{"role": "bigquery.user"}'
    cf bind-service vanguard gcp-storage -c '{"role": "storage.admin"}'
    cf bind-service vanguard gcp-ml -c '{"role": "ml.developer"}'
```
* Start the application
```
    cf start vanguard
```
