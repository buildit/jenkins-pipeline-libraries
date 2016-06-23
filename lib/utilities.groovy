
def timestamp(Date date = new Date()){
    return date.format('yyyyMMddHHmmss',TimeZone.getTimeZone('GMT')) as String
}

return this
