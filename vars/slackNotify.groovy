def call(String title, String text, String color, String icon, String channel) {
  def slackInst = new slack();
  slackInst.notify(title, text, color, icon, channel)
  // def slackURL = 'https://hooks.slack.com/services/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'

  // def payloadJson = [
  //   channel: channel,
  //   username: "Jenkins Overlord",
  //   icon_url: icon,
  //   attachments: [[
  //     fallback: text,
  //     color: color,
  //     fields: [
  //       [
  //         title: title,
  //         value: text,
  //         short: false
  //       ]
  //     ]
  //   ]]
  // ]
  // echo "curl -X POST --data \'payload=${payloadJson}\' ${slackURL}"
}
