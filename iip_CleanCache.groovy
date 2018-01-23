//node('slave-agent_ea62956482df') {

import hudson.scm.*
node {
  // Access to the Hudson Singleton
  echo getUserRemoteConfigs();

}