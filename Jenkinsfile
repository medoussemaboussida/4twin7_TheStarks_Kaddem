def success() {
    def imageUrl = 'https://miro.medium.com/v2/resize:fit:600/1*qzDG-ROC1aUkVZ-LYVe5pA.jpeg'
    def imageWidth = '800px'
    def imageHeight = 'auto'

    echo "Sending success notification..."
    emailext(
        body: """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: green;"> Build Completed Successfully!</h2>
            <p>Great news! The Jenkins pipeline executed without any issues.</p>
            <p>You can check the build details here: 
                <a href="${BUILD_URL}" style="color: #1a73e8;">View Build</a>
            </p>
            <p><img src="${imageUrl}" alt="CI/CD Flow Diagram" width="${imageWidth}" height="${imageHeight}" style="border:1px solid #ccc;"></p>
            <p style="font-size: 12px; color: gray;">— Jenkins CI System</p>
        </body>
        </html>
        """,
        subject: " Jenkins Pipeline Success - Build Info",
        to: 'asmariahii2000@gmail.com',
        from: 'asmariahii2000@gmail.com',
        replyTo: 'asmariahii2000@gmail.com',
        mimeType: 'text/html'
    )
    echo "Success email dispatched."
}


def failure() {
    def imageUrl = 'https://slack.engineering/wp-content/uploads/sites/7/2021/05/jenkins-fire.png'
    def imageWidth = '800px'
    def imageHeight = 'auto'

    echo "Sending failure alert..."
    emailext(
        body: """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: red;">❌ Build Failed</h2>
            <p>Unfortunately, something went wrong during the Jenkins build process.</p>
            <p>Take a look at the build logs and investigate here: 
                <a href="${BUILD_URL}" style="color: #d93025;">Open Build Logs</a>
            </p>
            <p><img src="${imageUrl}" alt="Failure Warning" width="${imageWidth}" height="${imageHeight}" style="border:1px solid #ccc;"></p>
            <p style="font-size: 12px; color: gray;">— Jenkins CI System</p>
        </body>
        </html>
        """,
        subject: " Jenkins Pipeline Success - Build Info",
        to: 'asmariahii2000@gmail.com',
        from: 'asmariahii2000@gmail.com',
        replyTo: 'asmariahii2000@gmail.com',
        mimeType: 'text/html'
    )
    echo "Failure email dispatched."
}

pipeline {
    agent any
        environment {

            DOCKER_CREDENTIALS_ID = credentials('docker-hub-credentials')
        }

  
  stages {
        stage('Fetch Source Code') {
            steps {
                echo 'Pulling latest changes...'
                git branch: 'asma',
                    url: 'https://github.com/medoussemaboussida/4twin7_TheStarks_Kaddem.git'
            }
        }

 stage('Clean Workspace') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile Project') {
            steps {
                sh 'mvn compile'
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








       
        stage('Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
             stage('Tests - JUnit/Mockito') {
                    steps {
                        sh 'mvn test'
                    }
                }




        stage('JaCoCo') {
            steps {
                jacoco(
                    execPattern: '**/target/jacoco.exec',
                    classPattern: '**/target/classes',
                    sourcePattern: '**/src/main/java',
                    exclusionPattern: '*/target/**/,**/*Test*,**/*_javassist/**'
                )
            }
        }

        stage('SonarQube Analysis') {  
            steps {
                withSonarQubeEnv('scanner') { 
                    sh 'mvn sonar:sonar'
                }
            }
        }


        stage('Docker Image') {
                    steps {
                        sh 'docker build -t asmariahi/kaddem:1.0.0 .'
                    }
                }
                stage('Docker Login') {
                    steps {
                        sh 'echo $DOCKER_CREDENTIALS_ID_PSW | docker login -u $DOCKER_CREDENTIALS_ID_USR --password-stdin'
                    }
                }

                 stage('Docker Push') {
                   steps {
                     sh 'docker push asmariahi/kaddem:1.0.0'
                           }
                                   }


               stage('Docker Compose') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }

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