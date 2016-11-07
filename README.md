# Demo of Spring Boot App Which Combines Various Google Cloud Platform Services

## Important
* Create an instance of the Storage service: `cf create-service google-storage standard gcp-storage -c '{"name" : "BUCKET_NAME" }'`
* Edit `./src/main/resources/application.properties`, assigning that bucket name to `gcp-storage-bucket`

## If using the simple image resizing service
* This service is available [here](https://github.com/cf-platform-eng/image-resizing-service)
* Follow its instructions and deploy it
* Consider scaling it up somewhat
* Edit `./src/main/resources/application.properties` in this project, setting the `image-resizing-service-url` key to the URL of your deployed image resizing service (e.g. `image-resizing-service-url=http://image-resizing-service.apps.yourdomain.com`)

