pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '**']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'CleanBeforeCheckout']],
                    submoduleCfg: [],
                    userRemoteConfigs: [[credentialsId: 'github-kemitix', url: 'git@github.com:kemitix/mon.git']]
                ])
                tool name: 'maven 3.5.2', type: 'maven'
                tool name: 'JDK 1.8', type: 'jdk'
                sh './mvnw clean install'
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}
