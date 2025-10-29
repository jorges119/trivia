package com.onesockpirates.common.Extensions

import java.security.MessageDigest

object StringExtensions {
  implicit class RichString(val str: String) extends AnyVal {
    def hash: String = MessageDigest
      .getInstance("MD5")
      .digest(str.getBytes)
      .map("%02X".format(_))
      .mkString
  }
}
