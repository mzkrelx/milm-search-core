/**************************************************************
  Source	: SortValue.java
  Date		: 2011/03/31 22:23:47
**************************************************************/
package org.milmsearch.core;

/**
 * 各並べ替えを表す列挙型です。
 *
 * @author Mizuki Yamanaka
 */
public enum SortValue {
    
    /** 日付の昇順 */
    DEFAULT,
    
    /** 日付の昇順 */
    DATE,
    
    /** 日付の降順 */
    DATE_R,
    
    /** 差し出し人の昇順 */
    FROM,

    /** 差し出し人の昇順 */
    FROM_R;

}
