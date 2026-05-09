def call(Map config = [:]) {

    def image_name = config.image_name ?: error("Image is required")
    def image_tag = config.image_tag ?: 'latest'

    echo "Pushing ${image_name}:${image_tag} to Docker Hub"
    
    sh """
        docker push ${image_name}:${image_tag}
        docker push ${image_name}:latest

        docker logout
    """
}
