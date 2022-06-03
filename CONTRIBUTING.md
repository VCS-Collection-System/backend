# Contributing guide

Contribution guidelines are currently a work-in-progress. Developer documentation can be found below.

## Reporting an issue

@TODO

## Before you contribute

@TODO

### Code reviews

@TODO

### Coding guidelines

@TODO

### Continuous Integration

@TODO

#### Environment Definitions

All relevant application-specific environment configurations (e.g. URLs, namespaces, etc.) are outlined here: <https://docs.jboss.org/display/RHVCS/Environments>.

### Tests and Documentation

@TODO

## Setup

@TODO

### Prep your workspace

Note: You must be on VPN to connect to hosted services

You can run the following commands to get your system ready for development:

```sh
# Clone the git repository
git clone https://github.com/VCS-Collection-System/frontend.git

# Go to the git repository directory
cd vcs-frontend

# Create a directory named public if it doesn't already exist
mkdir public 
```

### Local config files needed for local development

@TODO

## Build

@TODO

### The Quickest QuickStart

If you meet all of the pre-reqs listed below, then one of the two command blocks below and be on your way...

#### Two Separate Build &amp; Deploy Steps

__For Linux Users:__

```bash
mvn clean package && buildah bud -f Containerfile -t rhvcs-backend:latest

podman run --env AWS_ACCESS_KEY_ID="rhvcs-backend-access-key" --env AWS_SECRET_ACCESS_KEY="my_aw3s0m3_passw0rd" --env s3.bucket.host=10.69.12.92 --env s3.bucket.port=9100 --env s3.bucket.region="us-east-1" --env s3.bucket.name="dev-pam-data-bucket0" --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" --env enable.s3.persistence=true -p 8090:8090 rhvcs-backend:latest
```

__For non-Linux Users:__

```bash
mvn clean package && docker build . -f Containerfile -t rhvcs-backend:latest

docker run  --env AWS_ACCESS_KEY_ID="rhvcs-backend-access-key" --env AWS_SECRET_ACCESS_KEY="my_aw3s0m3_passw0rd" --env s3.bucket.host=host.docker.internal --env s3.bucket.port=9100 --env s3.bucket.region="us-east-1" --env s3.bucket.name="dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7" --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" --env enable.s3.persistence=true --env logging.level.com.redhat.service=debug --env keycloak.resource=rhvcs-backend-devtest --env keycloak.public_client="false" --env keycloak_client_secret="fbee80dc-d306-43e9-8ed2-ee2036b41985" --env keycloak.credentials.secret="fbee80dc-d306-43e9-8ed2-ee2036b41985" --env logging.level.org.kie.server.services=debug --env aas.enabled=false --env aas.proofs.endpoint="https://auto-approver.example.com/proofs" --env SPRING_DATASOURCE_USERNAME="vcs_devtest_user" --env SPRING_DATASOURCE_PASSWORD="vcsdevtest_user_T5es8" -p 8090:8090 rhvcs-backend:latest
```

#### All-In-One Build &amp; Deploy

__For Linux Users:__

```bash
mvn clean package && buildah bud -f Containerfile -t rhvcs-backend:latest && podman run --env AWS_ACCESS_KEY_ID="rhvcs-backend-access-key" --env AWS_SECRET_ACCESS_KEY="my_aw3s0m3_passw0rd" --env s3.bucket.host=10.69.12.92 --env s3.bucket.port=9100 --env s3.bucket.region="us-east-1" --env s3.bucket.name="dev-pam-data-bucket0" --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" --env enable.s3.persistence=true -p 8090:8090 rhvcs-backend:latest
```

__For non-Linux Users:__

