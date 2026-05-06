def call(Map config = [:]){

  def image_name = config.image_name ?: error("Image name is required")
  def image_tag = config.image_tag ?: 'latest'
  def severity = config.severity ?: 'HIGH,CRITICAL'

  def safe_name = image_name.replaceAll('/','-')
  def reportDir = "trivy-reports"

  echo "Scanning Image : ${image_name}:${image_tag}"

  // Create Report Directory
  sh "mkdir -p ${reportDir}"

  // Run Trivy Scan ( JSON )
  sh """
        docker run --rm \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v \$(pwd)/${reportDir}:/${reportDir} \
        aquasec/trivy:latest image \
        --format json \
        --output /${reportDir}/${safeName}-${imageTag}.json \
        --severity ${severity} \
        ${imageName}:${imageTag} || true
    """

  // Step 3: Generate HTML report
    sh """
        docker run --rm \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v \$(pwd)/${reportDir}:/${reportDir} \
        aquasec/trivy:latest image \
        --format template \
        --template "@/contrib/html.tpl" \
        --output /${reportDir}/${safeName}-${imageTag}.html \
        --severity ${severity} \
        ${imageName}:${imageTag} || true
    """

     // Step 4: Count vulnerabilities
    def vulnCount = sh(
        script: """
            if [ -f ${reportDir}/${safeName}-${imageTag}.json ]; then
                grep -c "VulnerabilityID" ${reportDir}/${safeName}-${imageTag}.json || echo 0
            else
                echo 0
            fi
        """,
        returnStdout: true
    ).trim()

    echo "Total vulnerabilities in ${imageName}:${imageTag} = ${vulnCount}"

    return vulnCount
  
}
