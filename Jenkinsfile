def success() {
    try {
        echo "Sending success email..."
        emailext(
            body: """
            <html>
            <body>
                <p>The Jenkins job was successful.</p>
                <p>You can view the build at: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                <p><img src="https://semaphoreci.com/wp-content/uploads/2020/02/cic-cd-explained.jpg" alt="Your Image" width="800px" height="auto"></p>
            </body>
            </html>
            """,
            subject: "Jenkins Build - Success",
            to: 'ghassenbenmahmoud6@gmail.com',
            from: 'ghassenbenmahmoud6@gmail.com',
            replyTo: 'ghassenbenmahmoud6@gmail.com',
            mimeType: 'text/html'
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
            body: """
            <html>
            <body>
                <p>Oops! The Jenkins job Failed.</p>
                <p>You can view the build at: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
                <p><img src="https://miro.medium.com/v2/resize:fit:4800/format:webp/1*ytlj68SIRGvi9mecSDb52g.png" alt="Your Image" width="800px" height="auto"></p>
            </body>
            </html>
            """,
            subject: "Jenkins Build - Failure",
            to: 'ghassenbenmahmoud6@gmail.com',
            from: 'ghassenbenmahmoud6@gmail.com',
            replyTo: 'ghassenbenmahmoud6@gmail.com',
            mimeType: 'text/html'
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
                    emailext( 
                        body: 'This is a test email from Jenkins.',
                        subject: 'Test Email',
                        to: 'ghassenbenmahmoud6@gmail.com',
                        from: 'ghassenbenmahmoud6@gmail.com',
                        mimeType: 'text/plain'
                    )
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
                sh 'docker push  ghassenbenmahmoud/kaddem:1.0.0'
            }
        }
        stage("Docker Compose") {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
    //mailing functions
        post {
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
    }
}
