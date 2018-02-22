pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh ./mvnw clean compile
            }
        }
        stage('Test') {
            steps {
                sh ./mvnw test
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Package') {
            steps {
                sh ./mvnw package
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Integration Tests') {
            steps {
                sh ./mvnw verify
            }
        }
        stage('Deploy') {
            steps {
                sh ./mvnw deploy
            }
        }
    }
}
