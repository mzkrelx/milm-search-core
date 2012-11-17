package org.milmsearch.core.dao

/** 存在しないDBテーブルのフィールドを扱おうとしたときの例外 */
class NoSuchFieldException(msg: String) extends Exception(msg)