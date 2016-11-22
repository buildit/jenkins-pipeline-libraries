package utilities

import org.codehaus.groovy.control.CompilerConfiguration
import stubs.WorkflowStub

class ScriptLoader {
    public static load(String script) {
        def CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = new WorkflowStub().getClass().getCanonicalName()
        def shell = new GroovyShell(this.class.classLoader, new Binding(), compilerConfiguration)
        shell.getClassLoader().addURL(new File("src").toURL())
        return shell.evaluate("new " + script + "()")
    }
}
