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
    s3         = "http://localhost:4566"
    cloudwatch = "http://localhost:4566"
    logs       = "http://localhost:4566"
  }
}

module "s3" {
  source = "../modules/s3"

  bucket_name = "blogs-app"

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

module "cloudwatch" {
  source            = "../modules/cloudwatch"
  name              = "${var.project}-${var.environment}"
  retention_in_days = 30
  tags = {
    Environment = var.environment
    Project     = var.project
  }
}
