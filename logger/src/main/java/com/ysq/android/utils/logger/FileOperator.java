package com.ysq.android.utils.logger;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileOperator {

	private File fileDir;
	private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static FileOperator mSelf;

	private FileOperator() {

	}

	public static synchronized FileOperator getInstance(File fileDir) {
		if (mSelf == null) {
			mSelf = new FileOperator();
		}
		mSelf.fileDir = fileDir;
		return mSelf;
	}

	public void saveToFile(Throwable ex, String message, Object... args) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Calendar calendar = Calendar.getInstance();

			StringBuffer sb = new StringBuffer();
			sb.append(getStartInfo());
			sb.append(args.length == 0 ? message : String.format(message, args));
			if (ex != null) {
				Writer writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter(writer);
				ex.printStackTrace(printWriter);
				Throwable cause = ex.getCause();
				while (cause != null) {
					cause.printStackTrace(printWriter);
					cause = cause.getCause();
				}
				printWriter.close();
				String result = writer.toString();
				sb.append(result);
			}
			sb.append(getEndInfo());
			File dir = fileDir;
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileWriter fWriter = null;
			try {
				fWriter = new FileWriter(fileDir + "/" + getFileNameByCalendar(calendar), true);
				fWriter.write(sb.toString());
				fWriter.flush();
				fWriter.close();
				deleteOldFile(calendar);
			} catch (IOException e) {
				e.printStackTrace();
				if (fWriter != null) {
					try {
						fWriter.flush();
						fWriter.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private String getStartInfo() {
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar.getTimeInMillis());
		long timestamp = calendar.getTimeInMillis();
		String time = mFormatter.format(date);
		sb.append("本次错误开始\n");
		sb.append(time).append("_").append(timestamp).append("\n");
		return sb.toString();
	}

	private String getEndInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("本次错误结束").append("\n\n");
		return sb.toString();
	}

	/**
	 * 删除给定日期的七天(含)前的及以后的log
	 * @param todayCalendar 所以日期
	 */
	private void deleteOldFile(Calendar todayCalendar) {
		File[] files = fileDir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				Calendar oldCalendar = (Calendar) todayCalendar.clone();
				oldCalendar.set(Calendar.DAY_OF_MONTH, oldCalendar.get(Calendar.DAY_OF_MONTH) - 1);
				if (file.isFile() && file.exists() && !file.getName().equals(getFileNameByCalendar(todayCalendar))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -1)))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -2)))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -3)))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -4)))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -5)))
						&& !file.getName().equals(getFileNameByCalendar(getCalendarByCorrection(todayCalendar, -6)))) {
					try {
						file.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 获取所给日期的修正后的日期
	 * @param calendar 原始日期
	 * @param correctionDay 修正值,单位为天
	 * @return 修正后的日期
	 */
	private Calendar getCalendarByCorrection(Calendar calendar, int correctionDay) {
		Calendar oldCalendar = (Calendar) calendar.clone();
		oldCalendar.set(Calendar.DAY_OF_MONTH, oldCalendar.get(Calendar.DAY_OF_MONTH) + correctionDay);
		return oldCalendar;
	}

	/**
	 * 根据日期获取文件名
	 * @param calendar 日期
	 * @return 文件名
	 */
	private String getFileNameByCalendar(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		Date date = new Date(calendar.getTimeInMillis());
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "y_log" + "_" + formatter.format(date) + ".txt";
		return fileName;
	}
}
