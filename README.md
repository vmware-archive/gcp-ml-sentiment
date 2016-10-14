# Demo of Spring Boot App Which Combines Various Google Cloud Platform Services

## Important
* Create an instance of the Storage service: `cf create-service google-storage standard gcp-storage -c '{"name" : "BUCKET_NAME" }'`
* Edit `./src/main/resources/application.properties`, assigning that bucket name to `gcp-storage-bucket`

