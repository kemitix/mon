pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'printenv'
                withMaven(maven: 'maven 3.5.2', jdk: 'JDK 1.8') {
                    sh "mvn clean install"
                }
            }
        }
    }
}
