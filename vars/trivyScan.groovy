def call(Map config = [:]){

  def image_name = config.image_name ?: error("Image name is required")
  def image_tag = config.image_tag ?: 'latest'
  def severity = config.severity ?: 'HIGH,CRITICAL'

  def safeName = image_name.replaceAll('/','-')
  def reportDir = "trivy-reports"

  echo "Scanning Image : ${image_name}:${image_tag}"

  // Create Report Directory
  sh "mkdir -p ${reportDir}"

  // Run Trivy Scan ( JSON )
  sh """
        docker run --rm \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v \$(pwd)/${reportDir}:/${reportDir} \
        -v $HOME/.cache/trivy:/root/.cache/trivy \
        aquasec/trivy:latest image \
        --format json \
        --output /${reportDir}/${safeName}-${image_tag}.json \
        --severity ${severity} \
        ${image_name}:${image_tag} || true
    """

  // Step 3: Generate HTML report
    sh """
        docker run --rm \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v \$(pwd)/${reportDir}:/${reportDir} \
        -v $HOME/.cache/trivy:/root/.cache/trivy \
        aquasec/trivy:latest image \
        --format template \
        --template "@/contrib/html.tpl" \
        --output /${reportDir}/${safeName}-${image_tag}.html \
        --severity ${severity} \
        ${image_name}:${image_tag} || true
    """

     // Step 4: Count vulnerabilities
    def vulnCount = sh(
        script: """
            if [ -f ${reportDir}/${safeName}-${image_tag}.json ]; then
                grep -c "VulnerabilityID" ${reportDir}/${safeName}-${image_tag}.json || echo 0
            else
                echo 0
            fi
        """,
        returnStdout: true
    ).trim()

    echo "Total vulnerabilities in ${image_name}:${image_tag} = ${vulnCount}"

    return vulnCount
  
}
