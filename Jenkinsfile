pipeline {
  agent any

  environment {
    AEM_HOST = 'http://TU_AEM_PUBLISH:4503'
    CRED_ID  = 'aem-publish-creds'
  }

  stages {
    stage('Clonar repo') {
      steps {
        git branch: 'main',
            url: 'https://github.com/TU_USUARIO/superchargesite.git'
      }
    }

    stage('Build & Deploy a AEM') {
      steps {
        withCredentials([usernamePassword(credentialsId: env.CRED_ID,
                                         usernameVariable: 'AEM_USER',
                                         passwordVariable: 'AEM_PASS')]) {
          sh """
            mvn clean install -PautoInstallPackage \
              -Daem.host=${AEM_HOST} \
              -Daem.user=${AEM_USER} \
              -Daem.password=${AEM_PASS}
          """
        }
      }
    }

    stage('Invalidar caché Dispatcher') {
      steps {
        withCredentials([usernamePassword(credentialsId: env.CRED_ID,
                                         usernameVariable: 'DISP_USER',
                                         passwordVariable: 'DISP_PASS')]) {
          sh """
            curl -X POST "http://DISPATCHER_SERVER:80/dispatcher/invalidate.cache" \
              -u ${DISP_USER}:${DISP_PASS} \
              -H "Content-Type: application/json" \
              -d '{"path":["/content/tu-sitio"]}'
          """
        }
      }
    }
  }

  post {
    success {
      echo '¡Build y deploy exitosos! 😈'
    }
    failure {
      mail to: 'contacto@javiersadev.com',
           subject: "Pipeline FALLÓ: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
           body: "Revisa los logs y reintenta, mi ciela."
    }
  }
}