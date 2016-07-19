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

def test(name, closure){
    ws {
        echo("*********************************************************************************************************")
        echo("Running Test: \"${name}\"")
        echo("*********************************************************************************************************")
        closure()
        echo("*********************************************************************************************************")
    }
}

def assertEquals(expected, actual, message = ""){
    if(!expected.equals(actual)){
        if(message){
            error(message.toString())
        }
        error("${actual} is not equal to ${expected}")
    }
}

def assertListEquals(expected, actual, message = ""){
    def difference = difference(expected, actual)
    if(difference.size() != 0){
        if(message){
            error(message.toString())
        }
        error("Expected: ${expected}, Actual: ${actual}, Difference: ${difference}")
    }
}

def assertTrue(actual, message = ""){
    if(!actual){
        if(message){
            error(message.toString())
        }
        error("Result is false - expected true")
    }
}

def assertFalse(actual, message = ""){
    if(actual){
        if(message){
            error(message.toString())
        }
        error("Result is true - expected false")
    }
}

def assertNotEquals(unexpected, actual, message = ""){
    if(unexpected.equals(actual)){
        if(message){
            error(message.toString())
        }
        error("${actual} is equal to ${unexpected}")
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

def difference(first, second) {
    def commons = first.intersect(second)
    def difference = first.plus(second)
    difference.removeAll(commons)

    return difference
}

def pipe(command){
    String fileName = UUID.randomUUID().toString() + ".tmp"
    sh("${command} | tee ${fileName}")
    def contents = readFile("${fileName}")
    sh("rm ${fileName}")
    return contents
}

return this
