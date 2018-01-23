node{
  // ${GERRIT_SCHEME}://${GERRIT_HOST}:${GERRIT_PORT}/oip/core/pipeline
  stage("INfo"){

    // Access to the Hudson Singleton
    hudsonInstance = hudson.model.Hudson.instance

    // Retrieve matching jobs
    allItems = hudsonInstance.items
    chosenJobs = allItems.findAll{job -> job.name =~ /GroovyShellTest/}

    println chosenJobs
/*
    Jenkins.instance.getAllItems(Job).each{ job ->

      println(job.scm)
    }
      def jobBuilds=it.getBuilds()
      //for each of such jobs we can get all the builds (or you can limit the number at your convenience)
        jobBuilds.each { build ->

          def runningSince = groovy.time.TimeCategory.minus( new Date(), build.getTime() )
          def currentStatus = build.buildStatusSummary.message
          def cause = build.getCauses()[0] //we keep the first cause
          //This is a simple case where we want to get information on the cause if the build was 
          //triggered by an user
          def user = cause instanceof Cause.UserIdCause? cause.getUserId():""
          //This is an easy way to show the information on screen but can be changed at convenience
          println "Build: ${build} | Since: ${runningSince} | Status: ${currentStatus} | Cause: ${cause} | User: ${user}" 
         
          // You can get all the information available for build parameters.
          def parameters = build.getAction(ParametersAction)?.parameters
          parameters.each {
            println "Type: ${it.class} Name: ${it.name}, Value: ${it.dump()}"
          }
        }
    }
    */
  }


  stage("bootstrap") {
    int sleep_duration = 5

    retry(6) {
      try {
        checkout([
          $class: 'GitSCM',
          doGenerateSubmoduleConfigurations: false,
          branches: [[name: '*/master-iip']],
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