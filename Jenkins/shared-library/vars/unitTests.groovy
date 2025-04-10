def call(config) {
    if (config.SERVICE_NAME == "frontend-prod" || config.SERVICE_NAME == "frontend-dev") {
        echo "Running frontend tests for ${config.SERVICE_NAME}..."
        sh """
            pnpm install
            mkdir -p reports
            pnpm add jest-junit --save-dev
            JEST_JUNIT_OUTPUT="reports/${config.TAG}_unittest_results.xml" \
            JEST_JUNIT_OUTPUT_DIR="reports" \
            JEST_JUNIT_OUTPUT_NAME="${config.TAG}_unittest_results.xml" \
            pnpm test --ci --reporters=default --reporters=jest-junit
            rm -rf node_modules
        """

        // Publish the test report in Jenkins
        junit "reports/${config.TAG}_unittest_results.xml"
    } else {
        echo "Running backend tests for ${config.SERVICE_NAME}..."
        sh """
            echo "Running Pytest Unit Tests..."
            python3 -m venv venv
            . venv/bin/activate
            pip install -r requirements.txt
            mkdir -p reports
            pytest --junitxml=reports/${config.TAG}_unittest_results.xml
            rm -rf venv .pytest_cache
        """

        // Publish the JUnit XML report in Jenkins
        junit "reports/${config.TAG}_unittest_results.xml"
    }
}
