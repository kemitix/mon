node{
    stage ('Build') {
        git url: 'https://github.com/kemitix/mon'
        withMaven() {
        sh "mvn clean install"
        }
    }
}
