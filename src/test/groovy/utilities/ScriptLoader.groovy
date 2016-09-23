package utilities

import stubs.WorkflowStub
import org.codehaus.groovy.control.CompilerConfiguration
import NonCPS;

class ScriptLoader {
    public static load(String script){
        def CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = new WorkflowStub().getClass().getCanonicalName()
        return new GroovyShell(this.class.classLoader, new Binding(), compilerConfiguration).evaluate(new File(script))
    }
}