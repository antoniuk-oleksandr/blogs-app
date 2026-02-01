variable "name" {
  type = string
}

variable "cluster_id" {
  description = "ECS cluster ID where the service will be deployed"
  type        = string
}

variable "container_image" {
  type = string
}

variable "container_port" {
  type    = number
  default = 8080
}

variable "subnet_ids" {
  type = list(string)
}

variable "security_group_ids" {
  type    = list(string)
  default = []
}

variable "cpu" {
  type    = number
  default = 512
}

variable "memory" {
  type    = number
  default = 1024
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "tags" {
  type = map(string)
  default = {}
}

variable "environment_variables" {
  description = "Environment variables for the container"
  type        = map(string)
  default     = {}
}

variable "ssm_secrets" {
  description = "Map of environment variable names to SSM parameter ARNs"
  type        = map(string)
  default     = {}
}

variable "region" {
  type    = string
  default = "eu-central-1"
}

variable "assign_public_ip" {
  description = "Assign public IP to Fargate tasks (required if using public subnets without NAT)"
  type        = bool
  default     = false
}