package org.milmsearch.core.dao

/** 存在しないフィールドを扱おうとしたときの例外 */
class FindException(msg: String) extends Exception(msg)