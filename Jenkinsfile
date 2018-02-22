pipeline {
    stage('Build') {
        steps {
            withMaven(maven: 'maven 3.5.2', jdk: 'JDK 1.8') {
                sh "mvn clean install"
            }
        }
    }
}
