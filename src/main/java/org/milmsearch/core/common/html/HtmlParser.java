/**************************************************************
  Source	: HtmlParser.java
  Date		: 2011/03/28 23:10:43
 **************************************************************/
package info.one.ideal.milm.search.common.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * @author Mizuki Yamanaka
 */
public class HtmlParser {

    /** タグの正規表現 */
    private static Pattern TAG_PATTERN = Pattern
            .compile("(<([^ >]+)([^>]*)>)([^<]*)");

    /** タグのマッチャー */
    private final Matcher matcher;

    /** タグ情報 */
    private Tag tag;

    /**
     * コンストラクタです。
     * 
     * @param src HTMLソース
     */
    public HtmlParser(String src) {
        this.matcher = TAG_PATTERN.matcher(src);
    }

    /**
     * 次のHTMLタグがあるかどうかを検査します。
     * 
     * @return 存在する場合はtrue
     */
    public boolean hasNext() {
        boolean hasNext = this.matcher.find();
        if (hasNext) {
            this.tag = new Tag(this.matcher.group(1), this.matcher.group(2), this.matcher.group(3),
                    this.matcher.group(4));
        }
        return hasNext;
    }

    /**
     * 次のHTMLタグを返します。
     * 
     * @return タグをあらわすオブジェクト
     */
    public Tag next() {
        return this.tag;
    }
}