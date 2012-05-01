/**************************************************************
  Source	: CrawlingProperty.java
  Date		: 2011/04/18 17:00:47
**************************************************************/
package info.one.ideal.milm.search.crawling;

import info.one.ideal.milm.search.SystemConfig;
import info.one.ideal.milm.search.common.util.IOUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * クローリングのためのプロパティデータを管理するクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingProperty {
    
    /** プロパティファイル名 */
    private static String FILE_NAME = "crawling.properties";
    
    /** 最後にインデクシングしたメールの送信時刻のキー */
    private static final String LAST_MAIL_DATE_KEY = "last.mail.date"; 
    
    /**
     * 前回最後にインデクシングしたメールの送信日時を取得します。
     * 見つからない場合は 0L が返ります。
     * 
     * @return 最後にインデクシングしたメールの送信日時
     * @throws IOException
     */
    public static long findPreLastMailDate() throws IOException {
        Properties properties = new Properties();
        InputStream is = new FileInputStream(CrawlingProperty.getFileName());
        try {
            properties.load(is);
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtil.closeQuietly(is);
        }
        String lastMailDate = properties.getProperty(CrawlingProperty.LAST_MAIL_DATE_KEY);
        if (lastMailDate == null) {
            return 0L;
        }
        return Long.parseLong(lastMailDate);
    }

    /**
     * 前回最後にインデクシングしたメールの送信日時を更新します。
     * 
     * @throws IOException
     */
    public static void updatePreLastMailDate(long newDate) throws IOException {
        Properties properties = new Properties();
        InputStream is = new FileInputStream(CrawlingProperty.getFileName());
        try {
            properties.load(is);
            properties.setProperty(CrawlingProperty.LAST_MAIL_DATE_KEY, String.valueOf(newDate));
            properties.store(new FileOutputStream(CrawlingProperty.getFileName()),
                    "This file is updated automatically by milm-search. Don't touch by hand.");
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtil.closeQuietly(is);
        }
    }
    
    /**
     * プロパティファイル名を取得します。
     * 
     * @return ファイル名
     */
    private static String getFileName() {
        return SystemConfig.getIndexDir()
                + System.getProperty("file.separator")
                + CrawlingProperty.FILE_NAME;
    }
}
