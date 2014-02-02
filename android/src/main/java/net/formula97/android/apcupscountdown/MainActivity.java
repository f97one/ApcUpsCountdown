package net.formula97.android.apcupscountdown;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

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

        DatePickerDialog.OnDateSetListener dpListener;
        TimePickerDialog.OnTimeSetListener tpListener;

        Calendar start;

        public Calendar getStart() {
            if (start != null) start.clear();
            start = Calendar.getInstance();
            return start;
        }

        public Calendar getEnd() {
            if (start != null) {
                end = (Calendar) start.clone();
            } else {
                end = Calendar.getInstance();
            }
            return end;
        }

        Calendar end;

        public PlaceholderFragment() {

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

            et_ShutdownStartTime.setWidth(getWidgetWidth(5));
            et_ShutdownStartDate.setWidth(getWidgetWidth(3));
            et_WakeUpTime.setWidth(getWidgetWidth(5));
            et_WakeUpDate.setWidth(getWidgetWidth(3));

            // 現在時刻＋２分をセット
            Calendar disp = getDelayed(2);
            Calendar delayed10 = (Calendar) disp.clone();
            delayed10.add(Calendar.MINUTE, 10);

            start = (Calendar) disp.clone();
            end = (Calendar) delayed10.clone();

            et_ShutdownStartDate.setText(buildDateFormat(disp));
            et_ShutdownStartTime.setText(disp.get(Calendar.HOUR_OF_DAY) + ":" + disp.get(Calendar.MINUTE));
            et_WakeUpDate.setText(buildDateFormat(delayed10));
            et_WakeUpTime.setText(delayed10.get(Calendar.HOUR_OF_DAY) + ":" + delayed10.get(Calendar.MINUTE));

            tv_shutdownStartAt.setText(buildDateFormat(disp) + " " + disp.get(Calendar.HOUR_OF_DAY) + ":" + disp.get(Calendar.MINUTE));
            tv_shutdownPeriod.setText(String.valueOf(diffCalendarInMin(disp, delayed10)));
            tv_shutdownPeriodInSec.setText(String.valueOf(diffCalendarInSec(disp, delayed10)));

            // クリックリスナーをセット
            et_ShutdownStartDate.setOnClickListener(this);
            et_ShutdownStartTime.setOnClickListener(this);
            et_WakeUpDate.setOnClickListener(this);
            et_WakeUpTime.setOnClickListener(this);

            // DatePickerListenerをセット
            dpListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    switch(view.getId()) {
                        case R.id.et_ShutdownStartDate:
                            start.set(year, monthOfYear, dayOfMonth);
                            et_ShutdownStartDate.setText(buildDateFormat(start));

                            end = (Calendar) start.clone();
                            et_WakeUpDate.setText(buildDateFormat(end));
                            break;
                        case R.id.et_WakeUpDate:
                            end.set(year, monthOfYear, dayOfMonth);
                            et_WakeUpDate.setText(buildDateFormat(end));
                            break;
                    }
                }
            };

            // TimePickerListenerをセット
            tpListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                }
            };
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
                    showDpDialog(context, getStart());

                    break;
                case R.id.et_ShutdownStartTime:
                    break;
                case R.id.et_WakeUpDate:
                    showDpDialog(context, getEnd());

                    break;
                case R.id.et_WakeUpTime:
                    break;
            }
        }

        private void showDpDialog(Context context, Calendar calendar) {
            // 年、月、日をそれぞれ取得する
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            dpDialog = new DatePickerDialog(context, dpListener, year, month, dayOfMonth);
            dpDialog.show();
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

        public Calendar getDelayed(int delayedMinute) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, delayedMinute);

            return calendar;
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

        private int diffCalendarInMin(Calendar startDate, Calendar endDate) {
            return (int) (diffCalendarInSec(startDate, endDate) / 60);
        }

        private int diffCalendarInSec(Calendar startDate, Calendar endDate) {

            long sd = startDate.getTimeInMillis();
            long ed = endDate.getTimeInMillis();

            // 時刻をミリ秒で取得しているので、秒に直す
            return (int) ((ed - sd) / 1000);
        }

        /**
         * 端末の「日付と時刻の設定」で、時刻表記が24時間制か否かを判断する。
         * @return boolean型、24時間制の場合はtrue、12時間制の場合はfalse
         */
        private boolean isSetting24hourFormat() {
            // 12、または24をString型で返してくるため、Stringの比較で判断する。
            String str = Settings.System.getString(
                    getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.TIME_12_24);
            String hours24 = "24";

            return hours24.equals(str) ? true : false;
        }

    }


}
