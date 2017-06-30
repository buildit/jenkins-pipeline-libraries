import groovy.json.JsonOutput

def notify(title, text, color, icon, channel) {
    def slackURL = 'https://hooks.slack.com/services/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'

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
    def encodedReq = URLEncoder.encode(JsonOutput.toJson(payloadJson), "UTF-8")
    sh("curl -X POST " +
            "--data \'payload=${encodedReq}\' ${slackURL}")
}

return this
