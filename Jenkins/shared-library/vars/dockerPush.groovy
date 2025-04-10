def call(config) {
    echo "Logging in to AWS ECR..."
    sh "aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com"

     echo "Tagging and pushing image"
    sh """
        docker push """+ config.IMAGE +""":"""+ config.TAG +"""
    """

    if (config.SERVICE_NAME == "backend-prod" || config.SERVICE_NAME == "backend-dev"){
    echo "Tagging and pushing Worker image"
    sh """
        docker push """+ config.SECONDARY_IMAGE +""":"""+ config.TAG +"""
    """}
}
