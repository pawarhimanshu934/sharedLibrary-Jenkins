def call(Map config = [:]){
  def app_image = config.app_image?: error("App Image required")
  def migration_image = config.migration_image?: error("Migration Image required")
  def image_tag = config.image_tag?: "latest"
  def manifest_dir = config.manifest_dir?: "kubernetes"

  withCredentials([gitUsernamePassword(
        credentialsId: 'github-credentials',
        gitToolName: 'Default'
        
    )]) {

        sh """
        
        echo "Updating Image name tags in K8 manifest with latest build number"
        sed -i 's|image: .*easyshop:.*|image: ${app_image}:${image_tag}|g' ${manifest_dir}/04-deployment.yml

        if [ -f ${manifest_dir}/10-data-migration.yml ]; then
          sed -i 's|image: .*easyshop-migration:.*|image: ${migration_image}:${image_tag}|g' ${manifest_dir}/10-data-migration.yml
        fi

        git add ./${manifest_dir}/*.yml || true
        git commit -m "kubernetes manifest updated with Build No : ${image_tag}" || echo "No changes to commit"

        git push origin main

        echo "Updated to:"
        echo "App → ${app_image}:${image_tag}"
        echo "Migration → ${migration_image}:${image_tag}"
        
        """
    }
  
}
