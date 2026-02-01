variable "project" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "parameters" {
  description = "Map of parameter names to values"
  type        = map(string)
}

variable "sensitive_parameters" {
  description = "List of parameter keys that should be stored as SecureString"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}
