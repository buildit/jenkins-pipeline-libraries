package jenkinsUnit

import groovy.json.JsonSlurper
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def run(location) {
    def items = [location]
    if (isDirectory(location)) {
        items = listing(location)
    }
    for (int i = 0; i < items.size(); i++) {
        if ("${items[i]}".endsWith("Test.groovy")) {
            echo("\n")
            echo("Running Test File: \"${items[i]}\"")
            echo("\n")
            load(items[i])
        }
    }
}

@NonCPS
def listing(dir) {
    String contents = pipe($/find ${
        dir
    } | sort | awk ' BEGIN { ORS = ""; print "["; } { print "\/\@"$0"\/\@"; } END { print "]"; }' | sed "s^\"^\\\\\"^g;s^\/\@\/\@^\", \"^g;s^\/\@^\"^g"/$)
    new JsonSlurper().parseText(contents)
}

@NonCPS
def isDirectory(dir) {
    def contents = pipe("find ${dir} -type d -maxdepth 0")
    contents.length() > 0
}

@NonCPS
def pipe(command) {
    sh(script: command, returnStdout: true)
}

return this
