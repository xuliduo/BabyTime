package opt.power.com.babytime.opt.power.com.babytime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by xuliduo on 15/3/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getName();

    private static final String DB_NAME = "baby";
    private static final int DB_VERSION = 1;
    private static final String DB_DIR = Environment.getExternalStorageDirectory().getPath() + "/baby_db";

    private static SQLiteDatabase DB;

    /**
     * 存在sdcard的db
     *
     * @param context
     */
    public DBHelper(Context context) {
//        super(new ContextWrapper(context) {
//            @Override
//            public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
//                // allow database directory to be specified
//                File dir = new File(DB_DIR);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                return SQLiteDatabase.openDatabase(DB_DIR + "/" + DB_NAME, null,
//                        SQLiteDatabase.CREATE_IF_NECESSARY);
//            }
//        }, DB_NAME, null, DB_VERSION);
        super(context, DB_DIR + "/" + DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecordDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 获得数据库
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getDB(Context context) {
        if (DB == null || !DB.isOpen()) {
            DB = new DBHelper(context).getWritableDatabase();
        }
        return DB;
    }
}
