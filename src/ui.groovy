def dropdown(name, description, closure) {
    def list = closure()

    if (!list) {
        return [
            $class: 'ChoiceParameterDefinition',
            choices: "",
            description: description,
            name: name
        ]
    }

    String choices = "\n"
    for (int i = 0; i < (list.size() as Integer); i++) {
        choices = "${choices}${list[i]}\n"
    }

    return [
        $class: 'ChoiceParameterDefinition',
        choices: choices,
        description: description,
        name: name
    ]
}

def selectTag(tags) {
    input(
        message: "Select tag",
        parameters: [
            dropdown("tags", "Tag") { tags }
        ],
        submitter: null
    )
}

return this
