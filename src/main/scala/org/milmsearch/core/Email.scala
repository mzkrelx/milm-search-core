package org.milmsearch.core
import java.util.Date
import javax.mail.internet.InternetAddress
import java.net.URL

case class Email(
  id: String,
  date: Date,
  from: InternetAddress,
  subject: String,
  text: String,
  snippet: String,  // TODO スニペットはここにあるべきか?
  url: URL       // TODO url はここにあるべきか?
)