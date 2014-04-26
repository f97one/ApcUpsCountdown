package net.formula97.android.apcupscountdown.test;

import android.content.Context;

import android.test.InstrumentationTestCase;

import net.formula97.android.apcupscountdown.DateDeltas;

import java.util.Calendar;

public class DateDeltasTest extends InstrumentationTestCase {

    private Context mContext;

    public void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext().getApplicationContext();
    }

    public void tearDown() throws Exception {

    }

    public void testGetDeltas() throws Exception {
        Calendar from = Calendar.getInstance();
        Calendar to =(Calendar)from.clone();
        to.add(Calendar.DAY_OF_MONTH, 1);
        to.add(Calendar.MINUTE, 1);

        DateDeltas deltas = new DateDeltas(mContext);
        String actual = deltas.getDeltas(from, to);

        String expected = "1day(s) 0hour 1min.";

        assertEquals("1日と1分後は「1day(s) 0hour 1min.」と表示される", expected, actual);
    }

    public void testGetEstimated() throws Exception {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, 1);

        DateDeltas deltas = new DateDeltas(mContext);

        assertEquals("3600秒後は1時間後と予測される", deltas.getEstimated(3600), now);
    }
}