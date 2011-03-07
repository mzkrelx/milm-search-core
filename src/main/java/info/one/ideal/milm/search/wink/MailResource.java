/**************************************************************
  Source  : MailResource.java
  Date    : 2011/03/07 10:30:47
**************************************************************/
package info.one.ideal.milm.search.wink;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * メールリソースクラス
 * 
 * @author Mizuki Yamanaka
 */
@Path("/mails")
public class MailResource {

    /**
     * /mails にGETアクセスで呼び出される処理。
     * メールを検索してメール情報のXML文書(AtomPub仕様)を返します。 
     * 
     * @param query     クエリ文字列
     * @param field     検索フィールド文字列
     * @param sortValue 並び替え指定文字列
     * @param pp        1ページに表示するメール数
     * @param page      取得するページ番号
     * @return          検索結果メールのXML文書
     */
    @GET
    @Produces("text/xml")
    public Response searchMails(@QueryParam("q")         String query,
                                @QueryParam("field")     String field,
                                @QueryParam("sortValue") String sortValue,
                                @QueryParam("pp")        String pp,
                                @QueryParam("page")      String page) {
        /*   ======XMLの雛形======
         * 
         * <feed xmlns="http://....">
         *   <entry>
         *     <title>件名</title>
         *     <link src=リンク/>
         *     <id>Luceneのスコアか件名?</id>
         *     <published>送信日時</published>
         *     <author>
         *       <name>差出人</name>
         *       <email>メールアドレス</email>
         *     </author>
         *     <content>本文</content>
         *   </entry>
         *   <entry>
         *    ---
         *   </entry>
         * </feed>
         */
        Document document = DocumentHelper.createDocument();

        Element feed = document.addElement("feed");
        feed.addNamespace("", "http://www.w3.org/2005/Atom");

        Element entry = feed.addElement("entry");
        entry.addElement("title").addText("query[" + query + "], field=[" + field
                + "], sortValue=[" + sortValue + "], pp=[" + pp + "], page=["
                + page + "]");
        entry.addElement("link").addAttribute("src", "リンクURL");
        entry.addElement("id").addText("ID");
        entry.addElement("published").addText("送信日時");
        Element author = entry.addElement("author");
        author.addElement("name").addText("差出人名");
        author.addElement("email").addText("メールアドレス");
        entry.addElement("content").addText("本文");

        byte[] entity = document.asXML().getBytes();
        return Response.ok(entity).type("text/xml").build();
    }
	
}
