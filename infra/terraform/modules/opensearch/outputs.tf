output "endpoint" {
  value = aws_opensearch_domain.this.endpoint
}

output "https_url" {
  value = "https://${aws_opensearch_domain.this.endpoint}"
}

output "security_group_id" {
  value = aws_security_group.this.id
}
