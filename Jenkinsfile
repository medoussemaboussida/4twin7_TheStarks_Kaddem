pipeline {
    agent any
  
  
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
       stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy'
            }


}

    }
}
