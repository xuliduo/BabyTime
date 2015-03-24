package opt.power.com.babytime;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import opt.power.com.babytime.opt.power.com.babytime.db.Record;
import opt.power.com.babytime.opt.power.com.babytime.db.RecordDAO;


public class HistoryActivity extends ActionBarActivity {

    private static String TAG = HistoryActivity.class.getName();

    private ListView listView = null;//listView

    private RecordDAO dao = null;

    private ArrayList<HashMap<String, String>> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView) this.findViewById(R.id.his_list);


        listView.setClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                long recordID = Long.parseLong(((TextView) view.findViewById(R.id.listView_his_id)).getText().toString());
                Intent intent = new Intent(HistoryActivity.this, DetailsActivity.class);
                intent.putExtra("id", recordID);
                startActivityForResult(intent, 1);
                return true;
            }
        });

        dao = new RecordDAO(this);

        GetListView dome = new GetListView();
        dome.execute("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.RE_LOAD) {
            //加载ListView
            GetListView task = new GetListView();
            task.execute("");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.setResult(MainActivity.RE_LOAD);
            this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 展示listview的task
     */
    private class GetListView extends AsyncTask<String, Void, Boolean> {
        String[] m = new String[]{"listView_his_id", "listView_his_start_time", "listView_his_end_time", "listView_his_type"};

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
            //读取数据库数据（List）
            List<Record> list = dao.getRecords(50, 1);
            for (Record record : list) {
                HashMap<String, String> map = new HashMap<>();
                map.put(m[0], record.getId() + "");
                map.put(m[1], record.getStartTime());
                map.put(m[2], record.getEndTime() != null && !"".equals(record.getEndTime()) ? record.getEndTime() : "未设置");
                switch (record.getType()) {
                    case 1://喂奶
                        map.put(m[3], "喂奶->人奶[" + record.getHuMilk() + "],牛奶[" + record.getMilk() + "]");
                        break;
                    case 2://睡觉
                        map.put(m[3], "睡觉");
                        break;
                    case 3://洗澡
                        map.put(m[3], "洗澡");
                        break;
                    case 4://玩
                        map.put(m[3], "玩");
                        break;
                    case 5://wc
                        map.put(m[3], "WC");
                        break;
                }
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
            SimpleAdapter mSchedule = new SimpleAdapter(HistoryActivity.this,
                    mylist,//数据来源
                    R.layout.listview_his,//ListItem的XML实现

                    //动态数组与ListItem对应的子项
                    m,
                    //ListItem的XML文件里面的两个TextView ID
                    new int[]{R.id.listView_his_id, R.id.listView_his_start_time, R.id.listView_his_end_time, R.id.listView_his_type});
            //添加并且显示
            listView.setAdapter(mSchedule);
            super.onPostExecute(boo);
        }

    }
}
