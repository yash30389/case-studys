def call(Map params) {
    def topicArn = params.topicArn ?: "arn:aws:sns:<AWS_REGION>:<ACCOUNT_ID>:<Jenkins-Build-Notifications-ARN>"
    def status = params.status ?: "UNKNOWN"
    def service_name = params.service_name ?: "UNKNOWN"
    def final_build_name = params.final_build_name ?: "#${env.BUILD_NUMBER}-unknown"
    def message = ""

    if (status == "STARTED") {
        message = """Jenkins Build Notification:
        Jenkins Job Name: ${env.JOB_NAME}
        Status: ${status}
        Branch: ${env.BRANCH_NAME}
        Build Number: ${env.BUILD_NUMBER}
        Build URL: ${env.BUILD_URL}"""
    } else if (status == "SUCCESS") {
        message = """Jenkins Build Notification:
        Jenkins Job Name: ${env.JOB_NAME}
        Status: ${status}
        Branch: ${env.BRANCH_NAME}
        Service Name: ${env.SERVICE_NAME}
        Final Build Name: ${final_build_name}
        Build Number: ${env.BUILD_NUMBER}
        Build URL: ${env.BUILD_URL}"""
    } else if (status == "FAILURE") {
        message = """Jenkins Build Notification:
        Jenkins Job Name: ${env.JOB_NAME}
        Status: ${status}
        Branch: ${env.BRANCH_NAME}
        Service Name: ${env.SERVICE_NAME}
        Build Number: ${env.BUILD_NUMBER}
        Build URL: ${env.BUILD_URL}"""
    }

    sh """
        aws sns publish --topic-arn "${topicArn}" --message "${message}" --region <AWS_REGION>
    """
}
