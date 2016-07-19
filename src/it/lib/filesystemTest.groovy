filesystem = load "lib/filesystem.groovy"

jenkinsTestRunner.test("should calculate same checksum for different folders with same content"){
    def src = createDirectoryWithRandomContent()
    def target = UUID.randomUUID().toString()
    copyDirectory(src, target)
    def srcChecksum = filesystem.dirChecksum(src)
    def targetChecksum = filesystem.dirChecksum(target)
    jenkinsTestRunner.assertEquals(srcChecksum, targetChecksum)
    cleanUp(src)
    cleanUp(target)
}

jenkinsTestRunner.test("should calculate different checksum for different folders with different content"){
    def src = createDirectoryWithRandomContent()
    def target = createDirectoryWithRandomContent()
    def srcChecksum = filesystem.dirChecksum(src)
    def targetChecksum = filesystem.dirChecksum(target)
    jenkinsTestRunner.assertNotEquals(srcChecksum, targetChecksum)
    cleanUp(src)
    cleanUp(target)
}

jenkinsTestRunner.test("should list all files in nested directories"){
    def path = UUID.randomUUID().toString()
    def expected = ["${path}".toString(), "${path}/somefile.txt".toString(), "${path}/a_directory".toString(), "${path}/a_directory/another.txt".toString()]
    sh("mkdir ${path}; echo 'test' > ${path}/somefile.txt; mkdir ${path}/a_directory; echo 'test' > ${path}/a_directory/another.txt")
    def actual = filesystem.listing(path)
    jenkinsTestRunner.assertListEquals(expected, actual)
    cleanUp(path)
}

jenkinsTestRunner.test("should exclude hidden files from listing"){
    def path = UUID.randomUUID().toString()
    def expected = ["${path}".toString()]
    sh("mkdir ${path}; echo 'test' > ${path}/.hidden;")
    def actual = filesystem.listing(path)
    jenkinsTestRunner.assertListEquals(expected, actual)
    cleanUp(path)
}

jenkinsTestRunner.test("should return empty list for missing directory"){
    def path = UUID.randomUUID().toString()
    def actual = filesystem.listing(path)
    jenkinsTestRunner.assertListEquals([], actual)
}

jenkinsTestRunner.test("should find directory"){
    def path = createDirectoryWithRandomContent()
    def actual = filesystem.isDirectory(path)
    jenkinsTestRunner.assertTrue(actual)
    cleanUp(path)
}

jenkinsTestRunner.test("should find file"){
    def path = createFileWithRandomContent()
    def actual = filesystem.isDirectory(path)
    jenkinsTestRunner.assertFalse(actual)
    cleanUp(path)
}

private void cleanUp(target) {
    sh("rm -r ${target}")
}

private void copyDirectory(src, target) {
    sh("cp -R ${src} ${target}")
}

private createDirectoryWithRandomContent() {
    def path = UUID.randomUUID().toString()
    sh("mkdir '${path}'")
    dir(path) {
        for(int i=0; i< 5; i++){
            createFileWithRandomContent()
        }
    }
    return path
}

private createFileWithRandomContent() {
    def name = UUID.randomUUID().toString()
    writeFile(file: name, text: UUID.randomUUID().toString())
    return name
}

return this
