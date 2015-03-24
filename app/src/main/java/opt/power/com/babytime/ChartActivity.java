package opt.power.com.babytime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import opt.power.com.babytime.opt.power.com.babytime.db.Record;
import opt.power.com.babytime.opt.power.com.babytime.db.RecordDAO;


public class ChartActivity extends ActionBarActivity {
    private static final String TAG = "baby_" + ChartActivity.class.getName();

    private EditText dateText = null;//日期框
    private WebView webView = null;//webView

    private RecordDAO dao = null;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //初始化
        dateText = (EditText) this.findViewById(R.id.ct_date);
        initWebView();

        dao = new RecordDAO(this);
        //事件处理
        dateText.setInputType(InputType.TYPE_NULL);
        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(ChartActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateText.setText(String.format("%d-%02d", year, (monthOfYear + 1)));
                            showChartData(ChartActivity.this.dateText.getText().toString());
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(ChartActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateText.setText(String.format("%d-%02d", year, (monthOfYear + 1)));
                        showChartData(ChartActivity.this.dateText.getText().toString());
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

    }

    //初始化webView
    private void initWebView() {
        webView = (WebView) this.findViewById(R.id.ct_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //showChartData(null);
            }

        });

        webView.loadUrl("file:///android_asset/chart.html");
    }

    /**
     * 初始化数据
     *
     * @param month //yyyy-MM
     */

    private void showChartData(String month) {
        if (month == null) {
            //获取时间
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(new Date().getTime());
            month = String.format("%d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        }
        final String ss = month;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dateText.setText(ss);
                List<Record> records = dao.getRecordsByMonth(ss);
                int[] pie = dao.getBfeedingByMonth(ss);
                //拼装报表
                // String data = "[";
                String dataModle = "[%d,%d]";//单天模板
                String pieData = "[[[0, %d]],[[0, %d]]]";//pie的数据（pie是个二维数组）

                StringBuilder huMilk = new StringBuilder("[");//人奶
                StringBuilder milk = new StringBuilder("[");//牛奶
                StringBuilder milkTime = new StringBuilder("[");//喝奶时间
                StringBuilder milkTimeMs = new StringBuilder("[");//喝奶时间的标签
                StringBuilder sleepTime = new StringBuilder("[");//睡觉时间
                StringBuilder sleepTimeMs = new StringBuilder("[");//睡觉时间的标签
                StringBuilder wc = new StringBuilder("[");//wc次数
                StringBuilder wcMs = new StringBuilder("[");//wc次数的标签

                //所有报表
                //开始处理柱状图
                Date temp = null;
                try {
                    temp = formatter.parse(ss);
                } catch (ParseException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
                //获得本月最后一天
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(temp.getTime());

                cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
                cal.add(Calendar.MONTH, 1);//月增加1天
                cal.add(Calendar.DAY_OF_MONTH, -1);//日期倒数一日,既得到本月最后一天

                int lastDay = cal.get(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= lastDay; i++) {
                    boolean hit = false;//验证records数据中是否有本天的数据
                    for (int j = 0; j < records.size(); j++) {
                        String day = records.get(j).getDay();//2015-03-18
                        int thisDay = Integer.parseInt(day.split("-")[2]);
                        if (i == thisDay) {//命中
                            hit = true;
                            Record record = records.get(j);
                            huMilk.append(String.format(dataModle, i, record.getHuMilkByDay()) + ",");
                            milk.append(String.format(dataModle, i, record.getMilkByDay()) + ",");
                            milkTime.append(String.format(dataModle, i, record.getMilkTimeByDay() / 1000 / 60 / 60) + ",");
                            milkTimeMs.append(String.format(dataModle, i, record.getMilkTimeByDay() / 1000 / 60 / 60) + ",");
                            sleepTime.append(String.format(dataModle, i, record.getSleepByDay() / 1000 / 60 / 60) + ",");
                            sleepTimeMs.append(String.format(dataModle, i, record.getSleepByDay() / 1000 / 60 / 60) + ",");
                            wc.append(String.format(dataModle, i, record.getWcByDay()) + ",");
                            wcMs.append(String.format(dataModle, i, record.getWcByDay()) + ",");
                            records.remove(j);
                            break;
                        }
                    }

                    //如果命中了
                    if (hit) {
                        continue;
                    } else {
                        String tempStr = String.format(dataModle, i, 0) + ",";
                        huMilk.append(tempStr);
                        milk.append(tempStr);
                        milkTime.append(tempStr);
                        milkTimeMs.append(tempStr);
                        sleepTime.append(tempStr);
                        sleepTimeMs.append(tempStr);
                        wc.append(tempStr);
                        wcMs.append(tempStr);
                    }
                }
                //去掉最后一个逗号
                huMilk.deleteCharAt(huMilk.length() - 1);
                milk.deleteCharAt(milk.length() - 1);
                milkTime.deleteCharAt(milkTime.length() - 1);
                milkTimeMs.deleteCharAt(milkTimeMs.length() - 1);
                sleepTime.deleteCharAt(sleepTime.length() - 1);
                sleepTimeMs.deleteCharAt(sleepTimeMs.length() - 1);
                wc.deleteCharAt(wc.length() - 1);
                wcMs.deleteCharAt(wcMs.length() - 1);

                //添加结束框
                huMilk.append("],");
                milk.append("]");
                milkTime.append("],");
                milkTimeMs.append("]");
                sleepTime.append("],");
                sleepTimeMs.append("]");
                wc.append("],");
                wcMs.append("]");

                String input = "[" + huMilk.toString() + milk.toString() + "] , [" +
                        milkTime.toString() + milkTimeMs.toString() +
                        "] , [" + sleepTime.toString() + sleepTimeMs.toString() +
                        "] , [" + wc.toString() + wcMs.toString() +
                        "] , " + String.format(pieData, pie[0], pie[1]);
                //Log.d(TAG, input);
                //showChart(milk, milk_time, sleep_time, wc, pie)
                webView.loadUrl("javascript:showChart(" + input + ")");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
