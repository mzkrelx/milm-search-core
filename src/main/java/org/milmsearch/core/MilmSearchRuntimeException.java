/**************************************************************
  Source	: MilmSearchRuntimeException.java
  Date		: 2011/03/07 18:25:13
**************************************************************/
package info.one.ideal.milm.search;

/**
 * 非チェック例外の基底クラス
 *
 * @author Mizuki Yamanaka
 */
public class MilmSearchRuntimeException extends RuntimeException {

    /** シリアルバーアジョンUID */
    private static final long serialVersionUID = -2211953978460905955L;

    /**
     * コンストラクタ
     *
     */
    public MilmSearchRuntimeException() {
    }

    /**
     * コンストラクタ
     *
     * @param message
     */
    public MilmSearchRuntimeException(String message) {
        super(message);
    }

    /**
     * コンストラクタ
     *
     * @param cause
     */
    public MilmSearchRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ
     *
     * @param message
     * @param cause
     */
    public MilmSearchRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
