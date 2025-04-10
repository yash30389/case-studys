def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    properties([disableConcurrentBuilds()])

    try {
        timeout(config.timeout ?: 180) {
            stage('Run Unit Tests') {
                unitTests(config)
            }

            stage('Docker Build') {
                dockerBuild(config)
            }
            stage('Push image to ECR') {
                dockerPush(config)
            }
            
            stage('Clean Up and Start Containers from new Builds'){
                dockerComposeUp(config)
            }

            stage('CleanUP Storage') {
                doCleanup(config)
            }
        }
    } catch (err) {
        currentBuild.result = 'FAILURE'
        snsNotify(config)
        throw err
    }
}