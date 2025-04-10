def call(config) {
    sh """
        #!/bin/bash
        IMAGE=${config.IMAGE}
        TAG=${config.TAG}
        SERVICE_NAME=${config.SERVICE_NAME}
        SECONDARY_IMAGE=${config.SECONDARY_IMAGE}
        TASK_DEFINITION=${config.TASK_DEFINITION}
        DYNAMIC_FILE_NAME=\$(basename \$IMAGE | tr ':' '_')_\${TAG}.json
        CLUSTER_NAME=${config.CLUSTER_NAME}
        ECS_SERVICE=${config.ECS_SERVICE}

        # Get the latest task definition
        TASK_DEFINITION_JSON=\$(aws ecs describe-task-definition --task-definition \$TASK_DEFINITION --query 'taskDefinition' --output json)

        # Determine which images to update dynamically
        NEW_TASK_DEF_JSON=\$(echo "\$TASK_DEFINITION_JSON" | jq '
            .containerDefinitions |= map(
                if .name == "Frontend" then
                    .image = "'"\$IMAGE:\$TAG"'"
                elif .name == "Backend" then
                    .image = "'"\$IMAGE:\$TAG"'"
                elif .name == "Backend_Worker" then
                    .image = "'"\$SECONDARY_IMAGE:\$TAG"'"
                elif .name == "Backend_Worker2" then
                    .image = "'"\$SECONDARY_IMAGE:\$TAG"'"
                else
                    .
                end
            )'
        )

        # Register a new task definition revision
        echo "\$NEW_TASK_DEF_JSON" | jq 'del(.taskDefinitionArn, .revision, .status, .registeredAt, .registeredBy, .requiresAttributes, .compatibilities)' > \$DYNAMIC_FILE_NAME
        aws ecs register-task-definition --cli-input-json file://\$DYNAMIC_FILE_NAME

        # Get the latest task revision number
        REVISION=\$(aws ecs describe-task-definition --task-definition \$TASK_DEFINITION --query 'taskDefinition.revision' --output text)

        # Update ECS service with the new task revision
        aws ecs update-service --cluster \$CLUSTER_NAME --service \$ECS_SERVICE --task-definition "\$TASK_DEFINITION:\$REVISION"
    """
}
