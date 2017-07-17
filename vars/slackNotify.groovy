def call(String title, String text, String color, String icon, String channel) {
    echo "slackNotify start"
    def slack = new slack();
    slack.notify(title, text, color, icon, channel)
}
