package com.jwzt.download.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.jwzt.download.Dao.DownDao;
import com.jwzt.download.application.JXJYApplication;
import com.jwzt.download.bean.VedioDownloadInfo;


/**
 * 下载管理(并实时更新数据库)
 * 
 * @author afnasdf
 */
public class DownloadManage {

	/**
	 * 下载的状
	 */
	public static final int STATE_NORMAL = 0;
	public static final int STATE_WAITING = 1;
	public static final int STATE_DOWANLODING = 2;
	public static final int STATE_PAUSE = 3;
	public static final int STATE_ERROR = 4;
	public static final int STATE_COMPULE = 5;
	public static final String  key = "12345678";


	private static String Algorithm = "DES"; // 定义 加密算法,可用DES,DESede,Blowfish


	private static DownloadManage mInstance;

	private Map<String, VedioDownloadInfo> downloadInfoMap = new ConcurrentHashMap<String, VedioDownloadInfo>();
	private Map<String, DownloadTask> downloadTaskMap = new ConcurrentHashMap<String, DownloadTask>();

	private DownloadManage() {
		
//		if(timer==null){
//			timer=new Timer();
//			timer.schedule(timerTask_speed,0, 3000);
//		}
	}

	private List<DownloadObser> obsers = new ArrayList<DownloadObser>();

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static DownloadManage getInstance() {
		synchronized (DownloadManage.class) {
			if (mInstance == null) {
				mInstance = new DownloadManage();
			}
		}
		return mInstance;
	}

	/**
	 * 设置要下载的信息
	 * 
	 * @param info
	 */
	public VedioDownloadInfo getDownloadInfo(String info) {
		System.out.println(downloadInfoMap.containsKey(info));
		if (downloadInfoMap.containsKey(info)) {
			return downloadInfoMap.get(info);
		}
		return null;
	}
	/**
	 * 下载
	 */
	public void download(List<VedioDownloadInfo> infos) {
		if(infos.size()>0){
		for (VedioDownloadInfo info : infos) {
		VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
		if (downloadInfo == null) {
			downloadInfo = info;
			downloadInfoMap.put(info.getPlaypath(), info);
		}else{
			downloadInfoMap.remove(info.getPlaypath());
			downloadInfo = info;
			downloadInfoMap.put(info.getPlaypath(), info);
		}
		if (downloadInfo.getDownState() == STATE_NORMAL
				|| downloadInfo.getDownState() == STATE_ERROR
				|| downloadInfo.getDownState() == STATE_PAUSE) {
			downloadInfo.setDownState(STATE_WAITING);
			//打开数据
			DownDao dao = new DownDao(JXJYApplication.getContext());
			// 更新数据库中的下载状
			dao.updataInfos(STATE_WAITING, downloadInfo.getId() + "",
					downloadInfo.getPlaypath());
			dao.closeDb();//关闭数据
			// 更新UI
			notifyStateChanged(downloadInfo);
			DownloadTask task = new DownloadTask(downloadInfo);
			downloadTaskMap.put(info.getPlaypath(), task);
			ThreadPoolManager.getDownloadPool().execute(task);
		
		}
			}}
	}
	
	
	/**
	 * 开始下载（单个任务）
	 * @param info
	 */
	public void downloadSingle(VedioDownloadInfo info) {
				VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
				if (downloadInfo == null) {
					downloadInfo = info;
					downloadInfoMap.put(info.getPlaypath(), info);
				}
				if (downloadInfo.getDownState() == STATE_NORMAL
						|| downloadInfo.getDownState() == STATE_ERROR
						|| downloadInfo.getDownState() == STATE_PAUSE
						|| downloadInfo.getDownState() == STATE_DOWANLODING
						|| downloadInfo.getDownState() == STATE_WAITING) {
					
					downloadInfo.setDownState(STATE_WAITING);
					DownDao dao = new DownDao(JXJYApplication.getContext());
					dao.updataInfos(STATE_WAITING, downloadInfo.getId() + "",
							downloadInfo.getPlaypath());
					dao.closeDb();//关闭数据
					// 更新UI
					notifyStateChanged(downloadInfo);
					DownloadTask task = new DownloadTask(downloadInfo);
					downloadTaskMap.put(info.getPlaypath(), task);
					ThreadPoolManager.getDownloadPool().execute(task);
				}
	}
	/**
	 * 离开系统开始下载（单个任务）
	 * @param info
	 */
	public void downloadExitSystem(VedioDownloadInfo info) {
		VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
		if (downloadInfo == null) {
			downloadInfo = info;
			downloadInfoMap.put(info.getPlaypath(), info);
		}
		if (downloadInfo.getDownState() == STATE_NORMAL
				|| downloadInfo.getDownState() == STATE_ERROR
				|| downloadInfo.getDownState() == STATE_PAUSE
				||downloadInfo.getDownState() == STATE_DOWANLODING) {
			downloadInfo.setDownState(STATE_WAITING);
			//打开数据
			DownDao dao = new DownDao(JXJYApplication.getContext());
			// 更新数据库中的下载状
			dao.updataInfos(STATE_WAITING, downloadInfo.getId() + "",
					downloadInfo.getPlaypath());
			dao.closeDb();//关闭数据
			// 更新UI
			notifyStateChanged(downloadInfo);
			DownloadTask task = new DownloadTask(downloadInfo);
			downloadTaskMap.put(info.getPlaypath(), task);
			ThreadPoolManager.getDownloadPool().execute(task);
		}
	}

