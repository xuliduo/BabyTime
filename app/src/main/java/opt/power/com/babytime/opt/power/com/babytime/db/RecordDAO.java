package opt.power.com.babytime.opt.power.com.babytime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * record表操作
 * Created by xuliduo on 15/3/14.
 */
public class RecordDAO {

    //public static enum BabyType {喂奶, 睡觉, 洗澡, WC, 玩}

    public static final String TABLE_NAME = "record";

    private static final String KEY_ID = "_id";

    public static final String[] COLUMN = new String[]{"day", "start_time", "end_time", "type", "hu_milk", "milk",
            "milk_time", "sleep_time", "is_wc", "play_time", "xinai_milk", "xinai_time"};

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN[0] + " TEXT NOT NULL, " +
            COLUMN[1] + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
            COLUMN[2] + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN[3] + " INTEGER NOT NULL, " +
            COLUMN[4] + " INTEGER, " +
            COLUMN[5] + " INTEGER," +
            COLUMN[6] + " INTEGER," +
            COLUMN[7] + " INTEGER," +
            COLUMN[8] + " INTEGER," +
            COLUMN[9] + " INTEGER," +
            COLUMN[10] + " INTEGER," +
            COLUMN[11] + " INTEGER )";

    private SQLiteDatabase db;

    /**
     * create a database conn
     *
     * @param context
     */
    public RecordDAO(Context context) {
        db = DBHelper.getDB(context);
    }

    /**
     * close the database conn
     */
    public void close() {
        if (db != null) {
            db.close();
        }
    }

    /**
     * @param record
     */
    private ContentValues getCV(Record record) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN[0], record.getDay());
        cv.put(COLUMN[1], record.getStartTime());
        cv.put(COLUMN[2], record.getEndTime());
        cv.put(COLUMN[3], record.getType());
        cv.put(COLUMN[4], record.getHuMilk());
        cv.put(COLUMN[5], record.getMilk());
        cv.put(COLUMN[6], record.getMilkTime());
        cv.put(COLUMN[7], record.getSleepTime());
        cv.put(COLUMN[8], record.getIsWc());
        cv.put(COLUMN[9], record.getPlayTime());
        cv.put(COLUMN[10], record.getXinaiMilk());
        cv.put(COLUMN[11], record.getXinaiTime());
        return cv;
    }

    /**
     * 插入
     *
     * @param record
     * @return
     */
    public Record insert(Record record) {

        ContentValues cv = getCV(record);

        long id = db.insert(TABLE_NAME, null, cv);
        record.setId(id);

        return record;
    }

    /**
     * 更新
     *
     * @param record
     * @return
     */
    public boolean update(Record record) {
        ContentValues cv = getCV(record);

        String where = KEY_ID + "=" + record.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    /**
     * 封装
     *
     * @param cursor
     * @return
     */
    protected Record getRecord(Cursor cursor) {
        Record record = new Record();

        record.setId(cursor.getLong(0));
        record.setDay(cursor.getString(1));
        record.setStartTime(cursor.getString(2));
        record.setEndTime(cursor.getString(3));
        record.setType(cursor.getInt(4));
        record.setHuMilk(cursor.getInt(5));
        record.setMilk(cursor.getInt(6));
        record.setMilkTime(cursor.getLong(7));
        record.setSleepTime(cursor.getLong(8));
        record.setIsWc(cursor.getInt(9));
        record.setPlayTime(cursor.getLong(10));
        record.setXinaiMilk(cursor.getInt(11));
        record.setXinaiTime(cursor.getLong(12));

        return record;
    }

    /**
     * 通过id获得对象
     *
     * @param id
     * @return
     */
    public Record get(long id) {
        Record record = null;
        String where = KEY_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            record = getRecord(result);
        }

        result.close();

        return record;
    }

    /**
     * 获得最后一条记录
     *
     * @return
     */
    public Record getLast() {
        Record record = null;

        Cursor result = db.query(TABLE_NAME, null, null, null, null, null, "start_time desc", "1");

        if (result.moveToFirst()) {
            record = getRecord(result);
        }

        result.close();

        return record;
    }

    /**
     * 获得汇总值（listVew用的）
     *
     * @param limit 数量
     * @return
     */
    public List<Record> getListView(int limit) {
        return getListGroupByDay("1=1", limit, "DESC");
    }

    /**
     * 通过day进行汇总
     *
     * @param where
     * @param limit
     * @param desc
     * @return
     */
    private List<Record> getListGroupByDay(String where, int limit, String desc) {
        List<Record> list = new ArrayList<>();
        String sql = "SELECT SUM(hu_milk),SUM(milk),SUM(hu_milk+milk),SUM(milk_time)," +
                "SUM(sleep_time),SUM(is_wc),SUM(play_time),SUM(xinai_milk),SUM(xinai_time),day FROM " +
                TABLE_NAME + " WHERE " +
                where + " GROUP BY day ORDER BY day "
                + desc + " LIMIT " + limit;
        Cursor result = db.rawQuery(sql, null);
        while (result.moveToNext()) {
            Record record = new Record();
            record.setHuMilkByDay(result.getLong(0));
            record.setMilkByDay(result.getLong(1));
            record.setAllMilkByDay(result.getLong(2));
            record.setMilkTimeByDay(result.getLong(3));
            record.setSleepByDay(result.getLong(4));
            record.setWcByDay(result.getLong(5));
            record.setPlayByDay(result.getLong(6));
            record.setXinaiMilkByDay(result.getLong(7));
            record.setXinaiTimeByDay(result.getLong(8));
            record.setDay(result.getString(9));

            list.add(record);
        }
        result.close();

        return list;
    }

    /**
     * 获得最近几天的数据
     *
     * @param limit
     * @param page
     * @return
     */
    public List<Record> getRecords(int limit, int page) {
        List<Record> list = new ArrayList<>();
        Cursor result = db.query(TABLE_NAME, null, null, null, null, null, "start_time desc", String.valueOf(limit));
        while (result.moveToNext()) {
            Record record = this.getRecord(result);
            list.add(record);
        }
        return list;
    }

    /**
     * 通过日期，获得汇总数据
     *
     * @param month
     * @return
     */
    public List<Record> getRecordsByMonth(String month) {
        return this.getListGroupByDay("day LIKE '" + month + "%'", 60, "");
    }

    /**
     * 通过日期，获得当月牛奶占比
     *
     * @param month
     * @return
     */
    public int[] getBfeedingByMonth(String month) {
        int[] re = new int[2];
        String sql = "SELECT SUM(hu_milk),SUM(milk),substr(day,1,4)||'-'||substr(day,6,2) as month FROM " +
                TABLE_NAME + " WHERE day LIKE '"
                + month + "%' GROUP BY month";
        Cursor result = db.rawQuery(sql, null);
        while (result.moveToNext()) {
            re[0] = (int) result.getLong(0);
            re[1] = (int) result.getLong(1);
        }
        result.close();
        return re;
    }
}
