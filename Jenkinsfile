//mailing functions
def success() { ... } // (unchanged)
def failure() { ... } // (unchanged)

pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS_ID = credentials('docker-hub-credentials')
        IMAGE_NAME = 'mohamedoussemaboussida/kaddem'
        IMAGE_TAG = '1.0.0'
        ARTIFACT_URL = 'http://192.168.50.4:8081/repository/maven-releases/tn/esprit/spring/kaddem/0.0.1/kaddem-0.0.1.jar'
    }

    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling ...'
                git branch: 'oussema',
                    url: 'https://github.com/medoussemaboussida/4twin7_TheStarks_Kaddem.git'
            }
        }

        stage('MVN CLEAN') {
            steps { sh 'mvn clean' }
        }

        stage('MVN COMPILE') {
            steps { sh 'mvn compile' }
        }

        stage('Tests - JUnit/Mockito') {
            steps { sh 'mvn test' }
        }

        stage('Build package') {
            steps { sh 'mvn package' }
        }

        stage('Maven Install') {
            steps { sh 'mvn install' }
        }

        stage('JaCoCo coverage report') {
            steps {
                step([$class: 'JacocoPublisher',
                      execPattern: '**/target/jacoco.exec',
                      classPattern: '**/classes',
                      sourcePattern: '**/src',
                      exclusionPattern: '*/target/**/,**/*Test*,**/*_javassist/**'
                ])
            }
        }

        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv('scanner') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def artifactExists = sh(script: "curl --head --silent --fail ${ARTIFACT_URL}", returnStatus: true) == 0
                    if (artifactExists) {
                        echo "Artifact already exists in Nexus, skipping deploy."
                    } else {
                        sh 'mvn deploy -Dmaven.test.skip=true'
                    }
                }
            }
        }

        stage('Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Docker Login') {
            steps {
                sh 'echo $DOCKER_CREDENTIALS_ID_PSW | docker login -u $DOCKER_CREDENTIALS_ID_USR --password-stdin'
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    def remoteDigest = sh(
                        script: "curl -s -H 'Accept: application/vnd.docker.distribution.manifest.v2+json' " +
                                "-u $DOCKER_CREDENTIALS_ID_USR:$DOCKER_CREDENTIALS_ID_PSW " +
                                "https://index.docker.io/v2/${IMAGE_NAME}/manifests/${IMAGE_TAG} | jq -r .config.digest",
                        returnStdout: true
                    ).trim()

                    def localDigest = sh(
                        script: "docker inspect --format='{{index .RepoDigests 0}}' ${IMAGE_NAME}:${IMAGE_TAG} | cut -d'@' -f2",
                        returnStdout: true
                    ).trim()

                    if (remoteDigest == localDigest) {
                        echo "Docker image already exists in Docker Hub and hasn't changed. Skipping push."
                    } else {
                        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage("Docker Compose") {
            steps {
                sh 'docker compose up -d'
            }
        }
    }

    post {
        success {
            script { success() }
        }
        failure {
            script { failure() }
        }
    }
}
