package utilities

import static org.junit.Assert.fail

trait AssertCommandAndExecute {

    def assertCommandAndExecute(String expected, String actual, def cl){
        if(actual.equals(expected)){
            cl()
        }else{
            fail("Unexpected call. Expected: \"${expected}\", Actual: \"${actual}\"")
        }
    }
}
