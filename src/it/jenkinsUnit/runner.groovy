import groovy.json.JsonSlurper

def run(location){
    def items = [location]
    if(isDirectory(location)){
        items = listing(location)
    }
    for(int i = 0; i < items.size(); i++){
        if("${items[i]}".endsWith("Test.groovy")){
            echo("\n")
            echo("Running Test File: \"${items[i]}\"")
            echo("\n")
            load(items[i])
        }
    }
}

def listing(dir){
    String contents = pipe($/find ${dir} | sort | awk ' BEGIN { ORS = ""; print "["; } { print "\/\@"$0"\/\@"; } END { print "]"; }' | sed "s^\"^\\\\\"^g;s^\/\@\/\@^\", \"^g;s^\/\@^\"^g"/$)
    new JsonSlurper().parseText(contents)
}

def isDirectory(dir){
    def contents = pipe("find ${dir} -type d -maxdepth 0")
    contents.length() > 0
}

def pipe(command){
    String fileName = UUID.randomUUID().toString() + ".tmp"
    sh("${command} | tee ${fileName}")
    def contents = readFile("${fileName}")
    sh("rm ${fileName}")
    return contents
}

return this
