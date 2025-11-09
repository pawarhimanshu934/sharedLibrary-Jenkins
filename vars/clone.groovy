def clone(String url, String branch){
  git url: "${url}", branch:"${branch}"
  echo "Code clone success git"
}
