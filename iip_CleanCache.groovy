/**
 * This Jenkinsfile provides the pipeline implementation that will clean sstate-cache in two ways. 
 * First way use sstate-cache-management.sh from yocto project, to remove all duplicated files.
 * In addition the second way use "find" command that will grab all older files then defined days value. 
 *
 * Author: Grzegorz Hetman
 * Date: 2017-10-11
 *
 * Copyright (C) 2016 Continental Automotive GmbH
 *
 * Alle Rechte vorbehalten. All Rights Reserved.
 * The reproduction, transmission or use of this document or its contents is
 * not permitted without express written authority.
 * Offenders will be liable for damages. All rights, including rights created
 * by patent grant or registration of a utility model or design, are reserved.
 *
 * The copyright applies to all files within this package without copyright
 * declaration.
 */


def cache_dir = params.SST_CACHE_DIR

node {
  
  if ((!params.REMOVE_DUPLICATES) && (!params.REMOVE_OBSOLETES)){
    currentBuild.result = 'ABORTED'
    // Use error instead trowing new exception, by this way it`s possible
    // to run this pipeline script in sandbox.
    error "Please select checkbox REMOVE_DUPLICATES or REMOVE_OBSOLETES or both."
  }

  stage('Checking directory.') {
    if (!fileExists(cache_dir)){
      println('Missing or invalid cache directory: $cache_dir - exit.')
      currentBuild.result = 'ABORTED'
      error "Invalid sstate-cache directory."
    }
  }


  stage("bootstrap") {
    
  }

  // Remove duplicate file by poky internal script.
  if (params.REMOVE_DUPLICATES){
    stage('Geting poky scripts.') {
      int sleep_duration = 5
      retry(4) {
        try {
          git credentialsId: params.CRED,
          url: 'ssh://wetp715x.we.de.conti.de:29418/oip/core/poky'
        } catch (err) {
          sleep_duration = sleep_duration * 4
          sleep (sleep_duration)
          throw (err)
        }
      }

     }
    stage('Remove duplicates.') {
        def ret = sh (returnStdout: true, script: "./scripts/sstate-cache-management.sh -d -y --cache-dir=${cache_dir}")
        println(ret);
    }
  }

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
              branches: [[name: '*/master']],
              userRemoteConfigs: [
                [ credentialsId: Cred,
                 url: "https://anafilaktyczny@github.com/anafilaktyczny/groovy.git"
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
