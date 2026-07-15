variable "name" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

variable "region" {
  type = string
}

variable "allowed_security_group_ids" {
  type = list(string)
}

variable "engine_version" {
  type    = string
  default = "OpenSearch_2.17"
}

variable "instance_type" {
  type    = string
  default = "t3.small.search"
}

variable "volume_size" {
  type    = number
  default = 10
}

variable "tags" {
  type    = map(string)
  default = {}
}
