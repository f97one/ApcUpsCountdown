package net.formula97.android.apcupscountdown;

import android.content.Context;
import android.util.Log;

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

    /**
     * 開始終了時刻の差を文字列で返す。
     * @param from Calendar型、開始時刻
     * @param to Calendar型、終了時刻
     * @return String型、開始終了時刻の差
     */
    public String getDeltas(Calendar from, Calendar to) {
        // 日付フォーマット
        SimpleDateFormat format = new SimpleDateFormat("yyyy/M/d/H/m");
        Calendar deltas = Calendar.getInstance();

        long hourInMillis = 3600000;
        long dayInMillis = hourInMillis * 24;
        long monthInMillis = dayInMillis * 30;
        long yearInMillis = dayInMillis * 365;

        // 差をミリ秒で取得
        long deltasInMillis = to.getTimeInMillis() - from.getTimeInMillis();
        Log.d("getDeltas", "deltas in ms = " + String.valueOf(deltasInMillis));
        deltas.setTimeInMillis(deltasInMillis - deltas.getTimeZone().getRawOffset());

        // 返す文字列を組み立てる
        String[] s = format.format(deltas.getTime()).split("/");
        StringBuilder builder = new StringBuilder();

        if (deltasInMillis >= yearInMillis) {   // 年
            builder.append(String.valueOf(Integer.parseInt(s[0]) - 1970) + ctx.getString(R.string.years) + " ");
        }
        if (deltasInMillis >= monthInMillis) {  // 月
            builder.append(s[1] + ctx.getString(R.string.months) + " ");
        }
        if (deltasInMillis >= dayInMillis) {    // 日
            builder.append(String.valueOf(Integer.parseInt(s[2]) - 1) + ctx.getString(R.string.days) + " ");
        }
        if (deltasInMillis >= hourInMillis) {  // 時間
            builder.append(s[3] + ctx.getString(R.string.hour) + " ");
        }
        if (Integer.parseInt(s[4]) != 0) {
            builder.append(s[4] + ctx.getString(R.string.minute));
        }

        return String.valueOf(builder);
    }

    /**
     * 現在の残りシャットダウン時間（単位:秒）から、想定起動時刻を返す。
     * @param remainsInSec int型、現在の残りシャットダウン時間（単位:秒）
     * @return Calendar型、想定起動時刻
     */
    public Calendar getEstimated(int remainsInSec) {
        Calendar estimated = Calendar.getInstance();
        estimated.add(Calendar.SECOND, remainsInSec);

        return estimated;
    }
}
