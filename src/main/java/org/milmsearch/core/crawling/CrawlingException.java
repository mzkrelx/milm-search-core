/**************************************************************
  Source	: MilmSearchException.java
  Date		: 2011/03/07 18:21:47
**************************************************************/
package info.one.ideal.milm.search.crawling;

/**
 * チェック例外の基底クラス
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingException extends Exception {

    /** シリアルバーアジョンUID */
    private static final long serialVersionUID = -6487223171589632407L;

    /**
     * コンストラクタ
     *
     */
    public CrawlingException() {
    }

    /**
     * コンストラクタ
     *
     * @param message
     */
    public CrawlingException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     *
     * @param cause
     */
    public CrawlingException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     *
     * @param message
     * @param cause
     */
    public CrawlingException(String message, Throwable cause) {
        super(message, cause);
    }

}
