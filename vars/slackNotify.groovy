def call(String title, String text, String color, String icon, String channel) {
    echo "slackNotify start"
    def slackInst = new slack();
    slackInst.notify(title, text, color, icon, channel)
}
