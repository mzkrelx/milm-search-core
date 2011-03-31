/**************************************************************
  Source	: CralingTimerTask.java
  Date		: 2011/03/07 17:35:11
**************************************************************/
package info.one.ideal.milm.search.crawling;

import info.one.ideal.milm.search.common.html.HtmlParser;
import info.one.ideal.milm.search.common.html.Tag;
import info.one.ideal.milm.search.common.util.DateUtil;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 *
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingTimerTask extends TimerTask {

    /** ロガー */
    private static Log log = LogFactory.getLog(CrawlingTimerTask.class);

    public String archiveUrlStr = "http://sourceforge.jp/projects/setucocms/lists/archive/public/";
    
    public String indexPath = "/home/charles/temp/milm-search";
    
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
            List<String> subUrlList = this.createMonthlyArchiveUrlList();
            long preLastMailDate = this.findPreLastMailDate();
            if (preLastMailDate == 0L) {
                log.warn("最後に解析したメール情報が取得できませんでした。全てのメール情報を解析します。");
                indexWriter = new IndexWriter(
                        FSDirectory.open(new File(this.indexPath)),
                        new CJKAnalyzer(Version.LUCENE_29),
                        true,    // trueなら空の状態から作り、falseなら追加する(既に追加されたものでも新たに増える)。
                        MaxFieldLength.UNLIMITED);
            } else {
                log.info("最後に解析したメール情報を取得しました。途中から解析します。");
                log.info("前回の最後に解析したメールの送信日時は " + DateUtil.convertDate2Str(new Date(preLastMailDate)));
                indexWriter = new IndexWriter(
                        FSDirectory.open(new File(this.indexPath)),
                        new CJKAnalyzer(Version.LUCENE_29),
                        false,    // trueなら空の状態から作り、falseなら追加する(既に追加されたものでも新たに増える)。
                        MaxFieldLength.UNLIMITED);
            }
            List<String> mailUrlList = this.createMailUrlList(subUrlList, preLastMailDate);
            
            int madeCount = 0;
            int logTerm = 100;
            long lastMailDate = 0L;
            for (String mailUrlStr : mailUrlList) {
                Mail mail = this.createMail(mailUrlStr);
                if (mail.getDate().getTime() <= preLastMailDate) {
                    continue;
                }
                indexWriter.addDocument(this.createDocument(mail));
                if (mail.getDate().getTime() > lastMailDate) {
                    lastMailDate = mail.getDate().getTime();
                }
                madeCount++;
                if ((madeCount % 100) == 0) {
                    indexWriter.optimize();
                    indexWriter.commit();
                }

                if ((madeCount % logTerm) == 0) {
                    log.info(madeCount + " 件目作成しました。");
                }
            }
            if (madeCount > 0) {
                Document doc = new Document();
                doc.add(new Field("lastMailDate", Long.toString(lastMailDate), Store.YES, Index.NOT_ANALYZED));
                indexWriter.addDocument(doc);
                log.debug("最後に解析したメール情報を保存しました。");
                log.info("保存した最新のメールの送信日時[" + DateUtil.convertDate2Str(new Date(lastMailDate)) + "]");
    
                indexWriter.optimize();
                indexWriter.commit();
            }
            log.info(madeCount + " 件目作成しました。(終了)");
        } catch (Exception e) {
            if (indexWriter != null) { 
                try {
                    indexWriter.rollback();
                } catch (IOException ignore) {
                }
            }
            log.error("処理中にエラーが発生しました。処理を中断します。", e);
        } finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (Exception ignore) {
                }
            }
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
     * 前回最後にインデクシングしたメールの送信日時を取得します。
     * 見つからない場合は 0L が返ります。
     * 
     * @return 最後にインデクシングしたメールの送信日時
     * @throws CorruptIndexException
     * @throws IOException
     * @throws StaleReaderException
     * @throws LockObtainFailedException
     */
    private long findPreLastMailDate() throws CorruptIndexException, IOException,
            StaleReaderException, LockObtainFailedException {
        long lastMailDate = 0L;
        IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(this.indexPath)), false);
        for(int i = 0; i < indexReader.maxDoc(); i++){
            Document doc = indexReader.document(i);
            if (doc.get("lastMailDate") == null) {
                continue;
            }
            lastMailDate = Long.parseLong(doc.get("lastMailDate"));
            indexReader.deleteDocument(i);
            break;
        }
        indexReader.close();
        return lastMailDate;
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
        doc.add(new Field("subject", mail.getSubject(), Store.YES, Index.ANALYZED));
        doc.add(new Field("from", mail.getFromName(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("email", mail.getFromEmail(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("url", mail.getMailUrl(), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("date", Long.toString(mail.getDate().getTime()), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("text", mail.getMailText(), Store.YES, Index.ANALYZED));
        return doc;
    }

    /**
     * メールURLからウェブにアクセスし、HTMLを解析してメールを作成します。
     * 
     * @param mailUrlStr メールURL
     * @return メール
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParseException
     */
    private Mail createMail(String mailUrlStr) throws MalformedURLException,
            IOException, ParseException {
        BufferedReader br = this.createUrlReader(this.archiveUrlStr + mailUrlStr);
        StringBuffer sb = new StringBuffer();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line + "\n");
        }
        
        HtmlParser htmlParser = new HtmlParser(sb.toString());
        List<Tag> tagList = new ArrayList<Tag>();
        while (htmlParser.hasNext()) {
            Tag tag = htmlParser.next();
            tagList.add(tag);
        }
        
        Set<Tag> mailInfoTagSet = this.chooseMailInfoTags(tagList);

        Mail mail = new Mail();
        for (Tag tag : mailInfoTagSet) {
            mail.setMailUrl(this.archiveUrlStr + mailUrlStr);
            if ("B".equals(tag.getTagName())) {
                mail.setFromName(tag.getInnerHtml());
            }
            if ("A".equals(tag.getTagName())) {
                String subjectSource = tag.getTagAttribute();
                mail.setSubject(subjectSource.substring(
                        subjectSource.indexOf("TITLE=\"") + "TITLE=\"".length(),
                        subjectSource.lastIndexOf('\"')));
                mail.setFromEmail(tag.getInnerHtml());
            }
            if ("I".equals(tag.getTagName())) {
                mail.setDate(DateUtil.convertDefaultToDate(tag.getInnerHtml()));
            }
            if ("PRE".equals(tag.getTagName())) {
                mail.setMailText(tag.getInnerHtml());
            }
        }
        return mail;
    }

    /**
     * タグリストから、メール情報に関するものだけを選び抜きます。
     * 
     * @param tagList タグリスト
     * @return メール情報のセット
     */
    private Set<Tag> chooseMailInfoTags(List<Tag> tagList) {
        boolean isBNext = false;
        boolean isANext = false;
        boolean isBAIEnd = false;
        boolean isBiginCommentNext = false;
        Map<String, Tag> mailInfoTagMap = new HashMap<String, Tag>();
        for (Tag tag : tagList) {
            // 閉じタグ,<BR>は無視する
            if (tag.getTagName().startsWith("/") || "BR".equals(tag.getTagName())) {
                continue;
            }
            
            // B A I のタグが続いていたら差出人、タイトル、送信日時とみなして取得する
            if (isBAIEnd == false && isANext == true && "I".equals(tag.getTagName())) {
                mailInfoTagMap.put("I", tag);
                isBAIEnd = true;
                continue;
            }
            if (isBAIEnd == false && isBNext == true && "A".equals(tag.getTagName())) {
                isANext = true;
                mailInfoTagMap.put("A", tag);
                continue;
            }
            if (isBAIEnd == false && "B".equals(tag.getTagName())) {
                isBNext = true;
                mailInfoTagMap.put("B", tag);
                continue;
            }
            
            // 本文のPREタグは "<!--beginaarticle-->" のコメントの次。
            if (isBiginCommentNext == true) {
                mailInfoTagMap.put("PRE", tag);
                break;
            }
            if (tag.getTagName().startsWith("!--beginarticle")) {   // !--の次にスペースがないのでコメント内容までTagNameの方に入っている
                isBiginCommentNext = true;
                continue;
            }
            
            isBNext = false;
            isANext = false;
        }
        return new HashSet<Tag>(mailInfoTagMap.values());
    }

    /**
     * 月ごとのメールリストのURLのリストを作成します。
     * 
     * @return URL文字列のリスト
     * @throws MalformedURLException
     * @throws IOException
     */
    private List<String> createMonthlyArchiveUrlList()
            throws MalformedURLException, IOException {
        BufferedReader br = this.createUrlReader(this.archiveUrlStr);
        List<String> subUrlList = new ArrayList<String>(); 
        
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            Matcher matcher = Pattern.compile("<A href=\".*?date.html").matcher(line);
            if (matcher.find()) {
                String subUrl = matcher.group().substring(9);
                subUrlList.add(subUrl);
            }
        }
        return subUrlList;
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
            if (!DateUtil.isSameYearMonth(preLastMailDate, subUrlDate) || preLastMailDate > subUrlDate) {
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
