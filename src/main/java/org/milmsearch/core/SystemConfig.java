/**************************************************************
  Source	: SystemConfig.java
  Date		: 2011/03/31 21:58:49
**************************************************************/
package org.milmsearch.core;

import java.util.Properties;

/**
 * システムコンフィグクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class SystemConfig {

    /** コンフィグファイル名 */
    private final String fileName = "system_config.properties";
    
    /** プロパティ */
    private static final Properties PROPERTIES = new Properties();
    
    /** 唯一のインスタンス */
    public final static SystemConfig OWN = new SystemConfig();
    
    /**
     * コンストラクタ
     */
    private SystemConfig() {
        try {
            PROPERTIES.load(SystemConfig.class.getClassLoader().getResourceAsStream(this.fileName));
        } catch (Exception e) {
            throw new MilmSearchRuntimeException("System Config Construction Error.", e);
        }
    }

    /**
     * 検索インデックスの保存ディレクトリのパスを取得します。
     * 
     * @return 検索インデックスの保存ディレクトリのパス
     */
    public static String getIndexDir() {
        return PROPERTIES.getProperty("index.dir");
    }
    
    /**
     * 保存書庫のURL文字列を取得します。
     * 
     * @return 保存書庫のURL文字列
     */
    public static String getArchiveUrl() {
        return PROPERTIES.getProperty("archive.url");
    }    
    
}
