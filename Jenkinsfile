//mailing functions
def success() {
    def imageUrl = 'https://semaphoreci.com/wp-content/uploads/2020/02/cic-cd-explained.jpg'
    def imageWidth = '800px'
    def imageHeight = 'auto'

    echo "Sending success email..."
    emailext(
        body: """
        <html>
        <body>
            <p>The Jenkins job was successful.</p>
            <p>You can view the build at: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
            <p><img src="${imageUrl}" alt="Your Image" width="${imageWidth}" height="${imageHeight}"></p>
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
}

def failure() {
    def imageUrl = 'https://miro.medium.com/v2/resize:fit:4800/format:webp/1*ytlj68SIRGvi9mecSDb52g.png'
    def imageWidth = '800px'
    def imageHeight = 'auto'

    echo "Sending failure email..."
    emailext(
        body: """
        <html>
        <body>
            <p>Oops! The Jenkins job Failed.</p>
            <p>You can view the build at: <a href="${BUILD_URL}">${BUILD_URL}</a></p>
            <p><img src="${imageUrl}" alt="Your Image" width="${imageWidth}" height="${imageHeight}"></p>
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
}
pipeline {
    agent any

    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling ...'
                git branch: 'ghassen',
                    url: 'https:    //github.com/medoussemaboussida/TheStarks_DevOps.git'
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
          stage('Build Package') {
            steps {
                sh 'mvn package'
            }
        }
        stage('Maven Install') {
            steps {
                sh 'mvn install'
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
                sh 'mvn deploy'
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
