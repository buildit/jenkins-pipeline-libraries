package utilities

class ReadFromResources {

    static def String readFromResources(String path, String basePath = "") {
        String md5Path = java.security.MessageDigest.getInstance("MD5").digest(path.bytes).encodeHex().toString()
        println("path: ${path}, md5Path: ${md5Path}")
        String fullPath = basePath + md5Path
        String url = URLDecoder.decode(new ReadFromResources().getClass().getClassLoader().getResource(fullPath).toString().replace("file:", ""), "UTF8")
        String result = new File(url).text
        return result
    }
}
