/**************************************************************
  Source	: MilmSearchRuntimeException.java
  Date		: 2011/03/07 18:25:13
**************************************************************/
package info.one.ideal.milm.search.crawling;

/**
 * 非チェック例外の基底クラス
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingRuntimeException extends RuntimeException {

    /** シリアルバーアジョンUID */
    private static final long serialVersionUID = -2859953978460905955L;

    /**
     * コンストラクタ
     *
     */
    public CrawlingRuntimeException() {
    }

    /**
     * コンストラクタ
     *
     * @param message
     */
    public CrawlingRuntimeException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     *
     * @param cause
     */
    public CrawlingRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     *
     * @param message
     * @param cause
     */
    public CrawlingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
