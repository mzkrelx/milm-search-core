/**************************************************************
  Source	: UlTagNotExistsException.java
  Date		: 2011/03/07 18:28:15
**************************************************************/
package org.milmsearch.core.crawling;


/**
 * ULタグが見つからなかった時に発生する例外
 *
 * @author Mizuki Yamanaka
 */
public class UlTagNotExistsException extends CrawlingException {

    /** シリアルバージョンUID */
    private static final long serialVersionUID = 5566238913981729159L;

    /**
     * コンストラクタ
     *
     */
    public UlTagNotExistsException() {
        super();
    }

}
