/**************************************************************
  Source	: LuceneUtils.java
  Date		: 2011/04/05 21:43:11
**************************************************************/
package info.one.ideal.milm.search.lucene;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Searchable;

/**
 *
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
}
