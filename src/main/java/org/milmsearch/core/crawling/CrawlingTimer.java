/**************************************************************
  Source	: ArchiveCrawler.java
  Date		: 2011/03/07 11:17:08
**************************************************************/
package org.milmsearch.core.crawling;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * クローリングするタイミングを管理するクラスです。
 *
 * @author Mizuki Yamanaka
 */
public class CrawlingTimer implements ServletContextListener {
    
    /** タイマー */
    private static final Timer TIMER = new Timer();
    
    /** クローリングする間隔時間 */
    private final int hour = 24;

    /* (非 Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        TIMER.cancel();
    }

    /* (非 Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        TIMER.schedule(new CrawlingTimerTask(),
                0,   // 初回の起動時間
                hour * 60 * 60 * 1000);  // 固定間隔時間
    }
}
