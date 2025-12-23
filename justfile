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
      -Dsonar.token=sqp_775d4ea6ed662b19a73f9470372c070ea0c03d21 \
      -Dsonar.scm.disabled=true

jacoco:
    ./gradlew test jacocoTestReport
