variable "project" {
  type    = string
  default = "blogs-app"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "container_image" {
  type        = string
  default = "some-account-id.dkr.ecr.eu-central-1.amazonaws.com/blogs-app-dev:latest"
  description = "Full URL of the container image in ECR (repository_url:tag)"
}

variable "container_port" {
  type    = number
  default = 8080
}

variable "db_username" {
  type    = string
  default = "admin"
}

variable "db_password" {
  type    = string
  default = "password123"
}

variable "db_name" {
  type    = string
  default = "blogsdb"
}

variable "jwt_secret_key" {
  type        = string
  default = "someverysecureandlongsecretkeyvalue123"
  description = "Secret key used for signing JWT tokens"
}

variable "region" {
  type    = string
  default = "eu-central-1"
}
