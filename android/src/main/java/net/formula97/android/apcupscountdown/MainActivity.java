package net.formula97.android.apcupscountdown;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // ViewIdの取得はPlaceholderFlagment#onCreateView()で行う。
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        TextView et_ShutdownStartDate;
        TextView et_ShutdownStartTime;
        TextView et_WakeUpDate;
        TextView et_WakeUpTime;
        TextView tv_shutdownStartAt;
        TextView tv_shutdownPeriod;
        TextView tv_shutdownPeriodInSec;

        DatePickerDialog dpDialog;
        TimePickerDialog tpDialog;

        DatePickerDialog.OnDateSetListener dpListenerStart;
        DatePickerDialog.OnDateSetListener dpListenerEnd;
        TimePickerDialog.OnTimeSetListener tpListenerStart;
        TimePickerDialog.OnTimeSetListener tpListenerEnd;

        Calendar start;
        Calendar end;

        public PlaceholderFragment() {
            // 時刻を初期化する
            start = Calendar.getInstance();
            end = Calendar.getInstance();
            end.add(Calendar.MINUTE, 10);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // ここでウィジェット類のfindViewByIdを呼ぶ
            et_ShutdownStartDate = (TextView) rootView.findViewById(R.id.et_ShutdownStartDate);
            et_ShutdownStartTime = (TextView) rootView.findViewById(R.id.et_ShutdownStartTime);
            et_WakeUpDate = (TextView) rootView.findViewById(R.id.et_WakeUpDate);
            et_WakeUpTime = (TextView) rootView.findViewById(R.id.et_WakeUpTime);
            tv_shutdownStartAt = (TextView) rootView.findViewById(R.id.tv_shutdownStartAt);
            tv_shutdownPeriod = (TextView) rootView.findViewById(R.id.tv_shutdownPeriod);
            tv_shutdownPeriodInSec = (TextView) rootView.findViewById(R.id.tv_shutdownPeriodInSec);

            return rootView;
        }

        /**
         * Called when the fragment is visible to the user and actively running.
         * This is generally
         * tied to {@link android.app.Activity#onResume() Activity.onResume} of the containing
         * Activity's lifecycle.
         */
        @Override
        public void onResume() {
            super.onResume();

            // ウィジェットの表示幅を調整する
            et_ShutdownStartTime.setWidth(getWidgetWidth(3));
            et_ShutdownStartDate.setWidth(getWidgetWidth(3));
            et_WakeUpTime.setWidth(getWidgetWidth(3));
            et_WakeUpDate.setWidth(getWidgetWidth(3));

            // 現在時刻＋２分をセット
            start.add(Calendar.MINUTE, 2);
            cloneToEnd();

            et_ShutdownStartDate.setText(buildDateFormat(start));
            et_ShutdownStartTime.setText(buildTimeFormat(start));
            et_WakeUpDate.setText(buildDateFormat(end));
            et_WakeUpTime.setText(buildTimeFormat(end));

            setResultTime();

            // クリックリスナーをセット
            et_ShutdownStartDate.setOnClickListener(this);
            et_ShutdownStartTime.setOnClickListener(this);
            et_WakeUpDate.setOnClickListener(this);
            et_WakeUpTime.setOnClickListener(this);

            // DatePickerListenerをセット
            dpListenerStart = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.d("OnDateSetListener", "entered OnDateSetListener.");
                    start.set(year, monthOfYear, dayOfMonth);
                    et_ShutdownStartDate.setText(buildDateFormat(start));
                    Log.d("OnDateSetListener", "accepted date : " + buildDateFormat(start) + " " + buildTimeFormat(start));

                    cloneToEnd();
                    et_WakeUpDate.setText(buildDateFormat(end));
                    setResultTime();
                }
            };
            dpListenerEnd = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.d("OnDateSetListener", "entered OnDateSetListener.");
                    end.set(year, monthOfYear, dayOfMonth);
                    et_WakeUpDate.setText(buildDateFormat(end));
                    Log.d("OnDateSetListener", "accepted date : " + buildDateFormat(end) + " " + buildTimeFormat(end));
                    setResultTime();
                }
            };

            // TimePickerListenerをセット
            tpListenerStart = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Log.d("OnTimeSetListener", "entered OnTimeSetListener.");
                    // シャットダウン開始時刻の修正処理
                    start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    start.set(Calendar.MINUTE, minute);
                    start.set(Calendar.SECOND, 0);
                    et_ShutdownStartTime.setText(buildTimeFormat(start));

                    cloneToEnd();
                    et_WakeUpDate.setText(buildDateFormat(end));
                    et_WakeUpTime.setText(buildTimeFormat(end));
                    setResultTime();
                }
            };
            tpListenerEnd = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Log.d("OnTimeSetListener", "entered OnTimeSetListener.");
                    // 起動時刻の修正処理
                    end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    end.set(Calendar.MINUTE, minute);
                    end.set(Calendar.SECOND, 0);

                    et_WakeUpTime.setText(buildTimeFormat(end));
                    setResultTime();
                }
            };
        }

        /**
         *
         */
        private void setResultTime() {
            DateDeltas deltas = new DateDeltas(getActivity());

            tv_shutdownStartAt.setText(buildDateFormat(start) + " " + buildTimeFormat(start));
            tv_shutdownPeriod.setText(deltas.getDeltas(start, end));
            tv_shutdownPeriodInSec.setText(String.valueOf(diffCalendarInSec(start, end)));
        }

        /**
         * endフィールドにstartフィールドの時刻をcloneし、時刻を10分進める。
         */
        private void cloneToEnd() {
            end = (Calendar) start.clone();
            end.add(Calendar.MINUTE, 10);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {

            Context context = getActivity();

            switch (v.getId()) {
                case R.id.et_ShutdownStartDate:
                    showDpDialog(context, start, true);
                    break;
                case R.id.et_ShutdownStartTime:
                    showTpDialog(context, start, true);
                    break;
                case R.id.et_WakeUpDate:
                    showDpDialog(context, end, false);
                    break;
                case R.id.et_WakeUpTime:
                    showTpDialog(context, end, false);
                    break;
            }
        }

        /**
         * DatePickerDialogを表示する。
         * @param context context型、表示するアプリケーションコンテクスト
         * @param calendar Calendar型、表示の際に引き渡すCalendar
         * @param isStart boolean型、trueなら開始日の、falseなら終了日のリスナーをコール
         */
        private void showDpDialog(Context context, Calendar calendar, boolean isStart) {
            // 年、月、日をそれぞれ取得する
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            if (isStart) {
                dpDialog = new DatePickerDialog(context, dpListenerStart, year, month, dayOfMonth);
            } else {
                dpDialog = new DatePickerDialog(context, dpListenerEnd, year, month, dayOfMonth);
            }
            dpDialog.show();
        }

        /**
         * TimePickerDialogを表示する。
         * @param context context型、表示するアプリケーションコンテクスト
         * @param calendar Calendar型、表示の際に引き渡すCalendar
         * @param isStart boolean型、trueなら開始時刻の、falseなら終了時刻のリスナーをコール
         */
        private void showTpDialog(Context context, Calendar calendar, boolean isStart) {
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            if (isStart) {
                tpDialog = new TimePickerDialog(context, tpListenerStart, hourOfDay, minute, isSetting24hourFormat());
            } else {
                tpDialog = new TimePickerDialog(context, tpListenerEnd, hourOfDay, minute, isSetting24hourFormat());
            }
            tpDialog.show();
        }

        /**
         * 画面全体の幅に対し、何分の１のサイズが適当かを返す。
         * @param divisionNumber int型、画面の何分の1にしたいかを指定する。（1/3の場合は3）
         * @return int型、画面全体に対する指定分割数の横幅
         */
        private int getWidgetWidth(int divisionNumber) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int ret = (int)Math.floor(size.x / divisionNumber);

            Log.d("getWidgetWidth", "Overall width = " + String.valueOf(size.x));
            Log.d("getWidgetWidth", "returned width = " + String.valueOf(ret));

            return ret;
        }

        /**
         * editTextに日付をセットする。フォーマットはロケール設定に従う。
         * @param gcd Calendar型、指定日付にセットされたCalendarオブジェクト
         * @return String型、ロケールに沿った日付フォーマットの文字列
         */
        private String buildDateFormat(Calendar gcd) {
            Date dd = gcd.getTime();

            // AndroidのAPIに定義されているDateFormatでロケールを読み出し、
            // java.text.DateFormatに書き出す。
            Context ctx = getActivity().getApplicationContext();
            java.text.DateFormat df = android.text.format.DateFormat.getDateFormat(ctx);
            return df.format(dd);
        }

        /**
         * 端末設定に従い、フォーマット済みの時刻文字列を返す。
         * @param gcd Calendar型、表示するCalendar
         * @return String型、フォーマット済みの時刻文字列
         */
        private String buildTimeFormat(Calendar gcd) {
            Date dd = gcd.getTime();
            Context ctx = getActivity().getApplicationContext();
            java.text.DateFormat df = android.text.format.DateFormat.getTimeFormat(ctx);
            return df.format(dd);
        }

        /**
         * 開始から終了までの時間差を秒で返す。
         * @param startDate Calendar型、開始時刻
         * @param endDate Calendar型、終了時刻
         * @return int型、開始から終了までの時間差（単位：秒）
         */
        private int diffCalendarInSec(Calendar startDate, Calendar endDate) {

            long sd = startDate.getTimeInMillis();
            long ed = endDate.getTimeInMillis();

            // 時刻をミリ秒で取得しているので、秒に直す
            return (int) ((ed - sd) / 1000);
        }

        /**
         * 端末の「日付と時刻の設定」で、時刻表記が24時間制にセットされているか否かを判断する。
         * @return boolean型、24時間制の場合はtrue、12時間制の場合はfalse
         */
        private boolean isSetting24hourFormat() {
            // 12、または24をString型で返してくるため、Stringの比較で判断する。
            String str = Settings.System.getString(
                    getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.TIME_12_24);
            String hours24 = "24";

            return hours24.equals(str);
        }
    }
}
