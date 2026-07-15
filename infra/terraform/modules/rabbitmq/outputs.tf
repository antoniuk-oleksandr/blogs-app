locals {
  amqps_endpoint = aws_mq_broker.this.instances[0].endpoints[0]
  host_with_port = replace(local.amqps_endpoint, "amqps://", "")
}

output "endpoint" {
  value = local.amqps_endpoint
}

output "host" {
  value = split(":", local.host_with_port)[0]
}

output "port" {
  value = 5671
}

output "security_group_id" {
  value = aws_security_group.this.id
}
