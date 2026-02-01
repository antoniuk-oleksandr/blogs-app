resource "aws_ecr_repository" "this" {
  name = var.name
  tags = var.tags
}

resource "aws_ecr_lifecycle_policy" "this" {
  count      = length(var.lifecycle_rules) > 0 ? 1 : 0
  repository = aws_ecr_repository.this.name

  policy = jsonencode({
    rules = [
      for r in var.lifecycle_rules : {
        rulePriority = r.rule_priority
        description  = r.description
        selection = {
          tagStatus   = r.selection_tag_status
          countType   = "imageCountMoreThan"
          countNumber = r.max_image_count
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
