variable "name" {
  type = string
}

variable "tags" {
  type = map(string)
  default = {}
}

variable "lifecycle_rules" {
  type = list(object({
    rule_priority        = number
    description          = string
    selection_tag_status = string
    max_image_count      = number
  }))
  default = []
}