import groovy.json.JsonOutput

def notify(title, text, color, icon, channel) {
    def slackURL = 'https://hooks.slack.com/services/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'
    def tmpFile = UUID.randomUUID().toString() + ".xml"

    def payloadJson = [
        channel: channel,
        username: "Jenkins Overlord",
        icon_url: icon,
        attachments: [[
            fallback: text,
            color: color,
            fields: [
                [
                    title: title,
                    value: text,
                    short: false
                ]
            ]
        ]]
    ]
    writeFile(file: tmpFile, text: JsonOutput.toJson(payloadJson))
    sh("curl -X POST --data-binary @${tmpFile} ${slackURL}")
    sh("rm ${tmpFile}")
}

return this