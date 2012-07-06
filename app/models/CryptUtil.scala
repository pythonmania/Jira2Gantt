package models

import org.jasypt.util.text._

object CryptUtil {
  def decrypt(passwordSeed: String, passwordCrypt: String): String = {
    val textEncryptor: BasicTextEncryptor = new BasicTextEncryptor
    textEncryptor.setPassword(passwordSeed)
    textEncryptor.decrypt(passwordCrypt)
  }

  def encrypt(passwordSeed: String, password: String): String = {
    val textEncryptor: BasicTextEncryptor = new BasicTextEncryptor
    textEncryptor.setPassword(passwordSeed)
    textEncryptor.encrypt(password)
  }
}
