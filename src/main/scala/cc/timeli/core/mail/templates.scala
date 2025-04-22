package cc.timeli.core.mail

import pencil.*
import pencil.data.*
import pencil.syntax.*

object templates {
  private val fromMailbox = Mailbox("mail", "timeli.cc")

  def passwordResetEmail(toMailbox: Mailbox, link: String) = {
    Email.mime(
      From(fromMailbox),
      To(toMailbox),
      Subject("Timeli - Reset your password"),
      Body.Html(s"""
        <!DOCTYPE html>
          <html>
            <head>
              <meta charset="UTF-8" />
              <title>Password Reset</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <h2 style="color: #333;">Reset Your Password</h2>
                <p>Hi,</p>
                <p>We received a request to reset your password. Click the button below to set a new one:</p>
                <p style="text-align: center;">
                  <a href="${link}" style="background-color: #007bff; color: #ffffff; padding: 12px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">
                    Reset Password
                  </a>
                </p>
                <p>If you didn’t request a password reset, you can safely ignore this email.</p>
                <p>Thanks,<br />The Timeli Team</p>
              </div>
           </body>
          </html>
    """),
    )
  }
}
