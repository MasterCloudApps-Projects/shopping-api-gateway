#!/usr/bin/env bash

# Create namespace
printf "\n==> Creating DEV namespace\n"
kubectl create namespace tfm-dev-amartinm82

# start api gateway container
printf "\n==> Starting API Gateway deployment and service\n"
kubectl apply -f apigateway.yml

# start ingress
printf "\n==> Starting ingress\n"
kubectl apply -f ingress.yml
