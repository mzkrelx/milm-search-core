/**************************************************************
  Source  : MailResource.java
  Date    : 2011/03/07 10:30:47
**************************************************************/
package info.one.ideal.milm.search.wink;

import info.one.ideal.milm.search.MilmSearchException;
import info.one.ideal.milm.search.SearchCondition;
import info.one.ideal.milm.search.SearchField;
import info.one.ideal.milm.search.SearchResult;
import info.one.ideal.milm.search.SearchService;
import info.one.ideal.milm.search.SortValue;
import info.one.ideal.milm.search.crawling.Mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    /** ログ */
    private final Log log = LogFactory.getLog(MailResource.class);
    
    /**
     * /mails にGETアクセスで呼び出される処理。
     * メールを検索してメール情報のXML文書(AtomPub仕様)を返します。 
     * 
     * @param queryStr     クエリ文字列
     * @param fieldName     検索フィールド文字列
     * @param sortStr 並び替え指定文字列
     * @param itemCountPerPage        1ページに表示するメール数
     * @param pageNumber      取得するページ番号
     * @return          検索結果メールのXML文書
     */
    @GET
    @Produces("application/atom+xml")
    public Response searchMails(@QueryParam("q")         String queryStr,
                                @QueryParam("field")     SearchField searchField,
                                @QueryParam("sortValue") SortValue sortValue,
                                @QueryParam("pp")        int itemCountPerPage,
                                @QueryParam("page")      int pageNumber) {
        if (queryStr == null || "".equals(queryStr.trim())) {
            Response.noContent().build();
        }
        Document document = DocumentHelper.createDocument();
        try {
            SearchService searchService = new SearchService();
            SearchResult searchResult = searchService
                    .search(new SearchCondition(searchField, queryStr,
                            itemCountPerPage, pageNumber, sortValue));
    
            // TODO ROME使って作る
            // TODO CDATA
            Element feed = document.addElement("feed");
            feed.addNamespace("", "http://www.w3.org/2005/Atom");
            feed.addAttribute("total", String.valueOf(searchResult.getTotalCount()));
            Element title = feed.addElement("title");
            title.addText("setuco-public Mailing List Search");
            Element updated = feed.addElement("updated");
            updated.addText(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPAN)
            .format(new Date()));
            Element id = feed.addElement("id");
            id.addText("setuco-public Mailing List Search");

            for (Mail mail : searchResult.getMailList()) {
                Element entry = feed.addElement("entry");
                entry.addElement("title").addCDATA(mail.getSubject());
                entry.addElement("link").addAttribute("src", mail.getMailUrl());
                entry.addElement("summary").addCDATA(searchService.highlight(searchField, queryStr, mail.getMailText()));
                entry.addElement("updated").addText(mail.getDateRFC3339());
                entry.addElement("id").addText(mail.extractSubjectHeader());    // TODO URLのケツ
//                Element author = entry.addElement("author");
//                author.addElement("name").addCDATA(mail.getFromName());
//                author.addElement("email").addCDATA(mail.getFromEmail());
            }
        } catch (MilmSearchException e) {
            log.error("検索中に障害が発生しました。", e);
            return Response.serverError().build();
        }

        byte[] entity = document.asXML().getBytes();
        return Response.ok(entity).build();
    }
    
    @GET
    @Produces("text/plain")
    @Path("{id}/content")
    public Response mailText() {
        return null; // TODO
    }
    
	
}
/*   ======XMLの雛形======
 * 
 * <feed xmlns="http://....">
 *   <entry>
 *     <title>件名</title>
 *     <link src=リンク/>
 *     <id>Luceneのスコアか件名?</id>
 *     <summary>概要</summary>
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

