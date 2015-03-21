package opt.power.com.babytime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import opt.power.com.babytime.opt.power.com.babytime.db.Record;
import opt.power.com.babytime.opt.power.com.babytime.db.RecordDAO;


public class DetailsActivity extends ActionBarActivity {

    private static final String TAG = "baby_" + DetailsActivity.class.getName();

    private EditText startTime = null;//开始时间
    private EditText endTime = null;//结束时间
    private EditText huMilk = null;//人奶
    private EditText milk = null;//牛奶

    private Button btStart = null;//设置开始时间
    private Button btEnd = null;//设置结束时间
    private Button commit = null;//确定

    private RadioGroup types = null;//事件的radios
    private RadioButton bfeeding = null;//喂奶radio
    private RadioButton sleep = null;//睡觉radio
    private RadioButton bathe = null;//洗澡radio
    private RadioButton play = null;//玩radio
    private RadioButton wc = null;//wc radio

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private int type = 1;//事件类型，默认为喂奶

    private long id = -1;//id

    private RecordDAO dao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //初始化
        this.startTime = (EditText) this.findViewById(R.id.d_start_time);
        this.endTime = (EditText) this.findViewById(R.id.d_end_time);
        this.huMilk = (EditText) this.findViewById(R.id.d_human);
        this.milk = (EditText) this.findViewById(R.id.d_milk);

        this.btStart = (Button) this.findViewById(R.id.d_bt_start);
        this.btEnd = (Button) this.findViewById(R.id.d_bt_end_time);
        this.commit = (Button) this.findViewById(R.id.d_bt_commit);

        this.types = (RadioGroup) this.findViewById(R.id.d_rg);
        this.bfeeding = (RadioButton) this.findViewById(R.id.d_rb_bfeeding);
        this.sleep = (RadioButton) this.findViewById(R.id.d_rb_sleep);
        this.bathe = (RadioButton) this.findViewById(R.id.d_rb_bathe);
        this.play = (RadioButton) this.findViewById(R.id.d_rb_play);

        dao = new RecordDAO(this);

        //添加事件
        OpenDate openDate = new OpenDate();
        btStart.setOnClickListener(openDate);
        btEnd.setOnClickListener(openDate);
        commit.setOnClickListener(new GoMain());

        TypeCheck typeCheck = new TypeCheck();
        types.setOnCheckedChangeListener(typeCheck);

