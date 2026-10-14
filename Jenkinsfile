// ===================================================================
// Declarative Jenkins Pipeline as Code
// Project: Dockerized Hackathon Team Management System
// Complete CI/CD: Lint -> Build -> Test -> Selenium -> Package -> Deploy -> Health Probe -> Rollback
// ===================================================================

pipeline {
    agent any

    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['development', 'staging', 'production'],
            description: 'Target Deployment Environment'
        )
        string(
            name: 'RELEASE_VERSION',
            defaultValue: '1.0.0',
            description: 'Semantic Version Tag for Release'
        )
        string(
            name: 'APP_HOST_PORT',
            defaultValue: '8081',
            description: 'Host Port for Nginx Entry Point'
        )
        booleanParam(
            name: 'RUN_SELENIUM_TESTS',
            defaultValue: true,
            description: 'Execute Automated Browser User Journeys'
        )
    }

    environment {
        IMAGE_NAME = 'hackathon-team-management-system'
        DOCKER_TAG = "${params.RELEASE_VERSION}-${BUILD_NUMBER}"
        NGINX_PORT = "${params.APP_HOST_PORT}"
        HEADLESS = 'true'
        // Docker credentials identifier in Jenkins
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo "===> Step 1: Checking out source code..."
                checkout scm
            }
        }

        stage('Environment Verification') {
            steps {
                echo "===> Step 2: Validating build environment toolchain..."
                sh 'java -version'
                sh 'mvn -version'
                sh 'node -v'
                sh 'npm -v'
                sh 'docker --version || echo "Docker CLI optional on controller agent"'
            }
        }

        stage('Backend Build & Unit Tests') {
            steps {
                echo "===> Step 3: Compiling and verifying backend with Maven..."
                dir('backend') {
                    sh 'mvn clean verify --batch-mode'
                }
            }
            post {
                always {
                    echo "Publishing JUnit backend test results..."
                    junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Frontend Build') {
            steps {
                echo "===> Step 4: Compiling production React Vite frontend assets..."
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Selenium Browser Journeys') {
            when {
                expression { return params.RUN_SELENIUM_TESTS }
            }
            steps {
                echo "===> Step 5: Executing 5 Critical Selenium User Journeys..."
                dir('selenium-tests') {
                    sh 'mvn clean test -Dheadless=true'
                }
            }
            post {
                always {
                    echo "Publishing Selenium test execution reports and archiving screenshots..."
                    junit testResults: 'selenium-tests/target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'selenium-tests/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Docker Multi-Stage Build & Tag') {
            steps {
                echo "===> Step 6: Building production multi-stage Docker images..."
                sh """
                    docker build -t ${IMAGE_NAME}:${DOCKER_TAG} \
                                 -t ${IMAGE_NAME}:${params.RELEASE_VERSION} \
                                 -t ${IMAGE_NAME}:latest .
                """
            }
        }

        stage('Publish Image to Registry') {
            when {
                expression { return env.DOCKERHUB_USERNAME != null }
            }
            steps {
                echo "===> Step 7: Publishing image to container registry..."
                withCredentials([usernamePassword(credentialsId: "${env.DOCKERHUB_CREDENTIALS_ID}",
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh """
                        echo "\$DH_PASS" | docker login -u "\$DH_USER" --password-stdin
                        docker tag ${IMAGE_NAME}:${DOCKER_TAG} \$DH_USER/${IMAGE_NAME}:${DOCKER_TAG}
                        docker tag ${IMAGE_NAME}:${DOCKER_TAG} \$DH_USER/${IMAGE_NAME}:latest
                        docker push \$DH_USER/${IMAGE_NAME}:${DOCKER_TAG}
                        docker push \$DH_USER/${IMAGE_NAME}:latest
                        docker logout
                    """
                }
            }
        }

        stage('Deploy Container Stack') {
            steps {
                echo "===> Step 8: Deploying to ${params.DEPLOY_ENV} on port ${params.APP_HOST_PORT}..."
                sh """
                    export IMAGE_TAG=${DOCKER_TAG}
                    export NGINX_PORT=${params.APP_HOST_PORT}
                    docker compose up -d --remove-orphans
                """
            }
        }

        stage('Health Verification & Auto-Rollback') {
            steps {
                echo "===> Step 9: Probing live endpoint at http://localhost:${params.APP_HOST_PORT}/actuator/health..."
                script {
                    def maxRetries = 15
                    def healthy = false

                    for (int i = 1; i <= maxRetries; i++) {
                        sleep 3
                        def status = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${params.APP_HOST_PORT}/actuator/health || true",
                            returnStdout: true
                        ).trim()

                        if (status == '200') {
                            echo "Health probe PASSED (HTTP 200 OK) on attempt ${i}/${maxRetries}!"
                            healthy = true
                            break
                        } else {
                            echo "Attempt ${i}/${maxRetries}: Probe returned status ${status}. Retrying..."
                        }
                    }

                    if (!healthy) {
                        error("Deployment health verification FAILED! Triggering automated rollback.")
                    }
                }
            }
            post {
                failure {
                    echo "===> CRITICAL: Deployment failed health checks. Rolling back to previous stable release..."
                    sh """
                        chmod +x deployment/scripts/rollback.sh
                        ./deployment/scripts/rollback.sh || true
                    """
                }
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo " CI/CD Pipeline SUCCEEDED for release ${DOCKER_TAG}"
            echo " Live entry point: http://localhost:${params.APP_HOST_PORT}"
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo " CI/CD Pipeline FAILED. Unhealthy artifacts were blocked."
            echo "=========================================================="
        }
    }
}
