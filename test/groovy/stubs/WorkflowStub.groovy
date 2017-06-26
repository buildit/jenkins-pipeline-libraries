package stubs

import groovy.transform.ToString
import groovy.util.logging.Slf4j;

@Slf4j
@ToString
class WorkflowStub extends Script {

    def node(String nodeName, cl) {
        log.info(nodeName)
        cl()
    }

    def withCredentials(Collection args, cl) {
        env = [:]
        def values = args.iterator().next()
        if (values.$class == 'UsernamePasswordMultiBinding') {
            env.put(values.usernameVariable, values.usernameVariable)
            env.put(values.passwordVariable, values.passwordVariable)
        }
        if (values.$class == 'StringBinding') {
            env.put(values.variable, values.variable)            
        }
        cl()
    }

    def load(script) {
        log.info("Loading ${script}")
        return new WorkflowStub()
    }

    def stage(name) {
        log.info("Entering stage ${name}")
    }

    def tool(version) {
        return version
    }

    def readFile(path) {
        return new File(path as String).text
    }

    def writeFile(args) {
        def targetFile = new File(args.file)
        if (!fileExists(targetFile.getAbsolutePath())) {
            println(targetFile.getAbsolutePath())
            targetFile.createNewFile()
        }
        targetFile.write(args.text)
    }

    def fileExists(path) {
        new File(path).exists()
    }

    def methodMissing(String name, args) {
        log.info("Missing method ${name} : ${args}")
    }

    def propertyMissing(String name) {
        return name
    }

    def getPipelineConfig() {
        return new WorkflowStub()
    }

    def echo(String arg) {
        println(arg)
    }

    def run() {
    }

    def sleep(Map args) {
    }

    def timeout(Map args, closure) {
        closure()
    }

    def waitUntil(closure) {
        closure()
    }


    def ws(closure) {
        closure()
    }

    def wrap(Map args, closure) {
        closure()
    }

}
