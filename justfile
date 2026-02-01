alias dcu := docker-compose-up
alias dcd := docker-compose-down
alias llu := localstack-up
alias lld := localstack-down
alias tf-local-apply := terraform-local-apply
alias tf-local-destroy := terraform-local-destroy

docker-compose-up:
    docker compose -f infra/docker-compose/docker-compose.yaml up -d

docker-compose-down:
    docker compose -f infra/docker-compose/docker-compose.yaml down

localstack-up:
    docker compose -f infra/docker-compose/docker-compose.localstack.yml  up -d

localstack-down:
    docker compose -f infra/docker-compose/docker-compose.localstack.yml  down

sonar:
    just jacoco
    ./gradlew sonar \
      -Dsonar.projectKey=blogs-app \
      -Dsonar.projectName='Blogs App' \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=sqp_a56c32adfa5ae1b05d8ec07b46b05ace1f2f9c7f \
      -Dsonar.scm.disabled=true

jacoco:
    ./gradlew test jacocoTestReport

terraform-local-apply:
    terraform -chdir=infra/terraform/local apply -auto-approve

terraform-local-destroy:
    terraform -chdir=infra/terraform/local destroy -auto-approve