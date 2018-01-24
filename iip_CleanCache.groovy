node {
  def cache_dir = params.SST_CACHE_DIR
  def ret = "None"
  // Remove obsolete files based on atime timestamp.
  if (params.REMOVE_OBSOLETES){
    stage('Remove obsolete files.') {
      def days = params.SST_CACHE_VALID_DAYS.toInteger() 
      if (days <= 0){
        println("Days value below 1, so skipping this job.")
      } else {

        int sleep_duration = 5
        retry(6) {
          try {
            checkout([
              $class: 'GitSCM',
              doGenerateSubmoduleConfigurations: false,
              branches: [[ name: params.SCM_BRANCH ]],
              userRemoteConfigs: [
                [ credentialsId: params.CRED,
                 url: params.SCM_REPO
                ]
              ]
            ])
          } catch (err) {
            sleep_duration = sleep_duration * 4
            sleep (sleep_duration)
            throw (err)
          }
        }
        println("Files in pwd")
        sh "pwd"
        sh "ls -al"
        println("Find all files older then ${days} and delete them")
        dir("${env.WORKSPACE}/../${env.JOB_NAME}@script/scripts/"){
          if (params.DEBUG){
            sh (returnStdout: true, script: "./iip_CleanCache.sh -a delete_it -d ${cache_dir} -n ${days} -v")
          } else {
            sh (returnStdout: false, script: "./iip_CleanCache.sh -a delete_it -d ${cache_dir} -n ${days}")
          }
        }
      }
    }
  }

}