        //获取
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        if (id != -1) {
            ShowData showData = new ShowData();
            showData.execute("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.setResult(-1);
            this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 操作数据库
     *
     * @return
     */
    private boolean doData() {
        long duration = -1;
        Record record = new Record();
        if (id != -1) {
            record.setId(id);
        }
        record.setStartTime(startTime.getText().toString());
        Date start;
        try {
            start = formatter.parse(startTime.getText().toString());
            //获取时间
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(start.getTime());
            String day = String.format("%d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            record.setDay(day);
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            Toast.makeText(this, "开始时间格式错误...", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTime.getText() != null && !"".equals(endTime.getText().toString())) {
            Date end;
            try {
                end = formatter.parse(endTime.getText().toString());
                duration = end.getTime() - start.getTime();
                if (duration < 0) {
                    Toast.makeText(this, "结束时间小于开始时间...", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                Toast.makeText(this, "结束时间格式错误...", Toast.LENGTH_SHORT).show();
                return false;
            }
            record.setEndTime(endTime.getText().toString());
        }
        record.setType(type);

        switch (type) {
            case 1://喂奶
                if (!"".equals(huMilk.getText().toString()))//人奶
                {
                    record.setHuMilk(Integer.parseInt(huMilk.getText().toString()));
                }
                if (!"".equals(milk.getText().toString()))//牛奶
                {
                    record.setMilk(Integer.parseInt(milk.getText().toString()));
                }
                if (duration > 0) {
                    record.setMilkTime(duration);
                }
                break;
            case 2://睡觉
                if (duration > 0) {
                    record.setSleepTime(duration);
                }
                break;
            case 3://洗澡
                // TODO do nothing
                Toast.makeText(this, "洗澡未实现...", Toast.LENGTH_SHORT).show();
                return false;
            case 4://玩
                if (duration > 0) {
                    record.setPlayTime(duration);
                }
                break;
            case 5://wc
                record.setIsWc(1);
                break;
        }
        if (id == -1) {
            dao.insert(record);
        } else {
            dao.update(record);
        }
        return true;
    }

    /**
     * 返回main
     */
    private class GoMain implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //DetailsActivity thiz = DetailsActivity.this;
            boolean success = true;
            String message = "操作成功...";

            String startTimeText = startTime.getText().toString();
            //String endTimeText = endTime.getText().toString();

            String huMilkText = huMilk.getText().toString();
            String milkText = milk.getText().toString();

            //没设置开始时间不能通过
            if (startTimeText == null || "".equals(startTimeText.trim())) {
                Toast.makeText(DetailsActivity.this, "开始时间不能为空...", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (type) {
                case 1://喂奶
                    if ((huMilkText == null || "".equals(huMilkText.trim())) && (milkText == null || "".equals(milkText.trim()))) {
                        Toast.makeText(DetailsActivity.this, "喂奶的时候，请至少选择一种剂量...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 2://睡觉

                    break;
                case 3://洗澡

                    break;
                case 4://玩

                    break;
                case 5://wc
                    break;
            }
            if (doData()) {
                Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                DetailsActivity.this.setResult(MainActivity.RE_LOAD);
                DetailsActivity.this.finish();
            } else {
                Toast.makeText(DetailsActivity.this, "操作失败...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 日历控件的处理
     */
    private class OpenDate implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            DetailsActivity thiz = DetailsActivity.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(thiz);
            View view = View.inflate(thiz, R.layout.dialog_datetime, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
            builder.setView(view);

            //获取时间
            Calendar cal = Calendar.getInstance();
            String myTime = v.getId() == thiz.btStart.getId() ? thiz.startTime.getText().toString() : thiz.endTime.getText().toString();
            if (myTime == null || "".equals(myTime.trim())) {
                cal.setTimeInMillis(System.currentTimeMillis());
            } else {
                try {
                    Date temp = formatter.parse(myTime);
                    cal.setTimeInMillis(temp.getTime());
                } catch (ParseException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    cal.setTimeInMillis(System.currentTimeMillis());
                }
            }
            //设置日历
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);

            final EditText myText = v.getId() == thiz.btStart.getId() ? thiz.startTime : thiz.endTime;

            if (v.getId() == thiz.btStart.getId()) {//开始时间按钮
                builder.setTitle("选取开始时间");
            } else if (v.getId() == thiz.btEnd.getId()) {//结束时间按钮
                builder.setTitle("选取结束时间");
            }

            builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    StringBuffer sb = new StringBuffer();
                    sb.append(String.format("%d-%02d-%02d",
                            datePicker.getYear(),
                            datePicker.getMonth() + 1,
                            datePicker.getDayOfMonth()));
                    sb.append(" ");
                    sb.append(String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                    //  sb.append(timePicker.getCurrentHour())
                    //          .append(":").append(timePicker.getCurrentMinute());

                    myText.setText(sb);

                    dialog.cancel();
                }
            });

            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * 类型选择监听
     */
    private class TypeCheck implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int radioButtonId = group.getCheckedRadioButtonId();
            switch (radioButtonId) {
                case R.id.d_rb_bfeeding:
                    type = 1;
                    break;
                case R.id.d_rb_sleep:
                    type = 2;
                    break;
                case R.id.d_rb_bathe:
                    type = 3;
                    break;
                case R.id.d_rb_play:
                    type = 4;
                    break;
                case R.id.d_rb_wc:
                    type = 5;
                    break;
                default:
                    type = -1;
                    break;
            }
        }
    }

    private class ShowData extends AsyncTask<String, String, Boolean> {
        Record record = null;

        /**
         * 在 doInParams...)之前被调用，在ui线程执行   *
         */
        @Override
        protected void onPreExecute() {
            record = new Record();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            record = dao.get(id);
            return record != null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (record != null) {
                startTime.setText(record.getStartTime());
                endTime.setText(record.getEndTime() != null ? record.getEndTime() : "");
                type = record.getType();
                switch (type) {
                    case 1://喂奶
                        bfeeding.setChecked(true);
                        huMilk.setText(record.getHuMilk() > 0 ? record.getHuMilk() + "" : "");
                        milk.setText(record.getMilk() > 0 ? record.getMilk() + "" : "");
                        break;
                    case 2://睡觉
                        sleep.setChecked(true);
                        break;
                    case 3://洗澡
                        bathe.setChecked(true);
                        break;
                    case 4://玩
                        play.setChecked(true);
                        break;
                    case 5://wc
                        wc.setChecked(true);
                        break;
                }
            }
            super.onPostExecute(aBoolean);
        }
    }
}
