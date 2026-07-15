resource "aws_security_group" "this" {
  name        = "${var.name}-rabbitmq-sg"
  description = "Allow RabbitMQ access from ECS"
  vpc_id      = var.vpc_id

  ingress {
    from_port       = 5671
    to_port         = 5671
    protocol        = "tcp"
    security_groups = var.allowed_security_group_ids
    description     = "RabbitMQ AMQPS from ECS"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name}-rabbitmq-sg"
  })
}

resource "aws_mq_broker" "this" {
  broker_name                = var.name
  engine_type                = "RabbitMQ"
  engine_version             = var.engine_version
  host_instance_type         = var.instance_type
  deployment_mode            = "SINGLE_INSTANCE"
  publicly_accessible        = false
  subnet_ids                 = [var.subnet_ids[0]]
  security_groups            = [aws_security_group.this.id]
  auto_minor_version_upgrade = true
  apply_immediately          = true

  user {
    username = var.username
    password = var.password
  }

  tags = var.tags
}
