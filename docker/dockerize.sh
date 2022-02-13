#!/usr/bin/env bash

DOCKER_LOCAL_IMAGE=$USER/tfm-apigw:$(mvn -f ../pom.xml help:evaluate -Dexpression=project.version -q -DforceStdout)-dev
echo "DOCKER_LOCAL_IMAGE=${DOCKER_LOCAL_IMAGE}">".env"
echo "DOCKER_HOST_IP=$(hostname -I | awk '{print $1}')">>".env"

# Compile image to work locally
printf "\n==> Compile app image for locally purposes with name '%s', using Dockerfile\n" $DOCKER_LOCAL_IMAGE
mvn -f ../pom.xml jib:dockerBuild -Dimage=$DOCKER_LOCAL_IMAGE

# Start locally created container
printf "\n==> Start locally container and its dependencies using docker-compose\n"
docker-compose up

exit 0
