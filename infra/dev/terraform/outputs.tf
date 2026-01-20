output "bucket_name" {
  value = aws_s3_bucket.blog_images.id
}

output "bucket_arn" {
  value = aws_s3_bucket.blog_images.arn
}