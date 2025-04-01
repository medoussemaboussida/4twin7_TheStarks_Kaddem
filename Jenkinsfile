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
}