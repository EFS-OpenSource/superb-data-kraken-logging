# SDK Logging configuration

This lib will add Azure Application Insights to the spring application. 

## Setup
### Build and Release - Manual
To set up your local environment for development and testing, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/EFS-OpenSource/superb-data-kraken-logging.git
   cd superb-data-kraken-logging
2. Build the project and install the package to your local Maven repository:
   ```bash
   ./mvnw clean install

### Build and Release - Pipeline
For building and releasing, the `azure-pipeline.yml` is used. The deployment is triggered on commit. When no git tag is set, a snapshot version will
be build and released to the snapshot repository. When you add a git tag, <mark>keep in mind, that the tag will be used as release version</mark>. 

### Configuration for Application Insights
#### Java Agent
To receive all information regarding the spring service, the agent needs to get downloaded (`azure-pipeline-template.yml`)
and added to the container. Add the following block *before* the docker build step.

```yaml
# ...
  - task: CmdLine@2
    displayName: Download Application Insights
    inputs:
      script: 'curl -L -o applicationinsights-agent.jar https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.14/applicationinsights-agent-3.4.14.jar'
  - task: Docker@2
    displayName: Build and push an image to container registry
    inputs:
# ...
```

After the download step in the pipeline, add the `applicationinsights-agent.jar` to the container and add the javaagent to the `ENTRYPOINT`.
For the application insights configuration to be found, the `APPLICATIONINSIGHTS_CONFIGURATION_FILE` environment variable needs to be added to the dockerfile aswell.
```dockerfile
# ...
ENV APPLICATIONINSIGHTS_CONFIGURATION_FILE /etc/application/applicationinsights.json
COPY applicationinsights-agent.jar applicationinsights-agent.jar
ENTRYPOINT ["java","-javaagent:/applicationinsights-agent.jar", "-jar","/app.jar","-Xmx=512M"]
```

#### Application Config
For Application Insights to work, the config file needs to be added to the container. Add a `applicationinsights.json` to the `config-map.yml`.
Adapt the role name to the application name (eg. Organizationmanager Backend). The `connectionString` is stored in the pipeline variables.
```json
{
  "connectionString": "$(app-insights-con-string)",
  "role": {
    "name": "TODO: APPLICATION NAME"
  },
  "instrumentation": {
    "logging": {
      "level": "OFF"
    },
    "micrometer": {
      "enabled": true
    }
  },
  "heartbeat": {
      "intervalSeconds": 60
  }
}
```
For the configuration to be loaded correctly. An additional entry has to be made in the `bootstrap.yml` (spring v2) or `application-kubernetes.yaml` (spring >v3).
```yaml
#...
  config:
    paths:
      - /etc/application/application.yml
      - /etc/application/applicationinsights.json
    enable-api: false
  reload:
    enabled: false
```


#### Register Logger
In order to register the logback configuration for the application, add the following to the `application.yaml` section in the `config-map.yml`.
The `azure.application-insights.instrumentation-key` as well as the `logging.config` information needs to be added.

```yaml
  azure:
    application-insights:
      instrumentation-key: $(app-insights-instrumentation-key)
  # ...
  logging:
    config: classpath:com/efs/sdk/logging/sdk.logback.xml
    level:
      com.efs.sdk: $(logging-level)
```


## Usage
To include this module in your project, simply add the following dependency declaration to the <dependencies> section of your pom.xml:

```xml
<dependency>
    <groupId>com.efs.sdk</groupId>
    <artifactId>sdk-logging</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Create a Audit Log
In order to create a audit log in the application, Use the static methods of `AuditLogger`.

### Example
```java
import com.efs.sdk.logging.AuditLogger;
//... Use either the JwtAuthenticationToken Object or the UserId as String
AuditLogger.info(LOG, "creating space {} in organization {}", token, dto.getName(), orgId);
//...
```

## Built With
Links to tools used for building. Example:
- Maven v3.6.3 (see this [Link](https://maven.apache.org/))

## Contributing
See the [Contribution Guide](./CONTRIBUTING.md).

## Changelog
See the [Changelog](./CHANGELOG.md).
