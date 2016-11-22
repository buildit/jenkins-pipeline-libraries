import groovy.json.JsonSlurper

def getShell() { new shell() }

def dirChecksum(dir) {
    getShell().pipe("find ${dir} -type f -exec md5sum {} \\; | sort | cut -d ' ' -f 1 | md5sum")
}

def listing(dir) {
    String contents = getShell().pipe($/find ${
        dir
    } -not -path '*/\.*'| sort | awk ' BEGIN { ORS = ""; print "["; } { print "\/\@"$0"\/\@"; } END { print "]"; }' | sed "s^\"^\\\\\"^g;s^\/\@\/\@^\", \"^g;s^\/\@^\"^g"/$)
    new JsonSlurper().parseText(contents)
}

def isDirectory(dir) {
    def contents = getShell().pipe("find ${dir} -type d -maxdepth 0")
    contents.length() > 0
}

static String getFileName(String filename) {
    if (filename == null) {
        return null;
    } else {
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }
}

static String getFileBaseName(String filename) {
    return removeFileExtension(getFileName(filename));
}

static String removeFileExtension(String filename) {
    if (filename == null) {
        return null;
    } else {
        int index = indexOfExtension(filename);
        return index == -1 ? filename : filename.substring(0, index);
    }
}

private static int indexOfExtension(String filename) {
    if (filename == null) {
        return -1;
    } else {
        int extensionPos = filename.lastIndexOf(46);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }
}

private static int indexOfLastSeparator(String filename) {
    if (filename == null) {
        return -1;
    } else {
        int lastUnixPos = filename.lastIndexOf(47);
        int lastWindowsPos = filename.lastIndexOf(92);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}

return this
