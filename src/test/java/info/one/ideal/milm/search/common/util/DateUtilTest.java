/**************************************************************
  Source	: DateUtilsTest.java
  Date		: 2011/03/31 13:53:25
**************************************************************/
package info.one.ideal.milm.search.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.one.ideal.milm.search.common.util.DateUtil;

import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 * @author Mizuki Yamanaka
 */
public class DateUtilTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonth() {
        Date date1 = new Date(2011, 1, 1);
        Date date2 = new Date(2011, 1, 1);
        assertTrue(DateUtil.isSameYearMonth(date1, date2));
    }

    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonthDifferentDay() {
        Date date1 = new Date(2011, 1, 1);
        Date date2 = new Date(2011, 1, 15);
        assertTrue(DateUtil.isSameYearMonth(date1, date2));
    }

    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonthDifferentYear() {
        Date date1 = new Date(2010, 1, 1);
        Date date2 = new Date(2011, 1, 1);
        assertFalse(DateUtil.isSameYearMonth(date1, date2));
    }
    
    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonthLong() {
        Date date1 = new Date(2011, 1, 1);
        Date date2 = new Date(2011, 1, 1);
        assertTrue(DateUtil.isSameYearMonth(date1.getTime(), date2.getTime()));
    }

    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonthLongDifferentDay() {
        Date date1 = new Date(2011, 1, 1);
        Date date2 = new Date(2011, 1, 15);
        assertTrue(DateUtil.isSameYearMonth(date1.getTime(), date2.getTime()));
    }

    /**
     * {@link info.one.ideal.milm.search.common.util.DateUtil#isSameYearMonth(java.util.Date, java.util.Date)} のためのテスト・メソッド。
     */
    @Test
    public void testIsSameYearMonthLongDifferentYear() {
        Date date1 = new Date(2010, 1, 1);
        Date date2 = new Date(2011, 1, 1);
        assertFalse(DateUtil.isSameYearMonth(date1.getTime(), date2.getTime()));
    }    

}
