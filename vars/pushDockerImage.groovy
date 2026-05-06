def call(Map config = [:]){

  image_name = config.image_name ?: error("Image is required")
  image_tag = config.image_tag ?: 'latest'
  credentials = config.credentials ?: 'docker-hub-credentials'

  echo "Pushing ${image_name}:${image_tag} to Docker Hub"

  withCredentials([usernamePassword(credentialsId: credentials, 
                                          passwordVariable: 'DOCKER_PASSWORD', 
                                          usernameVariable: 'DOCKER_USERNAME')]) {
            
            // Login to the registry
            sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
            
            // Push the image
            sh "docker push \$DOCKER_USERNAME${image_name}:${image_tag}"
            sh "docker push \$DOCKER_USERNAME${image_name}:latest"
            
            // Logout for security
            sh "docker logout"  
  
}

  
