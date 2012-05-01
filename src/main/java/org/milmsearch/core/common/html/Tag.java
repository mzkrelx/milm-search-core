/**************************************************************
  Source	: Tag.java
  Date		: 2011/03/28 23:06:46
 **************************************************************/
package info.one.ideal.milm.search.common.html;

/**
 * HTMLのタグ情報クラスです。
 * 
 * @author Mizuki Yamanaka
 */
public class Tag {
    
    /** タグの文字列 例)<B> */
    private final String tagStr;

    /** タグ名 */
    private final String tagName;

    /** タグ属性 */
    private final String tagAttribute;

    /** タグの中身の文字列 */
    private final String innerHtml;

    public Tag(String tagStr, String tagName, String tagAttribute,
            String innerHtml) {
        this.tagStr = tagStr;
        this.tagName = tagName;
        this.tagAttribute = tagAttribute;
        this.innerHtml = innerHtml;
    }

    /**
     * tagStr を取得します。
     *
     * @return tagStr
     */
    public String getTagStr() {
        return this.tagStr;
    }

    /**
     * tagName を取得します。
     *
     * @return tagName
     */
    public String getTagName() {
        return this.tagName;
    }

    /**
     * tagAttribute を取得します。
     *
     * @return tagAttribute
     */
    public String getTagAttribute() {
        return this.tagAttribute;
    }

    /**
     * innerHtml を取得します。
     *
     * @return innerHtml
     */
    public String getInnerHtml() {
        return this.innerHtml;
    }

    @Override
    public String toString() {
        return "タグ:[" + tagName + "], 属性:[" + tagAttribute + "], テキスト: ["
                + innerHtml + "]";
    }

}
