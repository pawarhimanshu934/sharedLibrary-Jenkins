def call(Map config = [:]){

  def image_name = config.image_name ?: error("No Image Found")
  def image_tag = config.image_tag ?: 'latest'
  def docker_file = config.dockerfile ?: 'Dockerfile'
  def context = config.context ?: '.'

  echo "Building Docker Image - ${image_name}:${image_tag} using ${docker_file}"

  sh """
  docker build -t ${image_name}:${image_tag} -t ${image_name}:latest -f ${docker_file} ${context}
  """
}
