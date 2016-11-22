package utilities

import static org.junit.Assert.fail

class AssertAndExecute {

    static def assertCommandAndExecute(String expected, String actual, def cl) {
        if (actual.equals(expected)) {
            cl()
        } else {
            fail("Unexpected call. Expected: \"${expected}\", Actual: \"${actual}\"")
        }
    }

    static def assertCommandRegexAndExecute(String expected, String actual, def cl) {
        if (actual ==~ /${expected}/) {
            cl()
        } else {
            fail("Unexpected call. Expected: \"${expected}\", Actual: \"${actual}\"")
        }
    }
}
