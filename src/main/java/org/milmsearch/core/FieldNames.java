/**************************************************************
  Source	: FieldNames.java
  Date		: 2011/04/05 21:25:21
**************************************************************/
package org.milmsearch.core;

/**
 * 検索フィールドの定数クラスです。
 *
 * @author Mizuki Yamanaka
 */
public class FieldNames {

    /** 件名 */
    public static final String SUBJECT = "subject";
    
    /** 送信者名 */
    public static final String FROM = "from";

    /** 送信者メールアドレス */
    public static final String EMAIL = "email";

    /** メールのURL */
    public static final String URL = "url";

    /** 送信日時 */
    public static final String DATE = "date";

    /** メール本文 */
    public static final String TEXT = "text";

    /** 最後のメールの日時 */
    @Deprecated
    public static final String LAST_MAIL_DATE = "lastMailDate";

}
