package jenkinsUnit

def test(name, closure) {
    ws {
        echo("*********************************************************************************************************")
        echo("Running Test: \"${name}\"")
        echo("*********************************************************************************************************")
        closure()
        echo("*********************************************************************************************************")
    }
}

def assertEquals(expected, actual, message = "") {
    if (!expected.equals(actual)) {
        if (message) {
            error(message.toString())
        }
        error("${actual} is not equal to ${expected}")
    }
}

def assertListEquals(expected, actual, message = "") {
    def difference = difference(expected, actual)
    if (difference.size() != 0) {
        if (message) {
            error(message.toString())
        }
        error("Expected: ${expected}, Actual: ${actual}, Difference: ${difference}")
    }
}

def assertTrue(actual, message = "") {
    if (!actual) {
        if (message) {
            error(message.toString())
        }
        error("Result is false - expected true")
    }
}

def assertFalse(actual, message = "") {
    if (actual) {
        if (message) {
            error(message.toString())
        }
        error("Result is true - expected false")
    }
}

def assertNotEquals(unexpected, actual, message = "") {
    if (unexpected.equals(actual)) {
        if (message) {
            error(message.toString())
        }
        error("${actual} is equal to ${unexpected}")
    }
}

private difference(first, second) {
    def commons = first.intersect(second)
    def difference = first.plus(second)
    difference.removeAll(commons)

    return difference
}

return this
