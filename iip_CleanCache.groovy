node{
  // ${GERRIT_SCHEME}://${GERRIT_HOST}:${GERRIT_PORT}/oip/core/pipeline

  stage("INfo"){
    println (scm.getUserRemoteConfigs());
    repository = scm.getRepositories();
    println(repository.aa)
    println(scm.GIT_BRANCH)
  }


  stage("bootstrap") {
    int sleep_duration = 5

    retry(6) {
      try {
        checkout([
          $class: 'GitSCM',
          doGenerateSubmoduleConfigurations: false,
          branches: [[name: '*/master']],
          userRemoteConfigs: [
            [ credentialsId: Cred,
             url: "https://anafilaktyczny@github.com/anafilaktyczny/groovy.git"
            ]
          ]
        ])
      }
      catch (err) {
        sleep_duration = sleep_duration * 4
        sleep (sleep_duration)
        throw (err)
      }
    }
  }

  stage("Real work"){
    println("Preparing....")

  }

}