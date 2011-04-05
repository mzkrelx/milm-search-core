/**************************************************************
  Source  : SearchService.java
  Date    : 2008/12/23 23:59:56
**************************************************************/
package info.one.ideal.milm.search;

import info.one.ideal.milm.search.crawling.Mail;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 検索サービスクラスです。
 * 
 * @author Mizuki Yamanaka
 */
public class SearchService {
    
	/** ログ */
	private final Log log = LogFactory.getLog(SearchService.class);
	
	/** 解析ロジック */
	protected Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_29);
	
	/**
     * コンストラクタ
     */
    public SearchService() {
    }
	
	/**
	 * 検索をしてメールリストを取得します。
	 * 
	 * @param fieldName 検索フィールド
	 * @param queryStr 検索文字列
	 * @param sortingStr 並べ替え識別文字列
	 * @return 検索結果
	 * @throws MilmSearchException
	 */
    public List<Mail> search(String fieldName, String queryStr, int itemCountPerPage, int pageNumber, SortValue sortValue) 
            throws MilmSearchException {
		List<Mail> mailList = new ArrayList<Mail>();
		IndexSearcher searcher = null;
    	try {
    	    searcher = new IndexSearcher(FSDirectory.open(new File(SystemConfig.getIndexDir())), true);
            Query query = new QueryParser(Version.LUCENE_29, fieldName, this.analyzer).parse(queryStr);
            Sort sort = this.createSort(sortValue);
            TopDocs topDocs;
            if (sort == null) {
                topDocs = searcher.search(query, null, itemCountPerPage * pageNumber);
            } else {
                topDocs = searcher.search(query, null, itemCountPerPage * pageNumber, sort);
            }
            // ◯件取得したうちの、最後のページ部分のみメールリストに含めるための最初の位置。
            int beginIndex = (pageNumber - 1) * itemCountPerPage;
            int i = 0;
            for(ScoreDoc scoreDoc: topDocs.scoreDocs) { // ScoreDoc は Doc へのポインタ
                if (i++ < beginIndex) {
                    continue;
                }
                Document doc = searcher.doc(scoreDoc.doc);
                Mail mail = this.createMail(doc, fieldName, queryStr);
	    		mailList.add(mail);
	    	}
    	} catch (ParseException pe) {
    	    log.error("検索キーワードのパースに失敗しました。", pe);
    	    throw new MilmSearchException("検索キーワードが無効です。\n" + pe.getMessage(), pe);
    	} catch (Exception e) {
    	    log.error("検索中に例外が発生しました。", e);
    		throw new MilmSearchException(e.getMessage(), e);
    	} finally {
    	    // TODO LuceneUtil.closeQuietly(Searchable) みたいなのつくる 
    	    if (searcher != null) {
    	        try {
                    searcher.close();
                } catch (IOException ignore) {
                }
    	    }
    	}
    	return mailList;
    }
    
    /**
     * 検索結果が全部で何件かを取得します。
     * 
     * @return 検索結果の件数
     * @throws MilmSearchException 
     */
    public int countTotal(String fieldName, String queryStr) throws MilmSearchException {
        try {
            IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(SystemConfig.getIndexDir())), true);
            Query query = new QueryParser(Version.LUCENE_29, fieldName, this.analyzer).parse(queryStr);
            TopDocs topDocs = searcher.search(query, null, 1);
            return topDocs.totalHits;
        } catch (ParseException pe) {
            throw new MilmSearchException("検索キーワードが無効です。\n" + pe.getMessage(), pe);
        } catch (Exception e) {
            throw new MilmSearchException(e.getMessage(), e);
        }
    }
    
    /**
     * 並び順を作成します。
     * 
     * @param sortValue 並び替えの種類
     * @return 並び順。並び替えの種類がデフォルトの場合は null
     */
    protected Sort createSort(SortValue sortValue) {
        if (sortValue == null) {
            sortValue = SortValue.DEFAULT;
        }
        switch (sortValue) {
            case DATE:
                return new Sort(new SortField("date", SortField.LONG));
            case DATE_R:
                return new Sort(new SortField("date", SortField.LONG, true));
            case FROM:
                return new Sort(new SortField("from", SortField.STRING_VAL));
            case FROM_R:
                return new Sort(new SortField("from", SortField.STRING_VAL, true));
            default:
                return null;
        }
    }
    
    /**
     * 文字列をハイライトします。
     * fieldTextに検索の該当箇所がないと null を返します。
     * 
     * @param fieldName 検索フィールド
     * @param queryStr 検索クエリ
     * @param fieldText ハイライトする文字列
     * @throws MilmSearchException 
     */
    public String highlight(String fieldName, String queryStr, String fieldText) throws MilmSearchException {
        try {
            Formatter formatter = new SimpleHTMLFormatter(Highlight.preTag, Highlight.postTag);
            QueryScorer score = new QueryScorer(new QueryParser(Version.LUCENE_29, fieldName, this.analyzer).parse(queryStr), fieldName);
            Highlighter highlighter = new Highlighter(formatter, score);
            String highlightString = highlighter.getBestFragment(this.analyzer, fieldName, fieldText);
            return highlightString;
        } catch (Exception e) {
            throw new MilmSearchException("ハイライト処理時に例外が発生しました。", e);
        }
    }
	
	/**
	 * 検索ドキュメントからメールを作成します。
	 * 
	 * @param doc 検索ドキュメント
	 * @return メール 
	 * @throws MilmSearchException 
	 */
	protected Mail createMail(Document doc, String fieldName, String queryStr) throws MilmSearchException {
		Mail mail = new Mail();
		// TODO subject とかを定数化
		mail.setSubject(doc.get("subject"));
		mail.setFromName(doc.get("from"));
		mail.setFromEmail(doc.get("email"));
        mail.setMailUrl(doc.get("url"));
	    mail.setDate(new Date(Long.parseLong(doc.get("date"))));
		mail.setMailText(doc.get("text"));
		// TODO 検索対象のENUMを作る
		if ("text".equals(fieldName)) {
            /* 本文をハイライトしてsummaryにセット */
            String text = mail.getMailText();
            text = this.highlight(fieldName, queryStr, text);
            mail.setMailSummary(text);
        } else if ("subject".equals(fieldName)) {
            /* 件名をハイライト */
            String subject = mail.getSubject();
            subject = this.highlight(fieldName, queryStr, subject);
            mail.setSubject(subject);
            /* 本文を短くしてsummaryにセット */
            String text = mail.getMailText();
            text = this.scrapeMailText(text);
            mail.setMailText(text);
        }
		return mail;
	}
	
	/**
     * メール本文を短く削ります。
     * 
     * @param text メール本文
     * @return 短く削ったメール本文
     */
    private String scrapeMailText(String text) {
        StringReader sr = new StringReader(text);
        StringBuffer sb = new StringBuffer();
        int lineCount = 0;
        try {
            for (int character = sr.read(); character != -1; character = sr.read()) {
                if (lineCount >= 10) {  // 10行文のデータを表示
                    sb.append("...");   // 最後には...を表示
                    break;
                }
                char ch = (char)character;
                if (ch == '\n' || ch == '\r') {
                    lineCount++;
                    continue;           // 改行は表示しない
                }
                sb.append(ch);
            }
        } catch (IOException e) {
            log.error("メール本文のを削る処理に失敗しました。処理を中断し、全文を表示します。\n" +
                    "エラーになったメール本文:\n" + text, e);
            sr.close();
            return text;
        }
        sr.close();
        return sb.toString();
    }

}
