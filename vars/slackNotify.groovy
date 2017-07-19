def call(String title, String text, String color, String icon, String channel) {
  def slackInst = new slack();
  slackInst.notify(title, text, color, icon, channel)
}
