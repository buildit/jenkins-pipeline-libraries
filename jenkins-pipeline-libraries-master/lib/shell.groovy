
def pipe(command){
    String fileName = UUID.randomUUID().toString() + ".tmp"
    sh("${command} | tee ${fileName}")
    def contents = readFile("${fileName}")
    sh("rm ${fileName}")
    return contents
}

return this