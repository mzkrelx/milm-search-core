/**************************************************************
  Source	: LuceneUtils.java
  Date		: 2011/04/05 21:43:11
**************************************************************/
package org.milmsearch.core.lucene;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Searchable;

/**
 * Luceneパッケージ関連のユーティリティクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class LuceneUtils {

    /** ログ */
    private final Log log = LogFactory.getLog(LuceneUtils.class);

    /**
     * Searchable をクローズします。
     * 
     * @param closer クローズするもの
     */
    public static void closeQuietly(Searchable closer) {
        if (closer != null) {
            try {
                closer.close();
            } catch (IOException e) {
                new LuceneUtils().log.error("Searchableのクローズに失敗しました。", e);
            }
        }
    }

    /**
     * IndexWriter をクローズします。
     * 
     * @param closer クローズするもの
     */
    public static void closeQuietly(IndexWriter closer) {
        if (closer != null) {
            try {
                closer.close();
            } catch (IOException e) {
                new LuceneUtils().log.error("IndexWriterのクローズに失敗しました。", e);
            }
        }
    }
    
    /**
     * IndexReader をクローズします。
     * 
     * @param closer クローズするもの
     */
    public static void closeQuietly(IndexReader closer) {
        if (closer != null) {
            try {
                closer.close();
            } catch (IOException e) {
                new LuceneUtils().log.error("IndexReaderのクローズに失敗しました。", e);
            }
        }
    }    
}
