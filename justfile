alias dcu := docker-compose-up
alias dcd := docker-compose-down

docker-compose-up:
    docker compose -f infra/dev/docker-compose.yaml up -d

docker-compose-down:
    docker compose -f infra/dev/docker-compose.yaml down

sonar:
    just jacoco
    ./gradlew sonar \
      -Dsonar.projectKey=Blogs-App \
      -Dsonar.projectName='Blogs App' \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=sqp_e3518a17963166bedaef989b31c043276d5b015f \
      -Dsonar.scm.disabled=true

jacoco:
    ./gradlew test jacocoTestReport

localstack-up:
    docker compose -f infra/dev/docker-compose.localstack.yml  up -d

localstack-down:
    docker compose -f infra/dev/docker-compose.localstack.yml  down