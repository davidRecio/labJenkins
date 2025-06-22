pipeline {
    agent {
        label 'docker'
    }
    environment {
        JOB_NAME_ENV = env.JOB_NAME
        BUILD_NUMBER_ENV = env.BUILD_NUMBER
    }
    stages {
        stage('Source') {
            steps {
                git 'https://github.com/srayuso/unir-cicd.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/api_*.xml', allowEmptyArchive: true
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/e2e_*.xml', allowEmptyArchive: true
            }
        }
    }
    post {
        always {
            echo 'Publishing test reports...'
            junit 'results/*_result.xml'
            cleanWs()
        }
        failure {
            echo "Sending failure notification for ${env.JOB_NAME} build #${env.BUILD_NUMBER}"
            // mail to: 'devops@fintechsolutions.com',
            //      subject: "FAILED: Job ${env.JOB_NAME} [#${env.BUILD_NUMBER}]",
            //      body: "Build failed. Check Jenkins for details."
        }
    }
}
