package net.formula97.android.apcupscountdown;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by HAJIME on 14/02/09.
 */
public class DateDeltas {
    Context ctx;

    public DateDeltas(Context context) {
        this.ctx = context;
    }

    public String getDeltas(Calendar from, Calendar to) {
        // 日付フォーマット
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-kk-mm");
        Calendar deltas = Calendar.getInstance();

        long dayInMillis = 86400000;
        long monthInMillis = dayInMillis * 30;
        long yearInMillis = dayInMillis * 365;
        long hourInMillis = 3600000;

        // 差をミリ秒で取得
        long deltasInMillis = to.getTimeInMillis() - from.getTimeInMillis() - deltas.getTimeZone().getRawOffset();
        deltas.setTimeInMillis(deltasInMillis);

        // 返す文字列を組み立てる
        String[] s = format.format(deltas.getTime()).split("-");
        StringBuilder builder = new StringBuilder();

        if (deltasInMillis >= yearInMillis) {   // 年
            builder.append(String.valueOf(Integer.parseInt(s[0]) - 1970) + ctx.getString(R.string.years) + " ");
        }
        if (deltasInMillis >= monthInMillis) {  // 月
            builder.append(s[1] + ctx.getString(R.string.months) + " ");
        }
        if (deltasInMillis >= dayInMillis) {    // 日
            builder.append(s[2] + ctx.getString(R.string.days) + " ");
        }
        if (deltasInMillis >= hourInMillis) {  // 時間
            builder.append(s[3] + ctx.getString(R.string.hour) + " ");
        }
        if (Integer.parseInt(s[4]) != 0) {
            builder.append(s[4] + ctx.getString(R.string.minute));
        }

        return String.valueOf(builder);
    }
}
