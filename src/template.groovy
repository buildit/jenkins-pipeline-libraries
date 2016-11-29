import groovy.text.GStringTemplateEngine
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def transform(String template, Map map) {
    new GStringTemplateEngine().createTemplate(template).make(map)
}

return this
