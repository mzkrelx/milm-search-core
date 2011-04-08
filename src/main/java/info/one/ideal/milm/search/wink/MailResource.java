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

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.common.model.atom.AtomEntry;
import org.apache.wink.common.model.atom.AtomFeed;
import org.apache.wink.common.model.atom.AtomLink;
import org.apache.wink.common.model.atom.AtomPerson;
import org.apache.wink.common.model.atom.AtomText;
import org.apache.wink.common.model.opensearch.OpenSearchQuery;
import org.apache.wink.common.model.synd.SyndLink;
import org.apache.wink.common.model.synd.SyndPerson;

/**
 * メールリソースクラス
 * 
 * @author Mizuki Yamanaka
 */
@Path("/mails")
public class MailResource {

    /** ログ */
    private final Log log = LogFactory.getLog(MailResource.class);
    
    /** 検索サービス */
    private final SearchService searchService = new SearchService();
    
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
        log.debug("Now accessed. q=[" + queryStr + "], field=[" + searchField
                + "], sortValue=[" + sortValue + "], pp=[" + itemCountPerPage
                + "], page=[" + pageNumber + "]");
        if (queryStr == null || "".equals(queryStr.trim())) {
            Response.noContent().build();
        }
        AtomFeed feed = new AtomFeed();
        feed.setId("setuco-public Mailing List Search");
        feed.setTitle(new AtomText("setuco-public Mailing List Search"));
        feed.setUpdated(new Date());
        List<AtomEntry> entryList = feed.getEntries();
        feed.addOpenSearchQuery(new OpenSearchQuery());
        try {
            SearchResult searchResult = searchService
                    .search(new SearchCondition(searchField, queryStr,
                            itemCountPerPage, pageNumber, sortValue));
    
            for (Mail mail : searchResult.getMailList()) {
                AtomEntry entry = new AtomEntry();
                entry.setId(mail.getId());
                entry.setTitle(new AtomText(mail.getSubject()));
                entry.getLinks().add(new AtomLink(new SyndLink(null, mail.getMailUrl(), null)));
                entry.setUpdated(mail.getDate());
                entry.setSummary(new AtomText(mail.getMailSummary()));
                entry.getAuthors().add(new AtomPerson(new SyndPerson(mail.getFromName(), mail.getFromEmail(), null)));
                entryList.add(entry);
            }
        } catch (MilmSearchException e) {
            log.error("検索中に障害が発生しました。", e);
            return Response.serverError().build();
        }

        return Response.ok(feed).build();
    }

    @GET
    @Produces("text/plain")
    @Path("{id}/content")
    public Response mailText() {
        return null; // TODO
    }
    
	
}
