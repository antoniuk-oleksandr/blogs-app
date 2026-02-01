resource "aws_ssm_parameter" "parameters" {
  for_each = var.parameters

  name  = "/${var.project}/${var.environment}/${each.key}"
  type  = contains(var.sensitive_parameters, each.key) ? "SecureString" : "String"
  value = each.value

  tags = var.tags
}
