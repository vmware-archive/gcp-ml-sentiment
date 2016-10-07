# Demo of Spring Boot app which uses the Google Cloud Platform's Machine Learning sentiment API.

So far, this just shows how to authenticate to the API using the values pulled from the
`VCAP_SERVICES` environment variable, then analyze a single phrase.

[This class](./src/main/java/io/pivotal/CredentialManager.java) has the `main()` method to use
to demonstrate this.  It needs to be reworked to actually run as a Spring Boot app.

To run this, you will need the `VCAP_SERVICES` value provided by a properly configured GCP
tile running within PCF on Google Cloud Platform, and you need to set this in the environment
for running this locally.


