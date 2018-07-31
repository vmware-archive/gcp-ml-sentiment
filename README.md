# Demo of Spring Boot App Which Combines Various Google Cloud Platform Services


## To install
* Follow its instructions and deploy the image resizing service. This service is available [here](https://github.com/cf-platform-eng/image-resizing-service)
* Consider scaling it up somewhat
* Build this project
```
sh mvnw clean package
```
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
