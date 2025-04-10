### **Case Study: Shared Library for Continuous Deployment in a Microservices Architecture**

A **Jenkins Shared Library** is a powerful tool that allows you to centralize common Jenkins pipeline code and reuse it across multiple Jenkinsfiles. This avoids code duplication and makes the management of Jenkins pipelines more efficient and maintainable. Here's a case study and use case to demonstrate how Jenkins Shared Libraries are typically used.


<table align="center">
  <tr>
    <td align="center">
      <img src="https://miro.medium.com/v2/resize:fit:1116/1*5A8OrocBg3OhG4XHYoF3tQ.jpeg" alt="jenkins" width="300"/>
    </td>
  </tr>
</table>

#### **Overview:**
Company XYZ has a microservices-based architecture with multiple teams responsible for different services. Each microservice is managed by a separate Git repository and follows the same CI/CD pipeline steps (e.g., building, testing, and deploying the application). 

The company uses Jenkins to automate the CI/CD pipeline, but each team's Jenkinsfile is becoming large and hard to manage due to the repeated code for building, testing, and deploying microservices. Instead of copying and pasting the same pipeline code into every Jenkinsfile, they decide to implement a **Jenkins Shared Library**.

#### **Goals:**
- Centralize common pipeline logic to reduce duplication.
- Make pipelines easier to maintain.
- Allow teams to focus on their service-specific logic while reusing common functionality.

#### **Implementation:**
1. **Create the Shared Library:**
   The shared library will contain common steps for building, testing, and deploying services. This library is stored in a separate Git repository (`shared-library`) that all teams can access.

    # Jenkins Shared Library Documentation

    This repository contains a Jenkins Shared Library used to simplify and standardize common CI/CD tasks across projects. Each Groovy script in the `vars` directory defines a reusable step that can be invoked from Jenkins pipelines.

## Directory Structure

Path: `Jenkins/shared-library/vars`

| File Name              | Description |
|------------------------|-------------|
| `commonLibrary.groovy` | Contains reusable utility functions shared across multiple pipelines. These may include logging, error handling, environment setup, or common shell invocations. |
| `dockerBuild.groovy`   | Handles Docker image building using a standard Dockerfile. This can be used in CI pipelines to build project containers before deployment. |
| `dockerComposeUp.groovy` | **Deploys services to AWS ECS** using a Docker Compose file. It internally leverages AWS CLI commands or SDK to:<br>- Register a new ECS Task Definition<br>- Update the ECS Service<br>- Deploy the new Task Definition revision to ECS. |
| `dockerPush.groovy`    | Pushes built Docker images to a container registry (e.g., Docker Hub, Amazon ECR) after a successful build. |
| `doCleanup.groovy`     | Cleans up resources after job completion. This can include workspace cleanup, Docker image removal, or temporary file deletion. |
| `snsNotify.groovy`     | Sends notifications using AWS SNS. It can be used to notify stakeholders on job success/failure with custom messages. |
| `unitTests.groovy`     | Executes unit tests using defined frameworks (e.g., JUnit, pytest, etc.) and returns results to Jenkins. May include support for archiving test reports. |

---

## Key Feature: `dockerComposeUp.groovy`

This function enables zero-downtime ECS deployments using the following flow:

1. **Reads `docker-compose` configuration** and translates it to ECS-compatible task definitions.
2. **Registers a new Task Definition** with AWS ECS using the AWS CLI or SDK.
3. **Updates the ECS service** with the new task definition.
4. **Ensures smooth rollout** of the new revision without interrupting running services.
5. Optionally integrates health checks and rollback strategies.

This makes it easy to manage ECS service updates directly from Jenkins pipelines with minimal manual intervention.

---
2. **Configuring the Jenkins Shared Library:**
   The shared library is configured in the Jenkins global configuration, so Jenkins can access it across multiple pipelines.

   - In Jenkins, go to `Manage Jenkins` > `Configure System` > `Global Pipeline Libraries`.
   - Add the shared library configuration:
     - Name: `xyz-shared-library`
     - Source Code Management: Git
     - Repository URL: `https://github.com/xyz/xyz-jenkins-shared-library.git`
     - Credentials: (Provide credentials if necessary)


3. **Using the Shared Library in Jenkinsfiles:**
   Now, each team can reference the shared library in their own Jenkinsfile and use the predefined pipeline steps.

    ## How to Use in Jenkins Pipeline

    Checkout the JenkinsFile on Path: `Jenkins/shared-library/jenkinsfile`


4. **Advantages:**
   - **Reusability:** The shared library provides reusable pipeline logic, so teams don’t need to rewrite the same steps in each Jenkinsfile.
   - **Maintainability:** Any changes to the pipeline steps (e.g., updating build tools or deployment strategies) only need to be made in the shared library. Teams simply pull the latest version of the shared library to benefit from the changes.
   - **Consistency:** The same pipeline steps are used across all services, ensuring consistent CI/CD practices across the organization.
   - **Modularity:** The shared library is modular, allowing teams to extend or modify the functionality by adding their own utility classes or steps if necessary.

#### **Potential Issues:**
- **Versioning:** Teams must ensure they are using the correct version of the shared library. This can be managed by tagging versions in the Git repository or using the `@Library` annotation with a specific version.
- **Debugging:** It can sometimes be harder to debug pipelines if there is an issue in the shared library code. However, this can be mitigated by maintaining good logging and error-handling in the shared library.

---

### **Use Case: Reusable Steps**

The use case demonstrated in this case study can be broken down into the following reusable steps:

1. **Building the application:**
   - Compile the code.
   - Run unit tests.
   - Build Docker images.
   - Store build artifacts.

2. **Testing the application:**
   - Run integration tests.
   - Perform static code analysis.
   - Trigger automated security tests.

3. **Deploying the application:**
   - Deploy the microservice to different environments (e.g., development, production).
   - Rollback deployments if necessary.
   - Monitor the deployment and notify the team of any failures.

By centralizing these tasks in a shared library, each team only needs to call functions like `commonLibrary()`, `dockerBuild()`, and `dockerComposeUpp()` , `dockerPush()`, `doCleanup()`,`snsNotify()` and `unitTests()` in their Jenkinsfile, drastically reducing boilerplate code.