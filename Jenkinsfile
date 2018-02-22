node{
    stage ('Build') {
        withMaven(
            maven: 'Maven'
        ) {
            sh "mvn clean install"
        }
    }
}
