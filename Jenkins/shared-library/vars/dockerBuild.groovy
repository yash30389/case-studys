def call(config) {
    echo "Building Docker images..."
        sh """
            docker build -t """+ config.IMAGE +""":"""+ config.TAG +""" -f Dockerfile .
        """
        if (config.SERVICE_NAME == "backend-prod" || config.SERVICE_NAME == "backend-dev"){
            echo "Building Worker Image"
            sh """
                docker build  -t """+ config.SECONDARY_IMAGE +""":"""+ config.TAG +""" -f Dockerfile.worker .
            """
        }
}