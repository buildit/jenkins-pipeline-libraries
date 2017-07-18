pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }
  triggers {
    pollSCM('* * * * *')
  }
  tools {
    maven 'apache-maven-3.5.0'
  }
  stages {
    stage('Build') {
      steps {
        sh "mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V"
      }
    }
    stage('Test') {
      steps {
        sh "mvn test -B"
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }
  }
}
