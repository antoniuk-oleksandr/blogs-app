terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.28.0"
    }
  }

  required_version = ">= 1.5.0"
}

provider "aws" {
  region                      = "eu-central-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true
  s3_use_path_style           = true

  endpoints {
    s3  = "http://localhost:4566"
  }
}

resource "aws_s3_bucket" "blog_images" {
  bucket = "blog-images"

  tags = {
    Environment = "dev"
    Project     = "blogs-app"
  }
}

resource "aws_s3_bucket_versioning" "blog_images_versioning" {
  bucket = aws_s3_bucket.blog_images.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "blog_images_lifecycle" {
  bucket = aws_s3_bucket.blog_images.id

  rule {
    id     = "ExpireOldVersions"
    status = "Enabled"

    noncurrent_version_expiration {
      noncurrent_days = 30
    }
  }
}

