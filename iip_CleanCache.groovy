//node('slave-agent_ea62956482df') {

node {
  // Access to the Hudson Singleton
  import hudson.*
  echo scm.getUserRemoteConfigs()

}