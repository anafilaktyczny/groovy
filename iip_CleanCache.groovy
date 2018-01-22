node('slave-agent_ea62956482df') {

  state('Bootstrap'){
    // Access to the Hudson Singleton
    hudsonInstance = hudson.model.Hudson.instance

    // Retrieve matching jobs
    allItems = hudsonInstance.items
    chosenJobs = allItems.findAll{job -> job.name =~ /$jobFilter/}

// Do work and create the result table
chosenJobs.each { job ->
  if(!(job instanceof hudson.model.ExternalJob)) {

    // No SCM-Configuration possible for External Jobs!
    //if (job.scm instanceof SubversionSCM) { 
      println ""
      println job.name
      
      // Job has a SubversionSCM-Configuration
      def newSvnPath = [][]

      job.scm.locations.each{
        //For every Subversion-Location
        println "-   $it.remote"
        println "-   $it"
        /*
        if (it.remote =~ /$urlFilter/) {
          //SVN-Path contains the given Path
          newRemote = it
          match =  it.remote =~ /$oldBranch/
          
          if(match) {
            newRemote = match.replaceFirst(newBranch)
            println " -> $newRemote"
            newSvnPath.add(it.withRemote(newRemote))
          } else {
            println "    Doesn't match oldBranch"
            newSvnPath.add(it)
          }

        } else {
          println "    Doesn't match urlFilter"
        }
        */
      }
    }  
  //}
  
  // Remove obsolete files based on atime timestamp.

  
  if (params.REMOVE_OBSOLETES){
    stage('Remove obsolete files.') {
      def cache_dir="/tmp/"
      def days=19
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
