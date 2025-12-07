package net.fina.server.dashboard;


import net.fina.server.dashboard.legacy.proxy.LegacyDateCalculationsUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class LegacyDateCalculationsUtilTest {
    @Test
    public void getFirstDayOfPeriodFromStringTestMonth() {
        String periodString = "01-2019";
        Date result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "M");

        Assert.assertNotNull(result);

        Calendar c = Calendar.getInstance();
        c.setTime(result);

        Assert.assertEquals(2019, c.get(Calendar.YEAR));
        Assert.assertEquals(c.get(Calendar.MONTH), Calendar.JANUARY);
        Assert.assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(c.getActualMinimum(Calendar.HOUR), c.get(Calendar.HOUR));
        Assert.assertEquals(c.getActualMinimum(Calendar.MINUTE), c.get(Calendar.MINUTE));
        Assert.assertEquals(c.getActualMinimum(Calendar.SECOND), c.get(Calendar.SECOND));
    }

    @Test
    public void getFirstDayOfPeriodFromStringTestQuarter() {
        String periodString = "Q2 2019";
        Date result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "Q");

        Assert.assertNotNull(result);

        Calendar c = Calendar.getInstance();
        c.setTime(result);

        Assert.assertEquals(2019, c.get(Calendar.YEAR));
        Assert.assertEquals(c.get(Calendar.MONTH), Calendar.JUNE);
        Assert.assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(c.getActualMinimum(Calendar.HOUR), c.get(Calendar.HOUR));
        Assert.assertEquals(c.getActualMinimum(Calendar.MINUTE), c.get(Calendar.MINUTE));
        Assert.assertEquals(c.getActualMinimum(Calendar.SECOND), c.get(Calendar.SECOND));
    }

    @Test
    public void getFirstDayOfPeriodFromStringTestInvalid() {
        String periodString = "Q6 2019";
        Date result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "Q");
        Assert.assertNull(result);

        periodString = "13-2019";
        result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "M");
        Assert.assertNull(result);


    }

    @Test
    public void getFirstDayOfPeriodFromStringTestMalformed() {
        String periodString = "Q1-2019";
        Date result;

        try {
            result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "Q");
            Assert.assertNotNull(null);
        } catch (Exception e) {
        }

        periodString = "10 2019";
        try {
            result = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, "M");
            Assert.assertNotNull(null);
        } catch (Exception e) {
        }
    }
}
