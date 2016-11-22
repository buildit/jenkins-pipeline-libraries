def getTemplate() { new template() }

def getFilesystem() { new filesystem() }

def getTemplateFiles() {[polling: readFile("templates/app-polling-pipeline.xml"), manual: readFile("templates/app-manual-pipeline.xml")]}

def createPipelineJob(jobName, gitUrl, branch, pipelinePath, shouldPoll, jenkinsUrl, jenkinsCredentials, jenkinsScmPoll) {
    def tmpFile = UUID.randomUUID().toString() + ".xml"
    def templateFile = shouldPoll ? getTemplateFiles()["polling"] : getTemplateFiles()["manual"]
    def jobXML = getTemplate().transform(
            templateFile,
            [
                    git_url             : gitUrl,
                    branch              : branch,
                    pipeline_script_path: pipelinePath,
                    jenkins_credentials : jenkinsCredentials,
                    jenkins_scm_poll    : jenkinsScmPoll
            ]
    )
    writeFile(file: tmpFile, text: jobXML)

    // create the jenkins job the application pipeline
    sh("curl --header 'Content-Type: application/xml' --data-binary @${tmpFile} ${jenkinsUrl}/createItem?name=${jobName}")

    sh("rm ${tmpFile}")
}

def createFreestyleJobs(gitUrl, jenkinsUrl) {

    git(url: gitUrl)

    def jenkinsJobsPath = "./jenkinsJobs"
    def listing = getFilesystem().listing(jenkinsJobsPath)
    for (def i = 0; i < listing.size(); i++) {
        def filepath = listing[i]
        if (filepath != jenkinsJobsPath) {
            def jobName = new File(filepath).getName().split("\\.")[0]
            sh("curl --header 'Content-Type: application/xml' --data-binary @${filepath} ${jenkinsUrl}/createItem?name=${jobName}")
        }
    }
}

return this
