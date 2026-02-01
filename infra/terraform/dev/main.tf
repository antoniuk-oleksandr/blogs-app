provider "aws" {
  region = var.region
}

module "network" {
  source = "../modules/network"
}

module "rds" {
  source                 = "../modules/rds"
  db_name                = var.db_name
  name                   = "${var.project}-${var.environment}"
  username               = var.db_username
  password               = var.db_password
  subnet_ids             = module.network.private_subnet_ids
  vpc_security_group_ids = [module.network.rds_sg_id]

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

resource "aws_ecs_cluster" "this" {
  name = "${var.project}-${var.environment}-cluster"

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

module "ssm" {
  depends_on = [module.rds, module.s3, module.cloudwatch]
  source      = "../modules/ssm"
  project     = var.project
  environment = var.environment

  parameters = {
    POSTGRES_USERNAME = var.db_username
    POSTGRES_PASSWORD = var.db_password
    POSTGRES_URL      = "jdbc:postgresql://${module.rds.endpoint}/${var.db_name}"

    AWS_REGION         = var.region
    AWS_S3_BASE_URL    = "https://${module.s3.bucket_name}.s3.${var.region}.amazonaws.com"
    AWS_S3_BUCKET_NAME = module.s3.bucket_name

    SPRING_PROFILES_ACTIVE = "prod"

    CLOUDWATCH_LOG_GROUP = module.cloudwatch.log_group_name
    LOG_STREAM_NAME      = "${var.project}-${var.environment}-stream"

    JWT_SECRET_KEY = var.jwt_secret_key
  }

  sensitive_parameters = [
    "POSTGRES_PASSWORD",
    "JWT_SECRET_KEY"
  ]

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

module "fargate" {
  depends_on         = [module.network, module.ecr, module.cloudwatch, module.s3, module.ssm]
  source             = "../modules/fargate"
  name               = "${var.project}-${var.environment}"
  cluster_id         = aws_ecs_cluster.this.id
  container_image    = module.ecr.repository_url
  container_port     = var.container_port
  subnet_ids         = module.network.public_subnet_ids
  security_group_ids = [module.network.ecs_service_sg_id]
  region             = var.region
  assign_public_ip   = true
  desired_count      = 1

  ssm_secrets = module.ssm.parameter_arns

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

module "ecr" {
  depends_on = [module.network]
  source     = "../modules/ecr"
  name       = "${var.project}-${var.environment}"
  tags = {
    Environment = var.environment
    Project     = var.project
  }
  lifecycle_rules = [
    {
      rule_priority        = 1
      description          = "Keep last 10 images"
      selection_tag_status = "any"
      max_image_count      = 10
    }
  ]
}

module "cloudwatch" {
  depends_on        = [module.network]
  source            = "../modules/cloudwatch"
  name              = "${var.project}-${var.environment}"
  retention_in_days = 30


  tags = {
    Environment = var.environment
    Project     = var.project
  }
}

data "aws_caller_identity" "current" {}

module "s3" {
  depends_on = [module.network]
  source     = "../modules/s3"

  bucket_name = "${var.project}-${var.environment}-${data.aws_caller_identity.current.account_id}"

  tags = {
    Environment = var.environment
    Project     = var.project
  }
}
