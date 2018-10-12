package com.eningqu.emeeting.utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eningqu.emeeting.bean.TranslateInfo;

import java.util.ArrayList;
import java.util.List;

public class TranslateDao {
        private DBHelper mMyDBHelper;

        /**
         * dao类需要实例化数据库Help类,只有得到帮助类的对象我们才可以实例化 SQLiteDatabase
         * @param context
         */
        public TranslateDao(Context context) {
            mMyDBHelper=new DBHelper(context);
        }

        // 将数据库打开帮帮助类实例化，然后利用这个对象
        // 调用谷歌的api去进行增删改查

        // 增加的方法吗，返回的的是一个long值
        public long addDate(String oldlanguage, String tranlanguage, String audiopath, int trantype){
            // 增删改查每一个方法都要得到数据库，然后操作完成后一定要关闭
            // getWritableDatabase(); 执行后数据库文件才会生成
            // 数据库文件利用DDMS可以查看，在 data/data/包名/databases 目录下即可查看
            SQLiteDatabase sqLiteDatabase =  mMyDBHelper.getWritableDatabase();
            ContentValues contentValues=new ContentValues();

            contentValues.put("oldlanguage",oldlanguage);
            contentValues.put("tranlanguage", tranlanguage);
            contentValues.put("audiopath", audiopath);
            contentValues.put("trantype",trantype);
            contentValues.put("trandate", System.currentTimeMillis()+"");
            // 返回,显示数据添加在第几行
            // 加了现在连续添加了3行数据,突然删掉第三行,然后再添加一条数据返回的是4不是3
            // 因为自增长
            long rowid=sqLiteDatabase.insert("translatehistory",null,contentValues);

            sqLiteDatabase.close();
            return rowid;
        }


        public int deleteAllDate(){
            SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
            int deleteResult = sqLiteDatabase.delete("translatehistory",null,null);
            sqLiteDatabase.close();
            return deleteResult;
        }


        public List<TranslateInfo> alterAllDate(){
            SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
            // 查询比较特别,涉及到 cursor
            Cursor cursor = readableDatabase.query("translatehistory", new String[]{"oldlanguage,tranlanguage,audiopath,trantype,trandate"}, null,null, null, null, "trandate asc");

            List<TranslateInfo> mTranslateInfoList = new ArrayList<TranslateInfo>();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        TranslateInfo mTranslateInfo = new TranslateInfo();
                        mTranslateInfo.setTranslate_old(cursor.getString(0));
                        mTranslateInfo.setTranslate_other(cursor.getString(1));
                        mTranslateInfo.setUrl(cursor.getString(2));
                        mTranslateInfo.setType(cursor.getInt(3));
                        mTranslateInfo.setDate(cursor.getString(4));
                        mTranslateInfoList.add(mTranslateInfo);
                    } while (cursor.moveToNext());

                }
            }

            cursor.close(); // 记得关闭 corsor
            readableDatabase.close(); // 关闭数据库
            return mTranslateInfoList;
        }
}
