/**************************************************************
  Source	: SearchResult.java
  Date		: 2011/04/05 21:54:04
**************************************************************/
package org.milmsearch.core;

import org.milmsearch.core.crawling.Mail;

import java.util.ArrayList;
import java.util.List;

/**
 * 検索結果を表すクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class SearchResult {
    
    /** メールリスト */
    private final List<Mail> mailList = new ArrayList<Mail>();
    
    /** 全ての検索結果件数 */
    private int totalCount;

    /**
     * mailList を取得します。
     *
     * @return mailList
     */
    public List<Mail> getMailList() {
        return new ArrayList<Mail>(this.mailList);
    }

    /**
     * mailList を設定します。
     *
     * @param mailList 設定する mailList
     */
    public void setMailList(List<Mail> mailList) {
        this.mailList.removeAll(mailList);
        this.mailList.addAll(mailList);
    }

    /**
     * totalCount を取得します。
     *
     * @return totalCount
     */
    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * totalCount を設定します。
     *
     * @param totalCount 設定する totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
