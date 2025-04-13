def success() {
    try {
        echo "Sending success email..."
        emailext(
            body: "The Jenkins job was successful. Build URL: ${BUILD_URL}",
            subject: "Jenkins Build - Success",
            to: 'ghassenbenmahmoud6@gmail.com',
            mimeType: 'text/plain'
        )
        echo "Success email sent."
    } catch (Exception e) {
        echo "Failed to send success email: ${e.getMessage()}"
        throw e
    }
}

def failure() {
    try {
        echo "Sending failure email..."
        emailext(
            body: "The Jenkins job failed. Build URL: ${BUILD_URL}",
            subject: "Jenkins Build - Failure",
            to: 'ghassenbenmahmoud6@gmail.com',
            mimeType: 'text/plain'
        )
        echo "Failure email sent."
    } catch (Exception e) {
        echo "Failed to send failure email: ${e.getMessage()}"
        throw e
    }
}

pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS_ID = credentials('docker-hub-credentials')
    }
    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling ...'
                git branch: 'ghassen',
                    url: 'https://github.com/medoussemaboussida/4twin7_TheStarks_Kaddem.git'
            }
        }
        stage('MVN CLEAN') {
            steps {
                sh 'mvn clean'
            }
        }
        stage('MVN COMPILE') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Tests - JUnit/Mockito') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Build package') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Maven Install') {
            steps {
                sh 'mvn install'
            }
        }
        stage('Test Email') {
            steps {
                script {
                    echo "Sending test email..."
                    try {
                        emailext(
                            body: 'This is a test email from Jenkins.',
                            subject: 'Test Email',
                            to: 'ghassenbenmahmoud6@gmail.com',
                            mimeType: 'text/plain'
                        )
                        echo "Test email sent successfully."
                    } catch (Exception e) {
                        echo "Failed to send test email: ${e.getMessage()}"
                        throw e
                    }
                }
            }
        }
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
        stage('JaCoCo coverage report') {
            steps {
                echo "Running JaCoCo coverage report..."
                jacoco(
                    execPattern: '**/target/jacoco.exec',
                    classPattern: '**/classes',
                    sourcePattern: '**/src',
                    exclusionPattern: '*/target/**/,**/*Test*,**/*_javassist/**'
                )
                echo "JaCoCo report generated successfully."
            }
        }
        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv('scanner') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        stage('Docker Image') {
            steps {
                sh 'docker build -t ghassenbenmahmoud/kaddem:1.0.0 .'
            }
        }
        stage('Docker Login') {
            steps {
                sh 'echo $DOCKER_CREDENTIALS_ID_PSW | docker login -u $DOCKER_CREDENTIALS_ID_USR --password-stdin'
            }
        }
        stage('Push Docker Image') {
            steps {
                sh 'docker push ghassenbenmahmoud/kaddem:1.0.0'
            }
        }
        stage("Docker Compose") {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
    post {
        always {
            script {
                echo "Build status: ${currentBuild.result}"
                echo "Attempting to send status email..."
                try {
                    emailext(
                        body: "Build completed with status: ${currentBuild.result}. URL: ${BUILD_URL}",
                        subject: "Jenkins Build - Status",
                        to: 'ghassenbenmahmoud6@gmail.com',
                        mimeType: 'text/plain'
                    )
                    echo "Status email sent successfully."
                } catch (Exception e) {
                    echo "Failed to send status email: ${e.getMessage()}"
                    throw e
                }
            }
        }
        success {
            script {
                success()
            }
        }
        failure {
            script {
                failure()
            }
        }
        unstable {
            script {
                echo "Build is unstable."
                try {
                    emailext(
                        body: "Build is unstable. URL: ${BUILD_URL}",
                        subject: "Jenkins Build - Unstable",
                        to: 'ghassenbenmahmoud6@gmail.com',
                        mimeType: 'text/plain'
                    )
                    echo "Unstable email sent successfully."
                } catch (Exception e) {
                    echo "Failed to send unstable email: ${e.getMessage()}"
                    throw e
                }
            }
        }
    }
}