import groovy.text.GStringTemplateEngine

def transform(String template, Map map) {
    new GStringTemplateEngine().createTemplate(template).make(map)
}

return this