package utilities

class ResourcePath {
    static def String resourcePath(String path, String basePath = "") {
        def result = URLDecoder.decode(new ResourcePath().getClass().getClassLoader().getResource("${basePath}/${path}").toString().replace("file:", ""), "UTF8")
        println("Full path of ${path} is ${result}")
        return result
    }
}
