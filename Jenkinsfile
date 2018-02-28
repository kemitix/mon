final String gitRepoUrl = 'git@github.com:kemitix/mon.git'
final String mvn = "mvn --batch-mode --update-snapshots"

pipeline {
    agent any
    stages {
        stage('Prepare') {
            steps {
                git url: gitRepoUrl, branch: '**', credentialsId: 'github-kemitix'
            }
        }
        stage('Build') {
            parallel {
                stage('Java 8') {
                    withMaven(maven: 'maven 3.5.2', jdk: 'JDK 1.8') {
                        sh 'mvn clean install'
                    }
                }
                stage('Java 9') {
                    withMaven(maven: 'maven 3.5.2', jdk: 'JDK 9') {
                        sh 'mvn clean install'
                    }
                }
            }
        }
        stage('Reporting') {
            steps {
                junit '**/target/surefire-reports/*.xml'
                archiveArtifacts '**/target/*.jar'
            }
        }
        stage('Deploy') {
            when { expression { (env.GIT_BRANCH == 'master') } }
            steps {
                withMaven(maven: 'maven 3.5.2', jdk: 'JDK 1.8') {
                    sh "${mvn} deploy --activate-profiles release"
                }
            }
        }
    }
}
