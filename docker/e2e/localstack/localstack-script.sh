#!/bin/bash

AWS_REGION=eu-central-1
BUCKET_NAME=backity

create_s3_bucket() {
  local bucket_name=$1

  awslocal s3api create-bucket --bucket "$bucket_name" \
    --create-bucket-configuration LocationConstraint="$AWS_REGION" \
    --region "$AWS_REGION"
}

create_s3_bucket "$BUCKET_NAME"