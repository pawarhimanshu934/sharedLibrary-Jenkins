def call(Map config = [:]) {

    def image_name = config.image_name ?: error("Image is required")
    def image_tag = config.image_tag ?: 'latest'
    def credentials = config.credentials ?: 'docker-hub-credentials'

    echo "Pushing ${image_name}:${image_tag} to Docker Hub"

    withCredentials([usernamePassword(
        credentialsId: credentials,
        passwordVariable: 'DOCKER_PASSWORD',
        usernameVariable: 'DOCKER_USERNAME'
    )]) {

        // Login
        sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"

        // Push
        sh "docker push ${image_name}:${image_tag}"
        sh "docker push ${image_name}:latest"

        // Logout
        sh "docker logout"
    }
}
