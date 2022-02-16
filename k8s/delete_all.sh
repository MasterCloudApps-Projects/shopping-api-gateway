#!/usr/bin/env bash

# delete ingress
printf "\n==> Deleting ingress\n"
kubectl delete -f ingress.yml

# delete users container
printf "\n==> Deleting API Gateway deployment and service\n"
kubectl delete -f apigateway.yml

# delete namespace
printf "\n==> Deleting DEV namespace\n"
kubectl delete namespace tfm-dev-amartinm82
