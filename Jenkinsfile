pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '1'))
    disableConcurrentBuilds()
  }
  stages {
    stage('See Travis') {
      steps {
        echo 'see travis'
      }
    }
  }
}
