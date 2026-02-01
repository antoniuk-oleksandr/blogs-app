output "parameter_arns" {
  description = "Map of parameter names to ARNs"
  value = {
    for k, v in aws_ssm_parameter.parameters : k => v.arn
  }
}

output "parameter_names" {
  description = "Map of parameter names to full SSM parameter names"
  value = {
    for k, v in aws_ssm_parameter.parameters : k => v.name
  }
}
