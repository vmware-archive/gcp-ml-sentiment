# Demo of Spring Boot App Which Combines Various Google Cloud Platform Services

## Instruction to install
* Deploy the simple image resizing service
* Change the `manifest.yml` in this project to reflect the image service url
```yaml
    ---
    applications:
    - name: landmark
      path: target/gcp-ml-nlp-0.0.1-SNAPSHOT.jar
      buildpack: java_buildpack_offline
      memory: 1G
      env:
        IMAGE_RESIZING_SERVICE_URL: http://image-resizing-service.apps.yourdomain.com
        GOOGLE_MAPS_API_KEY: <INSERT YOUR GOOGLE_MAPS_API_KEY>
```
* Create the necessary services
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
    cf bind-service landmark gcp-bigquery -c '{"role": "bigquery.user"}'
    cf bind-service landmark gcp-storage -c '{"role": "storage.admin"}'
    cf bind-service landmark gcp-ml -c '{"role": "ml.developer"}'
```
* Start the application
```
    cf start landmark
```

## If using the simple image resizing service
* This service is available [here](https://github.com/cf-platform-eng/image-resizing-service)
* Follow its instructions and deploy it
* Consider scaling it up somewhat
* Edit `./src/main/resources/application.properties` in this project, setting the `image-resizing-service-url` key to the URL of your deployed image resizing service (e.g. `image-resizing-service-url=http://image-resizing-service.apps.yourdomain.com`)
