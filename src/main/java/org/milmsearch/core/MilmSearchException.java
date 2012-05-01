/**************************************************************
  Source	: MilmSearchException.java
  Date		: 2011/03/07 18:21:47
**************************************************************/
package info.one.ideal.milm.search;

/**
 * チェック例外の基底クラス
 *
 * @author Mizuki Yamanaka
 */
public class MilmSearchException extends Exception {

    /** シリアルバーアジョンUID */
    private static final long serialVersionUID = -8557223171589632407L;

    /**
     * コンストラクタ
     *
     */
    public MilmSearchException() {
    }

    /**
     * コンストラクタ
     *
     * @param message
     */
    public MilmSearchException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     *
     * @param cause
     */
    public MilmSearchException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     *
     * @param message
     * @param cause
     */
    public MilmSearchException(String message, Throwable cause) {
        super(message, cause);
    }

}
