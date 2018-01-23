//node('slave-agent_ea62956482df') {

import hudson.scm.*
node {
  // Access to the Hudson Singleton
  hudsonInstance = hudson.model.Hudson.instance
  // Retrieve matching jobs
  allItems = hudsonInstance.items
  //jobFilter = env.JOB_NAME
  //chosenJobs = allItems.findAll{job -> job.name =~ /$jobFilter/}
  // Do work and create the result table
  allItems.each { job ->
    //if(!(job instanceof hudson.model.ExternalJob)) {
      println "HI:-> "
      println job.name
  //  }
  }

}