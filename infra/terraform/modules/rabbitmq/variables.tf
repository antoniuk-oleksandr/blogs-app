variable "name" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

variable "allowed_security_group_ids" {
  type = list(string)
}

variable "username" {
  type = string
}

variable "password" {
  type      = string
  sensitive = true
}

variable "engine_version" {
  type    = string
  default = "3.13"
}

variable "instance_type" {
  type    = string
  default = "mq.m5.large"
}

variable "tags" {
  type    = map(string)
  default = {}
}
