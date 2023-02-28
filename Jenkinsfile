pipeline {
    agent any
    tools{
      gradle 'GRADLE_USER_HOME'
    }
    stages {
        stage('Build') {
            
            steps {  
                            
               bat 'gradle build --warning-mode all'
                echo "successfully build"
                
            }
              post{
                 success{
                     echo "Archiving the Artifacts"
                     archiveArtifacts artifacts: '**/debug/*.apk'                             
                 }
            }             
        }      
            stage('Test'){
                post{
                    success{
                        emailext body: '', recipientProviders: [developers()], subject: 'build', to: 'manjula.r@ciglobalsolutions.com'
                    }
                }
                steps {
                    echo "successfully"
                }
            }
    }
}