	/**
	 * 全部取消下载
	 */
	public void pause(List<VedioDownloadInfo> infos) {
		if(infos.size()>0){
			for (VedioDownloadInfo info : infos) {
				VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
				if (downloadInfo != null) {
					downloadInfo.setDownState(STATE_PAUSE);
					DownDao dao = new DownDao(JXJYApplication.getContext());
					dao.updataInfos(STATE_PAUSE, downloadInfo.getId() + "",
							downloadInfo.getPlaypath());
					dao.closeDb();
					notifyStateChanged(downloadInfo);
					cancelTask(downloadInfo);
				}
			}
		}
		
	}
	
	
	/**
	 * 单个下载任务的停
	 * @param info
	 */
	public void pauseSingle(VedioDownloadInfo info) {
				VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
				if (downloadInfo != null) {
							downloadInfo.setDownState(STATE_PAUSE);
							cancelTask(downloadInfo);
							notifyStateChanged(downloadInfo);
							DownDao dao = new DownDao(JXJYApplication.getContext());
							dao.updataInfos(STATE_PAUSE, downloadInfo.getId() + "",
									downloadInfo.getPlaypath());
							dao.closeDb();

				
				}
		
	}

	
	/**
	 * 应用后重新开始下
	 */
	public void downloadAfterExitSystem(List<VedioDownloadInfo> infos) {
		if(infos.size()>0){
			for (VedioDownloadInfo info : infos) {
				VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
				if (downloadInfo == null) {
					downloadInfo = info;
					downloadInfoMap.put(info.getPlaypath(), info);
					downloadInfo.setDownState(STATE_WAITING);
					//打开数据
					DownDao dao = new DownDao(JXJYApplication.getContext());
					// 更新数据库中的下载状
					dao.updataInfos(STATE_WAITING, downloadInfo.getId() + "",
							downloadInfo.getPlaypath());
					dao.closeDb();//关闭数据
					// 更新UI
					notifyStateChanged(downloadInfo);
					DownloadTask task = new DownloadTask(downloadInfo);
					downloadTaskMap.put(info.getPlaypath(), task);
					ThreadPoolManager.getDownloadPool().execute(task);
				}
			}}
	}
	
	
	/**
	 * 取消下载任务
	 */
	public void cancel(List<VedioDownloadInfo> infos) {
		for(VedioDownloadInfo info:infos){
			VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
			if (downloadInfo != null) {
				downloadInfo.setDownState(STATE_NORMAL);
				// 通知UI
				notifyStateChanged(downloadInfo);
				cancelTask(info);
				
				downloadInfoMap.remove(info.getPlaypath());
				File file = new File(downloadInfo.getSavepath());
				file.delete();
			}
		}
		
	}
	/**
	 * 取消单个下载任务
	 * @param info
	 */
	public void cancelSingle(VedioDownloadInfo info) {
			VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getPlaypath());
			if (downloadInfo != null) {
				downloadInfo.setDownState(STATE_NORMAL);
				// 通知UI
				notifyStateChanged(downloadInfo);
				cancelTask(info);
				
				downloadInfoMap.remove(info.getPlaypath());
				File file = new File(downloadInfo.getSavepath());
				file.delete();
		}
		
	}

	/**
	 * 下载完成状
	 * @param info
	 */
	public void downCompule(VedioDownloadInfo info) {
		VedioDownloadInfo downloadInfo = downloadInfoMap.get(info.getId());// 找出下载信息
		if (info != null) {
//			if(downloadTaskMap.containsKey(downloadInfo.getPlaypath())){
//				downloadTaskMap.remove(downloadInfo.getPlaypath());
//			}
//			downloadInfoMap.remove(downloadInfo.getId());
//			cancelTask(downloadInfo);//下载完成取消当前线程下载任务栈
		}
	}

	/**
	 * 取消下载任务 
	 * @param info
	 */
	private void cancelTask(VedioDownloadInfo info) {
		DownloadTask task = downloadTaskMap.get(info.getPlaypath());
		if (task != null) {
			ThreadPoolManager.getDownloadPool().cancel(task);
		}
		ThreadPoolManager.getDownloadPool().cancel(task);
	}

	/**
	 * 执行下载任务
	 * 
	 * @author afnasdf
	 * 
	 */
	class DownloadTask implements Runnable {

		private VedioDownloadInfo mInfo;
		private File file;

		public DownloadTask(VedioDownloadInfo info) {
			mInfo = info;
		}
		@Override
		public void run() {
			// 改为正在下载状
			mInfo.setDownState(STATE_DOWANLODING);
			//打开数据
			DownDao dao1 = new DownDao(JXJYApplication.getContext());
			// 更改数据库中的中的下载状
			dao1.updataInfos(STATE_DOWANLODING, mInfo.getId() + "",
					mInfo.getPlaypath());
			dao1.closeDb();
			// 通知UI去刷
			notifyStateChanged(mInfo);

			file = new File(mInfo.getSavepath());//保存的路
			System.out.println(mInfo.getSavepath());
			// 联网操作
			try {
				String urlPath=mInfo.getPlaypath();
//				String urlPath=mInfo.getPlaypath()+"?"+PlayCDNUrl.getDowmloadString(mInfo.getPlaypath(),JXJYApplication.getContext().getSharedPreferences(SPConstant.CONFIG_SP_NAME,JXJYApplication.getContext().MODE_PRIVATE).getString(SPConstant.CONFIG_SP_WS_KEY,null));
//				String urlPath = mInfo.getPlaypath();
				URL url = new URL(urlPath);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "*/*");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Accept-Charset", "utf-8");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				int code = conn.getResponseCode();
				if (code == 200) {
					// 下载文件的大
					int length = conn.getContentLength();
					mInfo.setVediolength(length);
					//打开数据
					DownDao dao2 = new DownDao(JXJYApplication.getContext());
					dao2.updataFileLength(length, mInfo.getId() + "",mInfo.getPlaypath());
					dao2.closeDb();
					notifyStateChanged(mInfo);
					//打开数据
					if(file.exists()){//文件存在
						long currentSize=file.length();
						if(currentSize>=length){
							//已经存在
							file.delete();
							startThreadDown(0, length);
						}else{
							mInfo.setCurrentposition(currentSize);
							notifyStateChanged(mInfo);
							startThreadDown((int)currentSize, length);
						}
					}else{
//						file.mkdirs();
//						SharedPreferences myPreferences = JXJYApplication.getContext().getSharedPreferences(SPConstant.LOGIN_SP_NAME,
//								Activity.MODE_PRIVATE);
						String username = "myDown".hashCode()+"";
						File down = new File(Environment.getExternalStorageDirectory(), "Download/"+username);
						down.mkdirs();
						startThreadDown(0, length);
					}
				} else {
					mInfo.setDownState(STATE_ERROR);
					//打开数据
					DownDao dao3 = new DownDao(JXJYApplication.getContext());
					dao3.updataInfos(STATE_ERROR, "" + mInfo.getId(),
							mInfo.getPlaypath());
					dao3.closeDb();
					notifyStateChanged(mInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
				notifyStateChanged(mInfo);
			}
		}

		/**
		 * 进行下载流的读写操作
		 * @param currentSize
		 * @param length
		 */
		protected void startThreadDown(final int currentSize, final int length) {
						
						try {
							String urlPath = mInfo.getPlaypath();
//							String urlPath = mInfo.getPlaypath()+"?"+ PlayCDNUrl.getDowmloadString(mInfo.getPlaypath(),JXJYApplication.getContext().getSharedPreferences(SPConstant.CONFIG_SP_NAME,JXJYApplication.getContext().MODE_PRIVATE).getString(SPConstant.CONFIG_SP_WS_KEY,null));
							Log.e("downPath=",urlPath);
							URL url = new URL(urlPath);
							HttpURLConnection conn = (HttpURLConnection) url
									.openConnection();
							conn.setRequestProperty("Accept", "*/*");
							conn.setRequestProperty("Connection", "Keep-Alive");
							conn.setRequestProperty("Accept-Charset", "utf-8");
							conn.setRequestMethod("GET");
							conn.setConnectTimeout(5000);
							conn.setReadTimeout(10000);
							// 添加标准的断点续传字段
							conn.setRequestProperty("Range", "bytes=" + currentSize
									+ "-" + length);

							int code = conn.getResponseCode();
							if (code == 206) {
								InputStream in = conn.getInputStream();
								FileOutputStream stream = new FileOutputStream(file,true);

								byte[] bs = new byte[16384];
								int len = 0;
								while ((len = in.read(bs)) != -1
										&& mInfo.getDownState() == STATE_DOWANLODING) {

//									if (file.exists()&&file.length()==0){
//										bs[0]=(byte) (bs[0]<<1);
//										bs[1]=(byte) (bs[1]<<1);
//										bs[2]=(byte) (bs[2]<<1);
//										bs[3]=(byte) (bs[3]<<1);
//										bs[4]=(byte) (bs[4]<<1);
//									}
									stream.write(bs, 0, len);
									stream.flush();
									mInfo.setCurrentposition(mInfo
											.getCurrentposition() + len);
									DownDao updataInfo=new DownDao(JXJYApplication.getContext());
									updataInfo.updataInfos(mInfo.getCurrentposition(),
											mInfo.getId() + "",mInfo.getPlaypath());
									updataInfo.closeDb();
									notifyProgressChanged(mInfo);
								}
								in.close();
								stream.close();
							} else {
								mInfo.setDownState(STATE_ERROR);
								DownDao updataErrorInfo=new DownDao(JXJYApplication.getContext());
								updataErrorInfo.updataInfos(STATE_ERROR, "" + mInfo.getId(),
										mInfo.getPlaypath());
								updataErrorInfo.closeDb();
								//更新UI
								notifyStateChanged(mInfo);
							}
							
							} catch (MalformedURLException e) {
//								e.printStackTrace();
								if (mInfo.getDownState() == STATE_DOWANLODING
										&&mInfo.getCurrentposition() == mInfo.getVediolength()
										&& file.exists()
										&& file.length() == mInfo.getCurrentposition()) {
									mInfo.setDownState(STATE_COMPULE);
									// 下载出现错误，更改数据库下载状(下载完成)
									DownDao daoError=new DownDao(JXJYApplication.getContext());
									daoError.updataInfos(STATE_COMPULE, "" + mInfo.getId(),
											mInfo.getPlaypath());
									daoError.closeDb();
									notifyStateChanged(mInfo);
								}else{
									mInfo.setDownState(STATE_ERROR);
									// 下载出现错误，更改数据库下载状
									DownDao updataErrorInfo1=new DownDao(JXJYApplication.getContext());
									updataErrorInfo1.updataInfos(STATE_ERROR, "" + mInfo.getId(),
											mInfo.getPlaypath());
									updataErrorInfo1.closeDb();
									// 更新UI
									notifyStateChanged(mInfo);
									
								}
							
							
							} catch (IOException e) {
//								e.printStackTrace();
							Log.e("e===",e.toString());
								//下载完成
								if (mInfo.getDownState() == STATE_DOWANLODING
										&&mInfo.getCurrentposition() == mInfo.getVediolength()
										&& file.exists()
										&& file.length() == mInfo.getCurrentposition()) {
									mInfo.setDownState(STATE_COMPULE);
									// 下载出现错误，更改数据库下载状(下载完成)
									DownDao daoError=new DownDao(JXJYApplication.getContext());
									daoError.updataInfos(STATE_COMPULE, "" + mInfo.getId(),
											mInfo.getPlaypath());
									daoError.closeDb();
									notifyStateChanged(mInfo);
								}else{
									mInfo.setDownState(STATE_ERROR);
									// 下载出现错误
									DownDao daoError=new DownDao(JXJYApplication.getContext());
									daoError.updataInfos(STATE_ERROR, "" + mInfo.getId(),
											mInfo.getPlaypath());
									daoError.closeDb();
									notifyStateChanged(mInfo);
								}
							}

			//下载完成
				if (mInfo.getDownState() == STATE_DOWANLODING
						&&mInfo.getCurrentposition() == mInfo.getVediolength()
						&& file.exists()
						&& file.length() == mInfo.getCurrentposition()
						) {

					Log.e("e===","=============");
				//发送下载完成的广播（主要用于通知其他界面的UI更新）
					Intent intent = new Intent("com.jwzt.jxjy.vedio.down.done");
					JXJYApplication.getContext().sendBroadcast(intent);
//					VideoRandomFile  fileTest=new VideoRandomFile(mInfo.getSavepath());
//					try {
//						fileTest.openFile();
//						fileTest.coding();
//						fileTest.closeFile();
//					} catch (Exception es) {
//						es.printStackTrace();
//					}
					mInfo.setDownState(STATE_COMPULE);
					// 下载出现错误，更改数据库下载状(下载完成)
					DownDao daoError=new DownDao(JXJYApplication.getContext());
					daoError.updataInfos(STATE_COMPULE, "" + mInfo.getId(),
							mInfo.getPlaypath());
					daoError.closeDb();
					notifyStateChanged(mInfo);

				} else if (mInfo.getDownState() == STATE_ERROR){
					notifyStateChanged(mInfo);
					notifyProgressChanged(mInfo);
				}

			}

	}

	/**
	 * 下载项进行注
	 * 
	 * @param obser
	 */
	public void registDownloadObser(DownloadObser obser) {
		synchronized (obsers) {
			if (!obsers.contains(obser)) {
				obsers.add(obser);
			}
		}
	}

	/**
	 * 下载项取消注
	 * 
	 * @param obser
	 */
	public void unregistDownloadObser(DownloadObser obser) {
		synchronized (obsers) {
			obsers.remove(obser);
		}
	}

	/**
	 * 下载状更新
	 * 
	 * @param info
	 */
	private void notifyStateChanged(VedioDownloadInfo info) {
		synchronized (obsers) {
			for (DownloadObser obser : obsers) {
				obser.onDownloadStateChanged(info);
			}
		}
	}

	/**
	 * 更新进度
	 * 
	 * @param info
	 */
	private void notifyProgressChanged(VedioDownloadInfo info) {
		synchronized (obsers) {
			for (DownloadObser obser : obsers) {
				obser.onDownloadProgressChanged(info);
			}
		}
	}

	/**
	 * 回调接口
	 * @author afnasdf
	 */
	public interface DownloadObser {
		public void onDownloadStateChanged(VedioDownloadInfo info);
		public void onDownloadProgressChanged(VedioDownloadInfo info);
	}




}
