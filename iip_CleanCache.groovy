node {
  // Remove obsolete files based on atime timestamp.
  if (params.REMOVE_OBSOLETES){
    stage('Remove obsolete files.') {
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
