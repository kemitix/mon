final String gitRepoUrl = 'git@github.com:kemitix/mon.git'
final String mvn = "mvn --batch-mode --update-snapshots"
def pom = readMavenPom file: 'pom.xml'

pipeline {
    agent any
    stages {
        stage('Prepare') {
            steps {
                git url: gitRepoUrl, branch: '**', credentialsId: 'github-kemitix'
            }
        }
        stage('no SNAPSHOT in master') {
            // checks that the pom version is not a snapshot when the current branch is master
            when { expression { (env.GIT_BRANCH == 'master') } }
            steps {
                if ((pom.version).contains("SNAPSHOT")) {
                    error("Build failed because SNAPSHOT version: ${pom.groupId}:${pom.artifactId}:${pom.version}")
                }
            }
        }
        stage('Build') {
            parallel {
                stage('Java 8') {
                    steps {
                        withMaven(maven: 'maven 3.5.2', jdk: 'JDK 1.8') {
                            sh 'mvn clean install'
                        }
                    }
                }
                // requires maven-failsafe-plugin:2.21 when it is released
//                stage('Java 9') {
//                    steps {
//                        withMaven(maven: 'maven 3.5.2', jdk: 'JDK 9') {
//                            sh 'mvn clean install'
//                        }
//                    }
//                }
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
                    sh "${mvn} deploy --activate-profiles release -DskipTests=true"
                }
            }
        }
    }
}
