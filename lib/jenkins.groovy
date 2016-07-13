import groovy.json.JsonSlurper

template = load "lib/template.groovy"
filesystem = load "lib/filesystem.groovy"
templateFiles = [polling: readFile("templates/app-polling-pipeline.xml"), manual: readFile("templates/app-manual-pipeline.xml")]

def createPipelineJob(jobName, gitUrl, pipelinePath, shouldPoll, jenkinsUrl, jenkinsCredentials, jenkinsScmPoll){
    def tmpFile = UUID.randomUUID().toString() + ".xml"
    def templateFile = shouldPoll ? templateFiles["polling"] : templateFiles["manual"]
    def jobXML = template.transform(
        templateFile,
        [
            git_url: gitUrl,
            pipeline_script_path: pipelinePath,
            jenkins_credentials: jenkinsCredentials,
            jenkins_scm_poll: jenkinsScmPoll
        ]
    )
    writeFile(tmpFile, jobXML)

    // create the jenkins job the application pipeline
    sh("curl --header 'Content-Type: application/xml' --data-binary @${tmpFile} ${jenkinsUrl}/createItem?name=${jobName}")

    sh("rm ${tmpFile}")
}

def createFreestyleJobs(gitUrl, jenkinsUrl){

    git(url: gitUrl)

    def jenkinsJobsPath = "./jenkinsJobs"
    def listing = filesystem.listing(jenkinsJobsPath)
    for (def i=0; i < listing.size(); i++) {
        def filepath = listing[i]
        if (filepath != jenkinsJobsPath) {
            def jobName = new File(filepath).getName().split("\\.")[0]
            sh("curl --header 'Content-Type: application/xml' --data-binary @${filepath} ${jenkinsUrl}/createItem?name=${jobName}")
        }
    }
}

return this
