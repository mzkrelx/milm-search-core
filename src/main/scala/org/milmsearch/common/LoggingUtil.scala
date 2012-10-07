package org.milmsearch.common
import net.liftweb.common.LazyLoggable
import net.liftweb.common.Box

/**
 * ログ関連ユーティリティ
 */
object LoggingUtil extends LazyLoggable {

  /**
   * 例外発生時にエラーログを出力する<br/>
   * catch した例外は throw する
   * 
   * @param f 例外を投げる可能性のある処理
   * @return 処理 f の戻り値
   */
  def withErrlog[T](f: => T): T =
    try {
      f
    } catch {
      case e => {
        logger.error(e.getMessage(), e)
        throw e
      }
    } 

  /**
   * 例外発生時にエラーログを出力する<br/>
   * catch した例外は黙殺する
   * 
   * @param f 例外を投げる可能性のある処理
   */
  def withErrlogQuietly(f: => Unit) {
    try {
      f
    } catch {
      case e => logger.error(e.getMessage(), e)
    } 
  }
}
