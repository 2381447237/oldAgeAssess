package com.youli.oldageassess.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.youli.oldageassess.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class UpdateManager {

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	private static final int MsgTrue = 3;
	Dialog noticeDialog;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	int versionCode;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				// 正在下载
				case DOWNLOAD:
					// 设置进度条位置
					mProgress.setProgress(progress);
					break;
				case DOWNLOAD_FINISH:
					// 安装文件
					installApk();
					break;
				case MsgTrue:

					if (null != mHashMap) {
						int serviceCode = Integer.valueOf(mHashMap.get("version"));
						Log.i("2016-11-28", "serviceCode==" + serviceCode);
						// 版本判断
						if (serviceCode > versionCode) {

							showNoticeDialog();

						} else {
							Toast.makeText(mContext, R.string.soft_update_no,
									Toast.LENGTH_LONG).show();
						}
					}

					break;
				default:
					break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {

		// if (isUpdate()) {
		// // 显示提示对话框
		// showNoticeDialog();
		// } else {
		// Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG)
		// .show();
		// }
		isUpdate();
	}

	/**
	 * 检查软件是否有更新版本
	 *
	 * @return
	 */
	private void isUpdate() {
		// 获取当前软件版本
		versionCode = getVersionCode(mContext);
		System.out.println("versionCode===============" + versionCode);

		new Thread(new Runnable() {
			public void run() {

				URL url;// 定义网络中version.xml的连接
				try {
					url = new URL(MyOkHttpUtils.BaseUrl
							+ "/version.xml");
					// url = new
					// URL("http://www.fcxx.net.cn/images/version.xml");//创建version.xml的连接地址。
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					connection.connect();
					InputStream inStream = connection.getInputStream();// 从输入流获取数据
					ParseXmlService service = new ParseXmlService();// 将数据通过ParseXmlService这个类解析
					mHashMap = service.parseXml(inStream);// 得到解析信息

					Message msgT = Message.obtain();

					msgT.what = MsgTrue;

					mHandler.sendMessage(msgT);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		builder.setCancelable(false);
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		// noticeDialog.dismiss();
		// 构造软件下载对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		builder.setCancelable(false);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.activity_load, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		mProgress.setVisibility(View.VISIBLE);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// 设置取消状态
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 下载文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件

		// new downloadApkThread().start();

		new Thread(new Runnable() {
			public void run() {
				try {
					// 判断SD卡是否存在，并且是否具有读写权限
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// 获得存储卡的路径
						String sdpath = Environment
								.getExternalStorageDirectory() + "/";
						mSavePath = sdpath + "download";
						URL url = new URL(mHashMap.get("url"));

						// 创建连接
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.connect();
						// 获取文件大小
						int length = conn.getContentLength();

						// 创建输入流
						InputStream is = conn.getInputStream();
						File file = new File(mSavePath);
						// 判断文件目录是否存在
						if (!file.exists()) {
							file.mkdir();
						}
						File apkFile = new File(mSavePath, mHashMap.get("name"));
						FileOutputStream fos = new FileOutputStream(apkFile);
						int count = 0;
						// 缓存
						byte buf[] = new byte[1024];
						// 写入到文件中
						do {
							int numread = is.read(buf);
							count += numread;

							// 计算进度条位置
							progress = (int) (((float) count / length) * 100);

							// 更新进度
							Message msg = Message.obtain();

							msg.what = DOWNLOAD;

							mHandler.sendMessage(msg);
							// mHandler.sendEmptyMessage(DOWNLOAD);
							if (numread <= 0) {
								// 下载完成
								Message msgF = Message.obtain();

								msgF.what = DOWNLOAD_FINISH;

								mHandler.sendMessage(msgF);
								// mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
								cancelUpdate = true;
								break;
							}
							// 写入文件
							fos.write(buf, 0, numread);
						} while (!cancelUpdate);// 点击取消就停止下载.
						fos.close();
						is.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				// 取消下载对话框显示
				// mDownloadDialog.dismiss();
			}
		}

		).start();

	}

	/**
	 * 下载文件线程
	 *
	 * @author clf
	 * @date 2013-5-7
	 *
	 */
	// private class downloadApkThread extends Thread {
	// @Override
	// public void run() {
	// try {
	// // 判断SD卡是否存在，并且是否具有读写权限
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// // 获得存储卡的路径
	// String sdpath = Environment.getExternalStorageDirectory()
	// + "/";
	// mSavePath = sdpath + "download";
	// URL url = new URL(mHashMap.get("url"));
	// // 创建连接
	// HttpURLConnection conn = (HttpURLConnection) url
	// .openConnection();
	// conn.connect();
	// // 获取文件大小
	// int length = conn.getContentLength();
	// // 创建输入流
	// InputStream is = conn.getInputStream();
	// File file = new File(mSavePath);
	// // 判断文件目录是否存在
	// if (!file.exists()) {
	// file.mkdir();
	// }
	// File apkFile = new File(mSavePath, mHashMap.get("name"));
	// FileOutputStream fos = new FileOutputStream(apkFile);
	// int count = 0;
	// // 缓存
	// byte buf[] = new byte[1024];
	// // 写入到文件中
	// do {
	// int numread = is.read(buf);
	// count += numread;
	// // 计算进度条位置
	// progress = (int) (((float) count / length) * 100);
	// // 更新进度
	// Message msg=Message.obtain();
	//
	// msg.what=DOWNLOAD;
	//
	// mHandler.sendMessage(msg);
	// //mHandler.sendEmptyMessage(DOWNLOAD);
	// if (numread <= 0) {
	// // 下载完成
	// Message msgF=Message.obtain();
	//
	// msgF.what=DOWNLOAD_FINISH;
	//
	// mHandler.sendMessage(msgF);
	// //mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
	// cancelUpdate = true;
	// break;
	// }
	// // 写入文件
	// fos.write(buf, 0, numread);
	// } while (!cancelUpdate);// 点击取消就停止下载.
	// fos.close();
	// is.close();
	// }
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // 取消下载对话框显示
	// //mDownloadDialog.dismiss();
	// }
	// };

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
		// 取消下载对话框显示
		mDownloadDialog.dismiss();
	}
}