/**************************************************************
  Source	: CralingTimerTask.java
  Date		: 2011/03/07 17:35:11
**************************************************************/
package info.one.ideal.milm.search.crawling;

import info.one.ideal.milm.search.FieldNames;
import info.one.ideal.milm.search.SystemConfig;
import info.one.ideal.milm.search.common.util.DateUtil;
import info.one.ideal.milm.search.lucene.LuceneUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * メールをクローリングして検索インデックスを作成するタスクです。
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingTimerTask extends TimerTask {

    /** ロガー */
    private static Log log = LogFactory.getLog(CrawlingTimerTask.class);

    /** 保存書庫のURL文字列 */
    public String archiveUrlStr = SystemConfig.getArchiveUrl();
    
    /* (非 Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        this.doCrawling();
    }

    /**
     * メール書庫をクローリングして、検索インデックスを更新します。
     */
    private void doCrawling() {
        IndexWriter indexWriter = null;
        try {
            long preLastMailDate = CrawlingProperty.findPreLastMailDate();
            if (preLastMailDate == 0L) {
                log.warn("最後に解析したメール情報が取得できませんでした。全てのメール情報を解析します。");
                indexWriter = new IndexWriter(
                        FSDirectory.open(new File(SystemConfig.getIndexDir())),
                        new CJKAnalyzer(Version.LUCENE_29),
                        true,    // trueなら空の状態から作り、falseなら追加する(既に追加されたものでも新たに増える)。
                        MaxFieldLength.UNLIMITED);
            } else {
                log.info("最後に解析したメール情報を取得しました。途中から解析します。");
                log.info("前回の最後に解析したメールの送信日時は " + DateUtil.convertDate2Str(new Date(preLastMailDate)));
                indexWriter = new IndexWriter(
                        FSDirectory.open(new File(SystemConfig.getIndexDir())),
                        new CJKAnalyzer(Version.LUCENE_29),
                        false,    // trueなら空の状態から作り、falseなら追加する(既に追加されたものでも新たに増える)。
                        MaxFieldLength.UNLIMITED);
            }
            List<String> mailUrlList = this.createMailUrlList(this.createMonthlyArchiveUrlList(), preLastMailDate);
            
            int madeCount = 0;
            final int commitTerm = 100;
            long lastMailDate = 0L;
            for (String mailUrlStr : mailUrlList) {
                Mail mail = this.createMail(this.archiveUrlStr + mailUrlStr);
                if (mail.getDate().getTime() <= preLastMailDate) {
                    continue;
                }
                indexWriter.addDocument(this.createDocument(mail));
                if (mail.getDate().getTime() > lastMailDate) {
                    lastMailDate = mail.getDate().getTime();
                }
                madeCount++;
                if ((madeCount % commitTerm) == 0) {
                    CrawlingProperty.updatePreLastMailDate(lastMailDate);
                    indexWriter.commit();
                    log.info(madeCount + " 件目コミットしました。");
                }
            }
            if (madeCount % commitTerm > 0) {
                CrawlingProperty.updatePreLastMailDate(lastMailDate);
                indexWriter.commit();
            }
            if (lastMailDate > 0L) {
                log.info("最後に解析したメールの送信日時は [" + new Date(lastMailDate) + "] です。");
            }
            log.info("全 " + madeCount + " 件作成しました。(終了)");
        } catch (Exception e) {
            log.error("処理中にエラーが発生しました。処理を中断します。", e);
            if (indexWriter != null) { 
                try {
                    indexWriter.rollback();
                    log.info("処理をロールバックしました。");
                } catch (IOException ioe) {
                    log.error("ロールバックに失敗しました。", ioe);
                }
            }            
        } finally {
            LuceneUtils.closeQuietly(indexWriter);
            log.info("インデックスの作成を終了しました。");
        }
    }

    /**
     * 月ごとのURLからその月の日付型の値を求めます。
     * 
     * @param subUrl 月ごとのメールリストのURL
     * @return その月の日付型
     * @throws ParseException
     */
    private long calcMonthDate(String subUrl)
            throws ParseException {
        String subUrlHead = subUrl.substring(0, subUrl.indexOf("/"));
        Date lastSubUrlDate = new SimpleDateFormat("yyyy-MMMMM", Locale.US).parse(subUrlHead);
        return lastSubUrlDate.getTime();
    }

    /**
     * メールから検索ドキュメントを作成します。
     * 
     * @param mail メール
     * @return 検索ドキュメント
     * @throws ParseException 
     */
    private Document createDocument(Mail mail) throws ParseException {
        Document doc = new Document();
        doc.add(new Field(FieldNames.SUBJECT, mail.getSubject(), Store.YES, Index.ANALYZED));
        doc.add(new Field(FieldNames.FROM, mail.getFromName(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(FieldNames.EMAIL, mail.getFromEmail(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(FieldNames.URL, mail.getMailUrl(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(FieldNames.DATE, Long.toString(mail.getDate().getTime()), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(FieldNames.TEXT, mail.getMailText(), Store.YES, Index.ANALYZED));
        return doc;
    }

    /**
     * メールURLからウェブにアクセスし、HTMLを解析してメールを作成します。
     * メールのウェブページのタグなどのカスタマイズをしていない前提です。
     * 
     * @param mailUrlStr メールURL
     * @return メール
     * @throws SAXException 
     * @throws MalformedURLException
     * @throws IOException
     * @throws TransformerException 
     * @throws DOMException 
     * @throws ParseException
     */
    private Mail createMail(String mailUrlStr) throws SAXException, IOException, TransformerException, DOMException, ParseException {
        DOMParser parser = new DOMParser();
        parser.parse(mailUrlStr);
        Node node = parser.getDocument();
        
        Mail mail = new Mail();
        mail.setMailUrl(mailUrlStr);
        mail.setFromName(XPathAPI.selectSingleNode(node, "//P/B").getTextContent().trim());
        Node a = XPathAPI.selectSingleNode(node, "//P/A");
        mail.setFromEmail(a.getTextContent().trim());
        String subjectSource = a.getAttributes().getNamedItem("title").toString();
        mail.setSubject(subjectSource.substring(
                subjectSource.indexOf("TITLE=\"") + "TITLE=\"".length() + 1,
                subjectSource.lastIndexOf('\"')).trim());
        mail.setDate(DateUtil.convertDefaultToDate(XPathAPI.selectSingleNode(node, "//P/I").getTextContent().trim()));
        mail.setMailText(XPathAPI.selectSingleNode(node, "//PRE").getTextContent());
        return mail;
    }
    
    /**
     * 月ごとのメールリストのURLのリストを作成します。
     * 
     * @return URL文字列のリスト
     * @throws IOException
     * @throws SAXException 
     * @throws TransformerException 
     */
    private List<String> createMonthlyArchiveUrlList() throws SAXException, IOException, TransformerException {
        DOMParser parser = new DOMParser();
        parser.parse(this.archiveUrlStr);
        Node contextNode = parser.getDocument();
        List<String> urlList = new ArrayList<String>(); 
        NodeList nodeList = XPathAPI.selectNodeList(contextNode, "//TABLE/TR/TD/A[4]/@href");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            urlList.add(node.getTextContent());
        }
        return urlList;
    }
    
    /**
     * それぞれのメールのURLのリストを作成します。
     * 
     * @param subUrlList 月ごとのメールリストのURLのリスト<br />
     *                   例)[2011-March/date.html, 2011-February/date.html, 2011-January/date.html]
     * @param preLastMailDate 前回最後にインデクシングしたメールの日付。それより前の月のメールは戻り値に含まれない。
     *                        そのメールの月はインデクシングされたものもすべて含まれる。これは、この時点では個々のメールの日付がわからないためである。 
     * @return 個々のメールのURLのリスト<br />
     *         例)[2011-March/000794.html, 2011-February/000757.html, 2011-January/000736.html, 2010-December/000681.html]
     * @throws MalformedURLException
     * @throws IOException
     * @throws UlTagNotExistsException
     * @throws ParseException 
     */
    private List<String> createMailUrlList(List<String> subUrlList, long preLastMailDate)
            throws MalformedURLException, IOException, UlTagNotExistsException, ParseException {
        List<String> mailUrlList = new ArrayList<String>();
        
        for (String subUrlStr : subUrlList) {
            long subUrlDate = this.calcMonthDate(subUrlStr);
            if (!isValidDate(subUrlDate, preLastMailDate)) {
                continue;
            }
            BufferedReader br = this.createUrlReader(this.archiveUrlStr + subUrlStr);
            StringBuffer allDocumentSB = new StringBuffer();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                allDocumentSB.append(line);
            }
            String ulDoc = this.extractUlDoc(allDocumentSB.toString());
            Matcher matcher = Pattern.compile("<LI>\\D*([0-9]+)(.html)\">").matcher(ulDoc);
            
            while (matcher.find()) {
                String mailUrl = matcher.group(1) + matcher.group(2);
                
                // 月ごとのURLのdate.htmlの部分をメール番号のhtml名に変換
                // 例) "2011-January/date.html" → "2011-January/000000.html"
                mailUrlList.add(subUrlStr.replaceFirst("date.html", mailUrl));  
            }
        }
        return mailUrlList;
    }

    /**
     * subUrlがメール解析の対象かを判断します。
     * 
     * @param subUrlDate subUrlの日付値
     * @param preLastMailDate 前回解析したメールの最後の日付値
     * @return 対象とするなら true
     */
    private boolean isValidDate(long subUrlDate, long preLastMailDate) {
        if (subUrlDate > preLastMailDate) {
            return true;
        }
        if (DateUtil.isSameYearMonth(preLastMailDate, subUrlDate)) {
            return true;
        }
        return false;
    }

    /**
     * HTMLドキュメントから必要なULタグ部分を抽出します。
     * 
     * @param dateHtmlDoc HTMLドキュメント
     * @return ULタグ部分の文字列
     * @throws UlTagNotExistsException
     */
    protected String extractUlDoc(String dateHtmlDoc) throws UlTagNotExistsException {
        Matcher ulMatcher = Pattern.compile("<b>記事数:</b>.*?<ul>.*?</ul>").matcher(dateHtmlDoc);
        if (!ulMatcher.find()) {
            throw new UlTagNotExistsException();
        }
        return ulMatcher.group();
    }

    /**
     * URLにアクセスして読み取るバッファリーダーを作成します。
     * 
     * @param urlStr アクセスするURL文字列
     * @return バッファリーダー
     * @throws MalformedURLException
     * @throws IOException
     */
    private BufferedReader createUrlReader(String urlStr)
            throws MalformedURLException, IOException {
        URL monthlyArchiveUrl = new URL(urlStr);
        URLConnection connection = monthlyArchiveUrl.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return br;
    }

}
