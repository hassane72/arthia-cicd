pipeline {
    agent any

    tools {
        maven 'maven363'
    }

    environment {
        VERSION = "v1"
        MAINTAINER = "hassane72"
        DOCKER_IMAGE_NAME = "arthia-service"
        BUILD_TAG = "prod-${VERSION}"
    }

    stages {
        stage('GEt maven version And clean package') {
            parallel {
                stage('Check java') {
                    steps {
                        sh "java -version"
                    }
                }
                stage('Check Maven') {
                    steps {
                        sh 'mvn --version'
                    }
                }
                stage('Clean') {
                    steps {
                        sh 'mvn clean'
                    }
                }
            }
        }
        stage('Packaging') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build docker image') {
            steps {
                sh "docker build -t ${MAINTAINER}/${DOCKER_IMAGE_NAME}:${BUILD_TAG} ."
            }
        }
        stage('Build docker latest image') {
            steps {
                sh "docker build -t ${MAINTAINER}/${DOCKER_IMAGE_NAME}:prod-latest ."
            }
        }
        stage('Push latest image to the Hub Docker') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-login', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                    sh "docker push ${MAINTAINER}/${DOCKER_IMAGE_NAME}:prod-latest"
                }
            }
        }
        stage("Push current image to the Hub Docker") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-login', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                    sh "docker push ${MAINTAINER}/${DOCKER_IMAGE_NAME}:${BUILD_TAG}"
                }
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}