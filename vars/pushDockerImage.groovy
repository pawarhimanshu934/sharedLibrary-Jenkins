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

        sh """
        echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin

        docker push ${image_name}:${image_tag}
        docker push ${image_name}:latest

        docker logout
        """
    }
}
