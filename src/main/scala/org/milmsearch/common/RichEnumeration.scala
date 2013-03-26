package org.milmsearch.common

/**
 * High functionality Enumeration
 */
abstract class RichEnumeration extends Enumeration {
  /**
   * Return matched `Value` without throw NoSuchException.
   *
   * @param `Enumeration` name
   * @return matched `Value`
   */
  def withNameOption(name: String) =
    try {
      Some(withName(name))
    } catch {
      case e: NoSuchElementException =>
        None
    }
}