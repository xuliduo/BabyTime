package opt.power.com.babytime;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import opt.power.com.babytime.opt.power.com.babytime.db.Record;
import opt.power.com.babytime.opt.power.com.babytime.db.RecordDAO;


public class MainActivity extends ActionBarActivity {

    private static String TAG = "baby_" + MainActivity.class.getName();

    protected static final int RE_LOAD = 1;

    private long lastId = -1l;

    private ListView listView = null;
    private Button btUpdate = null;//更新
    private Button btNew = null;//新增
    private Button btHis = null;//查看历史

    private TextView startTimeText = null;//开始时间的文本
    private TextView endTimeText = null;//结束时间的文本
    private TextView lastTypeText = null;//上次操作的说明

    private ArrayList<HashMap<String, String>> mylist;

    //数据库操作类
    private RecordDAO dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        this.listView = (ListView) this.findViewById(R.id.listView);
        this.btUpdate = (Button) this.findViewById(R.id.bt_update);
        this.btNew = (Button) this.findViewById(R.id.button3);
        this.btHis = (Button) this.findViewById(R.id.button2);

        this.startTimeText = (TextView) this.findViewById(R.id.last_start_time);
        this.endTimeText = (TextView) this.findViewById(R.id.last_end_time);
        this.endTimeText.setText("未设置");
        this.lastTypeText = (TextView) this.findViewById(R.id.last_type);

        dao = new RecordDAO(this);

        GoDetails goClick = new GoDetails();

        //添加click事件
        this.btUpdate.setOnClickListener(goClick);
        this.btNew.setOnClickListener(goClick);
        this.btHis.setOnClickListener(goClick);

        //加载最后一条记录
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeLast();
            }
        });
        //加载ListView
        GetListView task = new GetListView();
        task.execute("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RE_LOAD) {
            //加载最后一条记录
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeLast();
                }
            });
            //加载ListView
            GetListView task = new GetListView();
            task.execute("");
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * 更新ui
     */
    private void changeLast() {
        Record record = getLast();
        if (record != null) {
            lastId = record.getId();
            startTimeText.setText(record.getStartTime());
            endTimeText.setText(record.getEndTime() != null && !"".equals(record.getEndTime()) ? record.getEndTime() : "未设置");
            switch (record.getType()) {
                case 1://喂奶
                    lastTypeText.setText("喂奶->人奶[" + record.getHuMilk() + "],牛奶[" + record.getMilk() + "]");
                    break;
                case 2://睡觉
                    lastTypeText.setText("睡觉");
                    break;
                case 3://洗澡
                    lastTypeText.setText("洗澡");
                    break;
                case 4://玩
                    lastTypeText.setText("玩");
                    break;
                case 5://wc
                    lastTypeText.setText("WC");
                    break;
            }
        } else {
            Toast.makeText(MainActivity.this, "请添加记录...", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 获得最后一条记录
     *
     * @return
     */
    private Record getLast() {
        return dao.getLast();
    }

    /**
     * 展示listview的task
     */
    private class GetListView extends AsyncTask<String, Void, Boolean> {

        /**
         * 在 doInParams...)之前被调用，在ui线程执行   *
         */
        @Override
        protected void onPreExecute() {
            if (mylist == null) {
                mylist = new ArrayList<>();
            }
            mylist.clear();
        }

        /**
         * 在后台线程中执行的任务*
         */
        @Override
        protected Boolean doInBackground(String... params) {
//            //测试数据
//            for (int i = 0; i < 1; i++) {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("listView_time", "2015-03-14");
//                map.put("listView_bfeeding", "喂奶" + "总:[" + 500 + "ml],人奶:[" + 90 + "ml],牛奶[" + 410 + "ml]");
//                map.put("listView_sleep", "睡觉->" + 18.2 + "小时");
//                map.put("listView_wc", "大小便->" + 1 + "次");
//                mylist.add(map);
//            }
            //读取数据库数据（List）
            List<Record> list = dao.getListView(30);
            for (Record record : list) {
                HashMap<String, String> map = new HashMap<>();
                map.put("listView_time", record.getDay());
                map.put("listView_bfeeding", "喂奶" + "总:[" + record.getAllMilkByDay()
                        + "ml],人奶:[" + record.getHuMilkByDay() + "ml],牛奶[" + record.getMilkByDay() + "ml]");
                map.put("listView_sleep", "睡觉->" + record.getSleepByDay() / 1000 / 60 / 60 + "小时");
                map.put("listView_wc", "大小便->" + record.getWcByDay() + "次");
                mylist.add(map);
            }
            return true;
        }

        @Override
        protected void onCancelled() {
            mylist.clear();
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(Boolean boo) {

            //生成适配器
            SimpleAdapter mSchedule = new SimpleAdapter(MainActivity.this,
                    mylist,//数据来源
                    R.layout.listview_top10,//ListItem的XML实现

                    //动态数组与ListItem对应的子项
                    new String[]{"listView_time", "listView_bfeeding", "listView_sleep", "listView_wc"},

                    //ListItem的XML文件里面的两个TextView ID
                    new int[]{R.id.listView_time, R.id.listView_bfeeding, R.id.listView_sleep, R.id.listView_wc});
            //添加并且显示
            listView.setAdapter(mSchedule);
            super.onPostExecute(boo);
        }
    }

    /**
     * 跳转到details的事件
     */
    private class GoDetails implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            switch (v.getId()) {
                //更新
                case R.id.bt_update:
                    intent.putExtra("id", lastId);
                    startActivityForResult(intent, 1);
                    break;
                //新增
                case R.id.button3:
                    intent.putExtra("id", -1l);
                    startActivityForResult(intent, 1);
                    break;
                //历史
                case R.id.button2:
                    intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivityForResult(intent, 1);
                    break;
            }
        }
    }

}
