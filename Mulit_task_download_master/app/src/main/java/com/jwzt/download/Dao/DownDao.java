package com.jwzt.download.Dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jwzt.jxjy.bean.VedioDownloadInfo;
import com.jwzt.jxjy.dao.DBHelper;


/**
 * 下载  数据库操
 * @author afnasdf
 */
public class DownDao {
	private DBHelper dbHelper;
	private static final String DATABASE_NAME = "jxjy_down.db";
	public static String Lock = "dblock";
	public static String file_Lock="fileLock";

	public DownDao(Context context)
	{
		dbHelper = new DBHelper(context);
	}

	/**
	 * 查看下载线程中是否有下载的线
	 * @param urlstr 下载地址
	 * @return count==0 如果数据库里没有数据,返回true,如果数据库里有数据返回false
	 */
	public boolean isHasInfors(String urlstr)
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from threadtab where playpath=?";
		Cursor cursor = database.rawQuery(sql, new String[] { urlstr });
		cursor.moveToFirst();
		//getInt方法返回列的
		int count = cursor.getInt(0);
		cursor.close();
		database.close();
		return count == 0;
	}

	/*
     * 保存视频的下载信
     */
	public  void saveInfos(List<VedioDownloadInfo> infos, Context context)
	{
		//采用事务的方法提高效
		synchronized (Lock)
		{
			SQLiteDatabase database = context.openOrCreateDatabase(	DATABASE_NAME, Context.MODE_PRIVATE, null);
			database.beginTransaction();
			try {
				for (VedioDownloadInfo info : infos) {
					String sql = "insert into threadtab(" +
							"news_id," +
							"name," +
							"newsAbstract," +
							"newsPic," +
							"subTitle," +
							"playpath," +
							"savepath," +
							"currentposition," +
							"vediolength," +
							"downState) values (?,?,?,?,?,?,?,?,?,?)";
					Object[] bindArgs = { info.getId(),
							info.getName(),
							info.getNewsAbstract(),
							info.getNewsPic(),
							info.getSubTitle(),
							info.getPlaypath(),
							info.getSavepath(),
							info.getCurrentposition(),
							info.getVediolength(),
							info.getDownState()};
					database.execSQL(sql, bindArgs);
				}
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
			}
			database.close();
		}

	}


	/**
	 * 查询单个信息
	 * @return DownloadInfo 信息集合,下载信息
	 */
	public VedioDownloadInfo getInfos(String id)
	{
		VedioDownloadInfo info=null;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab where news_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] {id});
		while (cursor.moveToNext()) {
			info = new VedioDownloadInfo(
					cursor.getString(1),//ID
					cursor.getString(2), //name
					cursor.getString(3), //newsAbstract
					cursor.getString(4),//newsPic
					cursor.getString(5),//subTitle
					cursor.getString(6),//playpath
					cursor.getString(7),//savepath
					cursor.getInt(8),//currentposition
					cursor.getInt(9),//vediolength
					cursor.getInt(10));//downState
		}
		cursor.close();
		database.close();
		return info;
	}

	/**
	 * 获得数据库中的所有视频信息
	 * @return List<DownloadInfo>
	 */
	public List<VedioDownloadInfo> getAllInfos()
	{
		List<VedioDownloadInfo> list = new ArrayList<VedioDownloadInfo>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab";
		Cursor cursor = database.rawQuery(sql,new String[]{});
		while (cursor.moveToNext()) {
			VedioDownloadInfo info = new VedioDownloadInfo(
					cursor.getString(1),//ID
					cursor.getString(2), //name
					cursor.getString(3), //newsAbstract
					cursor.getString(4),//newsPic
					cursor.getString(5),//subTitle
					cursor.getString(6),//playpath
					cursor.getString(7),//savepath
					cursor.getInt(8),//currentposition
					cursor.getInt(9),//vediolength
					cursor.getInt(10));//downState);
			list.add(info);
		}
		cursor.close();
		database.close();
		return list;
	}
	/**
	 * 获得数据库中正在下载的视频
	 * @return List<DownloadInfo> 下载器信息集合器,里面存放了每条线程的下载信息
	 */
	public List<VedioDownloadInfo> getInfosAsState(int state)
	{
		List<VedioDownloadInfo> list = new ArrayList<VedioDownloadInfo>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab where downState<?";
		Cursor cursor = database.rawQuery(sql,new String[]{state+""});
		while (cursor.moveToNext()) {
			VedioDownloadInfo info = new VedioDownloadInfo(
					cursor.getString(1),//ID
					cursor.getString(2), //name
					cursor.getString(3), //newsAbstract
					cursor.getString(4),//newsPic
					cursor.getString(5),//subTitle
					cursor.getString(6),//playpath
					cursor.getString(7),//savepath
					cursor.getInt(8),//currentposition
					cursor.getInt(9),//vediolength
					cursor.getInt(10));//downState);
			list.add(info);
		}
		cursor.close();
		database.close();
		return list;
	}


	/**
	 * 获取已经下载的文件
	 * @return List<DownloadInfo> 存放下载线程的所用详细信
	 */
	public List<VedioDownloadInfo> fulfillFile (int downState)
	{
		List<VedioDownloadInfo> list = new ArrayList<VedioDownloadInfo>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab where downState = ?";
		Cursor cursor = database.rawQuery(sql, new String[]{downState+""} );
		while (cursor.moveToNext()) {
			VedioDownloadInfo info = new VedioDownloadInfo(
					cursor.getString(1),//ID
					cursor.getString(2), //name
					cursor.getString(3), //newsAbstract
					cursor.getString(4),//newsPic
					cursor.getString(5),//subTitle
					cursor.getString(6),//playpath
					cursor.getString(7),//savepath
					cursor.getInt(8),//currentposition
					cursor.getInt(9),//vediolength
					cursor.getInt(10));//downState);
			list.add(info);
		}
		cursor.close();
		database.close();
		return list;
	}
	/**
	 * 获取已经下载的文
	 * @return List<DownloadInfo> 存放下载线程的所用详细信
	 */
	public List<VedioDownloadInfo> getfulFileById (String id,String downState)
	{
		List<VedioDownloadInfo> list = new ArrayList<VedioDownloadInfo>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab where news_id = ? and downState = ?";
		Cursor cursor = database.rawQuery(sql, new String[]{id+"",downState} );
		while (cursor.moveToNext()) {
			VedioDownloadInfo info = new VedioDownloadInfo(
					cursor.getString(1),//ID
					cursor.getString(2), //name
					cursor.getString(3), //newsAbstract
					cursor.getString(4),//newsPic
					cursor.getString(5),//subTitle
					cursor.getString(6),//playpath
					cursor.getString(7),//savepath
					cursor.getInt(8),//currentposition
					cursor.getInt(9),//vediolength
					cursor.getInt(10));//downState);
			list.add(info);
		}
		cursor.close();
		database.close();
		return list;
	}
	/**
	 * 获取已经分组显示
	 * @return List<DownloadInfo> 存放下载线程的所用详细信�?
	 */
	public List<VedioDownloadInfo> getfulfillFileByGroup (int downState)
	{
		int vedioId=0;
		List<VedioDownloadInfo> list = new ArrayList<VedioDownloadInfo>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select * from threadtab where downState = ? group by news_id";
		Cursor cursor = database.rawQuery(sql, new String[]{downState+""} );
		while (cursor.moveToNext()) {
			if(vedioId!=cursor.getInt(1)){
				vedioId=cursor.getInt(1);

				VedioDownloadInfo info = new VedioDownloadInfo(
						cursor.getString(1),//ID
						cursor.getString(2), //name
						cursor.getString(3), //newsAbstract
						cursor.getString(4),//newsPic
						cursor.getString(5),//subTitle
						cursor.getString(6),//playpath
						cursor.getString(7),//savepath
						cursor.getInt(8),//currentposition
						cursor.getInt(9),//vediolength
						cursor.getInt(10));//downState);
				list.add(info);
			}

		}
		cursor.close();
		database.close();
		return list;
	}

	/**
	 * 获得数据库中存在的线程记
	 * @return
	 */
	public List<String> url(){
		List<String> list = new ArrayList<String>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select distinct url from threadtab ";

		Cursor cursor=database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		cursor.close();
		database.close();
		return list;

	}



	/**
	 * 如果不存在则返回true
	 * **/
	public boolean isHasFile(String id,String playPath)
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from threadtab where news_id=? and playpath=?";
		Cursor cursor = database.rawQuery(sql, new String[] { id,playPath});
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		database.close();
		return count == 0;
	}


	/**
	 * 如果不存在则返回true
	 * **/
	public boolean isHasFile(String id)
	{
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select count(*)  from threadtab where news_id=?";
		Cursor cursor = database.rawQuery(sql, new String[] { id});
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		database.close();
		return count == 0;
	}



	/**
	 * 更新数据库中的下载信保存和更新方法最好设置为同步
	 * @param threadId 线程
	 * @param compeleteSize 已经下载的长
	 * @param urlstr 下载地址
	 */
	public void updataInfos(int threadId, int compeleteSize, String urlstr,Context context)
	{
		synchronized (Lock)
		{
			//这里因为是要更新数据,要采用写操作,和事务的方法来提高效
//				SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update threadtab set compelete_size=? where thread_id=? and url=?";
			Object[] bindArgs = { compeleteSize, threadId, urlstr };
			//SQLiteDatabase database = dbHelper.getWritableDatabase();
			SQLiteDatabase database = context.openOrCreateDatabase(
					DATABASE_NAME, Context.MODE_PRIVATE, null);
			database.beginTransaction();
			try {
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();
			}
			database.close();
		}

	}


	/**
	 * 更新数据库视频的下载状
	 * @param currentState 当前下载视频的状
	 * @param news_id 下载视频的
	 * @param playPath 下载视频的路
	 */
	public void updataInfos(int currentState,String news_id,String playPath)
	{
		synchronized (Lock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update threadtab set downState=? where news_id=? and playpath=?";
			Object[] bindArgs = { currentState, news_id, playPath };
			database.beginTransaction();//
			try {
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();//关闭事务
			}
			database.close();
		}
	}


	/**
	 * 更改文件的下载保存路径
	 * @param newSavePath
	 * @param news_id
	 * @param playPath
     */
	public void updataInfos_path(String newSavePath,String news_id,String playPath)
	{
		synchronized (Lock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update threadtab set savepath=? where news_id=? and playpath=?";
			Object[] bindArgs = { newSavePath, news_id, playPath };
			database.beginTransaction();//
			try {
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();//关闭事务
			}
			database.close();
		}
	}


	/**
	 * 更新视频下载的长度
	 * @param currentSize 已经下载的长�?
	 * @param news_id 下载视频的id
	 */
	public void updataInfos(long currentSize,String news_id,String playPath){
		synchronized (Lock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update threadtab set currentposition=? where news_id=? and playpath=?";
			Object[] bindArgs = { currentSize, news_id ,playPath};
			database.beginTransaction();//
			try {
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();//关闭事务
			}
			database.close();
		}
	}
	/**
	 * 更新视频下载的长
	 * @param news_id 下载视频的id
	 */
	public int getCurrentPosition(String news_id){
		int currentPosition=0;
		synchronized (Lock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "select currentposition from threadtab where news_id=?";
			String[] bindArgs = {news_id};
			database.beginTransaction();//
			try {
				Cursor cursor=database.rawQuery(sql, bindArgs);

				currentPosition=cursor.getInt(10);//

				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();//关闭事务
			}
			database.close();
		}
		return currentPosition;
	}

	/**
	 * 更新视频的�?长度
	 */
	public void updataFileLength(long length,String news_id,String downPath){
		synchronized (Lock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update threadtab set vediolength=? where news_id=? and playpath=?";
			Object[] bindArgs = { length, news_id ,downPath};
			database.beginTransaction();//
			try {
				database.execSQL(sql, bindArgs);
				database.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				database.endTransaction();//关闭事务
			}
			database.close();
		}
	}



	/**
	 * 关闭数据�?
	 */
	public void closeDb() {
		dbHelper.close();
	}

	/**
	 * 删除指定文件的所有文件记�?
	 */
	public void deleteInfor(List<VedioDownloadInfo> delectList)
	{
		synchronized (Lock)
		{
			for(int i=0;i<delectList.size();i++){

				SQLiteDatabase database = dbHelper.getReadableDatabase();
				database.execSQL("DELETE FROM threadtab WHERE news_id=? and playpath=? ",new Object[]{delectList.get(i).getId(),delectList.get(i).getPlaypath()});
				database.close();

			}

		}

	}



	/**
	 * 删除下载记录
	 */
	public void delete(VedioDownloadInfo delectList){
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		database.execSQL("DELETE FROM threadtab WHERE news_id=? and playpath=? ",new Object[]{delectList.getId(),delectList.getPlaypath()});
		database.close();
	}

	/**
	 * 刪除文件信息  用在刪除文件的時
	 */
	public void deleteFileState(String fileName)
	{
		SQLiteDatabase database=dbHelper.getReadableDatabase();
		database.delete("localfile_tab", "file_name=?",new String[]{fileName});
		database.close();
	}

}
