/**************************************************************
  Source	: IOUtil.java
  Date		: 2011/04/18 16:29:19
**************************************************************/
package org.milmsearch.core.common.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 入出力に関するユーティリティです。
 *
 * @author Mizuki Yamanaka
 */
public class IOUtil {

    /**
     * 静かにクローズします。
     * 
     * @param is 入力ストリーム
     */
    public static void closeQuietly(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException ignore) {
        }
    }
}
