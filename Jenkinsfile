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
                sh './mvnw clean install'
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}
