def containerId=""
pipeline {
    agent none
    stages {
        stage('Build DockerWorld') {
            agent {
                    docker {
                        image 'maven:3-alpine'
                        args '-v /root/.m2:/root/.m2'
                    }
                  }
            steps {
                    sh 'mvn -X clean install -DskipTests'
                  }
            }

        stage('Staging DockerWorld Image') {
            agent any
            steps{
                    script{
                        containerId = sh (
                        script :'docker ps -aqf "name=dw"',
                        returnStdout: true
                        ).trim()
                        if("${containerId}"!= ""){
                          sh 'docker stop dw'
                          sh 'docker rm dw'
                          sh 'docker rmi $(docker images --filter=reference=dockerworld --format "{{.ID}}")'
                        }
                    }
                    sh 'docker build -t dockerworld:1.0 .'
                }
              }
        stage('Containerising DockerWorld') {
          agent any
           steps {
                   sh 'docker run -d -p 8081:8081 -v /home/ec2-user/logs:/logs -e HOST_NAME=socat --name dw --link=socat dockerworld:1.0'
                 }
         }
    }
 }