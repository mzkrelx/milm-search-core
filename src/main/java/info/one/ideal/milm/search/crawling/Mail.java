/**************************************************************
  Source	: Mail.java
  Date		: 2011/03/09 22:48:50
 **************************************************************/
package info.one.ideal.milm.search.crawling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * メールを表すクラスです。
 * 
 * @author Mizuki Yamanaka
 */
public class Mail {
    
    /** ID */
    private int id;
    
    /** 送信日時 */
    private Date date;

    /** 差出人名 */
    private String fromName;

    /** 差出人メールアドレス */
    private String fromEmail;

    /** 件名 */
    private String subject;

    /** メール本文 */
    private String mailText;
    
    /** メールの概要 */
    private String mailSummary;

    /** メールのURL */
    private String mailUrl;

    /**
     * 同値判定をします。
     * 
     * @return 同値であれば true
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        Mail otherSimpleMail = (Mail) other;
        if (this.date == null) {
            if (otherSimpleMail.date != null) {
                return false;
            }
        } else if (!this.date.equals(otherSimpleMail.date)) {
            return false;
        }
        return true;
    }

    /**
     * 件名の最初のML番号の部分を抜き出します。
     * もし件名が設定されていない場合例外が発生します。
     * もしML番号の部分が特定できない場合は、nullを返します。
     * 
     * @return ML番号の部分 例)[ML:001]
     */
    public String extractSubjectHeader() throws UnsupportedOperationException {
        if (this.subject == null) {
            throw new UnsupportedOperationException();
        }
        int headerEndIndex = this.subject.indexOf(']');
        if (headerEndIndex < 0) {
            return null;
        }
        return this.subject.substring(0, headerEndIndex + 1);
    }

    /**
     * 送信日時を取得します。
     * 
     * @return 送信日時
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * 送信日時をRFC3339形式で取得します。
     * 
     * @return 送信日時文字列
     */
    public String getDateRFC3339() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPAN)
                .format(this.date);
    }

    /**
     * 送信日時を文字列で取得します。
     * 
     * @return 送信日時文字列
     */
    public String getDateStr() {
        return new SimpleDateFormat("yyyy年MM月dd日 (EEE) HH:mm:ss", Locale.JAPAN)
                .format(this.date);
    }

    /**
     * 差出人メールアドレスを取得します。
     * 
     * @return 差出人メールアドレス
     */
    public String getFromEmail() {
        return this.fromEmail;
    }
    
    /**
     * 差出人名を取得します。
     * 
     * @return 差出人名
     */
    public String getFromName() {
        return this.fromName;
    }    

    /**
     * id を取得します。
     *
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * mailSummary を取得します。
     *
     * @return mailSummary
     */
    public String getMailSummary() {
        return this.mailSummary;
    }

    /**
     * メール本文を取得します。
     * 
     * @return メール本文
     */
    public String getMailText() {
        return this.mailText;
    }

    /**
     * メールのURLを取得します。
     * 
     * @return メールのURL
     */
    public String getMailUrl() {
        return this.mailUrl;
    }

    /**
     * 件名を取得します。
     * 
     * @return 件名
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * 送信日時を設定します。
     * 
     * @param date 送信日時
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 差出人メールアドレスを設定します。
     * 
     * @param fromEmail 差出人メールアドレス
     */
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    /**
     * 差出人名を設定します。
     * 
     * @param fromName 差出人名
     */
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    /**
     * id を設定します。
     *
     * @param id 設定する id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * mailSummary を設定します。
     *
     * @param mailSummary 設定する mailSummary
     */
    public void setMailSummary(String mailSummary) {
        this.mailSummary = mailSummary;
    }

    /**
     * メール本文を設定します。
     * 
     * @param mailText メール本文
     */
    public void setMailText(String mailText) {
        this.mailText = mailText;
    }

    /**
     * メールのURLを設定します。
     * 
     * @param mailUrl
     *            メールのURL
     */
    public void setMailUrl(String mailUrl) {
        this.mailUrl = mailUrl;
    }

    /**
     * 件名を設定します。
     * 
     * @param subject
     *            件名
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * インスタンスの文字列を取得します。
     * 
     * @return インスタンスの文字列
     */
    @Override
    public String toString() {
        return "送信日時=["
                + this.date
                + "]"
                + ", 差出人=["
                + this.fromName
                + "]"
                + ", 件名=["
                + this.subject
                + "]"
                + ", 本文(200文字)=["
                + this.mailText.substring(
                        0,
                        this.mailText.length() >= 200 ? 200 : this.mailText
                                .length()) + "]";
    }
}