```bash
mvn clean package && docker build . -f Containerfile -t rhvcs-backend:latest && docker run  --env AWS_ACCESS_KEY_ID="rhvcs-backend-access-key" --env AWS_SECRET_ACCESS_KEY="my_aw3s0m3_passw0rd" --env s3.bucket.host=host.docker.internal --env s3.bucket.port=9100 --env s3.bucket.region="us-east-1" --env s3.bucket.name="dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7" --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" --env enable.s3.persistence=true --env logging.level.com.redhat.service=debug --env keycloak.resource=rhvcs-backend-devtest --env keycloak.public_client="false" --env keycloak_client_secret="fbee80dc-d306-43e9-8ed2-ee2036b41985" --env keycloak.credentials.secret="fbee80dc-d306-43e9-8ed2-ee2036b41985" --env logging.level.org.kie.server.services=debug --env aas.enabled=false --env aas.proofs.endpoint="https://auto-approver.example.com/proofs" --env SPRING_DATASOURCE_USERNAME="vcs_devtest_user" --env SPRING_DATASOURCE_PASSWORD="vcsdevtest_user_T5es8" -p 8090:8090 rhvcs-backend:latest
```

### Environment Setup / Pre-requisites

Below are the documented settings of what worked for me...

#### Maven

Working version: >3.6.3

To check your version, run `mvn -v`

