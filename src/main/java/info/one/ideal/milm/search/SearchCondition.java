/**************************************************************
  Source	: SearchCondition.java
  Date		: 2011/04/05 22:23:27
**************************************************************/
package info.one.ideal.milm.search;

/**
 * 検索条件を表すクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class SearchCondition {

    /** 検索フィールド */
    private SearchField searchField;
    
    /** クエリ文字列 */
    private String queryStr;
    
    /** 1ページの件数 */
    private int itemCountPerPage;
    
    /** ページ番号 */
    private int pageNumber;
    
    /** 並べ替え項目 */
    private SortValue sortValue;

    /** デフォルトの検索フィールド */
    public final SearchField defaultSearchField = SearchField.text;
    
    /** デフォルトの1ページの件数 */
    public final int defaultItemCountPerPage = 20;
    
    /** デフォルトのページ番号 */
    public final int defaultPageNumber = 1;
    
    /** デフォルトの並べ替え項目 */
    public final SortValue defaultSortValue = SortValue.DEFAULT;
    
    /**
     * コンストラクタ
     *
     * @param searchField 検索フィールド
     * @param queryStr クエリ文字列
     * @param itemCountPerPage 1ページの件数
     * @param pageNumber ページ番号
     * @param sortValue 並べ替え項目
     */
    public SearchCondition(SearchField searchField, String queryStr,
            int itemCountPerPage, int pageNumber, SortValue sortValue) {
        this.searchField = searchField;
        this.queryStr = queryStr;
        this.itemCountPerPage = itemCountPerPage;
        this.pageNumber = pageNumber;
        this.sortValue = sortValue;
    }

    /**
     * searchField を取得します。
     *
     * @return searchField
     */
    public SearchField getSearchField() {
        if (this.searchField == null) {
            return this.defaultSearchField;
        }
        return this.getSearchField();
    }

    /**
     * searchField を設定します。
     *
     * @param searchField 設定する searchField
     */
    public void setSearchField(SearchField searchField) {
        this.searchField = searchField;
    }

    /**
     * queryStr を取得します。
     *
     * @return queryStr
     */
    public String getQueryStr() {
        return this.queryStr;
    }

    /**
     * queryStr を設定します。
     *
     * @param queryStr 設定する queryStr
     */
    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    /**
     * itemCountPerPage を取得します。
     *
     * @return itemCountPerPage
     */
    public int getItemCountPerPage() {
        if (this.itemCountPerPage <= 0) {
            return this.defaultItemCountPerPage;
        }
        return this.itemCountPerPage;
    }

    /**
     * itemCountPerPage を設定します。
     *
     * @param itemCountPerPage 設定する itemCountPerPage
     */
    public void setItemCountPerPage(int itemCountPerPage) {
        this.itemCountPerPage = itemCountPerPage;
    }

    /**
     * pageNumber を取得します。
     *
     * @return pageNumber
     */
    public int getPageNumber() {
        if (this.pageNumber <= 0) {
            return this.defaultPageNumber;
        }
        return this.pageNumber;
    }

    /**
     * pageNumber を設定します。
     *
     * @param pageNumber 設定する pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * sortValue を取得します。
     *
     * @return sortValue
     */
    public SortValue getSortValue() {
        if (this.sortValue == null) {
            return this.defaultSortValue;
        }
        return this.sortValue;
    }

    /**
     * sortValue を設定します。
     *
     * @param sortValue 設定する sortValue
     */
    public void setSortValue(SortValue sortValue) {
        this.sortValue = sortValue;
    }
}
