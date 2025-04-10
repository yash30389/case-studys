def call(config) {
    echo "Cleaning Cached Image"
    sh "docker image prune -f"

    echo "Cleaning Build Cache"
    sh "docker builder prune -f"

    // Set Jenkins build name to include the commit ID
    currentBuild.displayName = """#${env.BUILD_NUMBER}-"""+ config.TAG +""""""
}