To update your version, either use your preferred standard update method or if you do not have an opinionated development environment you can use [SDKMAN](https://sdkman.io/). For example, after installing SDKMAN run the following to install Maven:

`sdk install maven`

This should install the latest available version of Maven, which should be fine for our purposes.

#### Java-JDK

Working version: 11.0.11 (2021-04-20)

To check your version, run `javac --version`

To update your version of the Java JDK, follow the options as described in the Maven section above^^.

#### Buildah (For Linux users only)

Working version: 1.21.4

To check your version, run `buildah -v`

To update your version of the Buildah, use your Linux package manager (e.g. `dnf update buildah`).

:information_source: `podman` provides the same functionality.

#### Podman (For Linux users only)

Working version: 3.2.3

To check your version, run `podman -v`

To update your version of the Podman, use your Linux package manager (e.g. `dnf update podman`).

#### Docker (For non-Linux Users only)

Working version: 20.10.11

To check your version, run `docker -v`

To install/update docker, see the [Docker website](https://www.docker.com/get-started).

#### S3-Compatible Storage (MinIO)

For local development, it is recommended to deploy a container running [MinIO](https://hub.docker.com/r/minio/minio/).

```bash
mkdir minio-data

podman run \
    -p 9100:9000 -p 9001:9001 \
    --name minio1 -d --rm \
    -v minio-data:/data \
    minio/minio server /data \
    --console-address ":9001"
```

:warning: Please note that by default the MinIO container runs on the same port as the frontend deployment for local development (port 9000). The mapping local ports have been adjust above to account for this, but if you are running both simultaneously, you may need to adjust one deployment or the other if issues arise.

Default credentials for this tool is `minioadmin:minioadmin`.

Setup instructions:

1. Login to <127.0.0.1:9100> using the default credentials.
2. Create an S3 bucket: Select "Buckets" on the left-hand menu bar and then "Create Bucket" in the top right of the screen. Set the Bucket Name to `dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7` and click "Save".
3. Create a new user: Select "Users" on the left-hand menu bar and then "Create User". Set the Access Key to `rhvcs-backend-access-key` and Secret Key to `my_aw3s0m3_passw0rd`. Click "Save". :warning: Note: Do not try to be fancy here with a super secure password - there are character limitations in the AWS CLI, so it's suggested to stick to letters and numbers for simplicity.

podman run --env AWS_ACCESS_KEY_ID="rhvcs-backend-access-key" --env AWS_SECRET_ACCESS_KEY="my_aw3s0m3_passw0rd" --env s3.bucket.host=10.69.12.92 --env s3.bucket.port=9100 --env s3.bucket.region="us-east-1" --env s3.bucket.name="dev-pam-data-bucket0" --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" --env enable.s3.persistence=true -p 8090:8090 rhvcs-backend:latest

Assign Console Admin IAM policy because it has all available access

Note on changes:

- Renamed everything to rhvcs
- Removed nc_vax_aas that was preventing kjar container build
- https changed to http in application at S3Service.java; need to modify something (minio or app)
- instead of BUCKET_HOST (etc.), using s3.bucket.host; need to correct
- exposing port 8090 on 8090

Get your host IP address via `ip addr`
Use that as S3 host

For local debugging, set the following environment variables: `--env logging.level.com.redhat.rhvcs.service=debug --env logging.level.org.kie.server.services=debug`

Currently there are errors when launching the application/building the KJAR. These _may_ not be critical, but should be reviewed/resolved by someone with PAM expertise. The file causing this error has been renamed with an appended `.txt` to exclude it from the build process. See the original error message below...

```bash
Error creating container 'rhvcs-kjar-1_0-SNAPSHOT' for module 'com.redhat:rhvcs-kjar:1.0-SNAPSHOT'
```

Swagger UI is NOT currently available at ${backend-URL}/swagger-ui

##### Testing your local S3 connection

Download and install S3cmd

Configure with the following settings:

- Access Key [rhvcs-backend-access-key]
- Secret Key [my_aw3s0m3_passw0rd]
- Default Region [us-east-1]
- S3 Endpoint [127.0.0.1:9100]
- bucket [dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7]
- Encryption password: *leave empty*
- Path to GPG program: *leave empty*
- Use HTTPS protocol [No]
- HTTP Proxy server name: *leave empty*
- Test access with supplied credentials: Y

Save your settings if the test completed successfully.

Upload a file to the bucket: `s3cmd put eagle.gif s3://dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7`

Verify the image is available via the MinIO web console: <http://127.0.0.1:9100/buckets/dev-pam-data-bucket-1292c097-c98c-46ce-b56d-ee83b69d2df7/browse>

### Quick Start

Assuming you have the appropriate dependencies installed, run the following commands *from the project root directory*:

1. Build the application artifact: `mvn clean package`. After a successful build, you should see a JAR in `service/target` directory.
2. Build the container image:

- With Podman: `podman build -f Containerfile -t rhvcs-backend:latest`. After a successful build, you should be able to run `buildah images` and see a new image for `localhost/rhvcs-backend` with a tag of `latest`.
- With Buildah: `buildah bud -f Containerfile -t rhvcs-backend:latest`. After a successful build, you should be able to run `buildah images` and see a new image for `localhost/rhvcs-backend` with a tag of `latest`.

3. Run the application container: `podman run --env keycloak.auth-server-url="https://auth.test.vcs.example.com/auth" --env keycloak.realm="rhvcs-devtest" -p 8090:8090 rhvcs-backend:latest` ...but add the necessary/desired environment variables and configurations. :-)

Note: Visit <http://localhost:8090/> to spot check that the application was successfully deployed.

### Individual Components

If you find that you need to build/run individual components for *some* reason, follow the (currently incomplete - please update as you find gaps) instructions below.

#### Model

:warning: This is the base of the entire application. If this gets updated, you'll need to update all other parts accordingly.

To made the model available for all other components, you need to package it and store it in a location accessible by the other components. Fortunately Maven handles this for us via the `install` command.

Run `mvn clean install` and on success your model should be available for use by the other components.

#### KJAR

@TODO

#### Service

@TODO

### Kjar deployment

:important: The kjar version needs to be incremented everytime changes are made to the process.

:Use case: If we need to deploy two versions of a same process definition, we can increment the version and deploy both the containers to kie server. whenever we are deploying a newer version of kjar, to handle the exisitng tasks that are from the previous version, we should deploy both the old and new containers, that way users can still process old tasks as well as new tasks.

#### Deploying multiple kjar versions to kie server

1. add the container names, versions in rhvcs-service.xml. container alias can be used to call the containers from front end/ any client. The kie server uses the alias and routes the requests to appropriate container. 
2. If a container(old version) needs to be fetched from an artifactory, add the version in additional_kjars.txt

#### Process instance migration

If the version was not incremented and new changes in process with human task are deployed, the remaining tasks in older version will be inaccessible. To bring these back we can use process instance migration.

1. increment the version to 1.2 and deploy
2. deploy previous version (1.1) along with a new container 2.0
3. using swagger(PUT endpoint `server/admin/containers/{containerId}/processes/instances`) migrate all the inaccessible process instances from 1.1 to 2.0
4. deploy containers 1.2(fetch from artifactory) and 2.0 together

:warning: some data fields in the task table might be lost during process instance migration which needs to be identified and handled

## Test

To run various tests, choose from the commands below:

@TODO

## Documentation

@TODO

## Frequently Asked Questions (FAQs)

@TODO
