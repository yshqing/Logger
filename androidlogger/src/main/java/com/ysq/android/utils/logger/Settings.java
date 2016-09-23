package com.ysq.android.utils.logger;

import java.io.File;

public final class Settings {

	private int methodCount = 2;
	private boolean showThreadInfo = true;
	private int methodOffset = 0;
	private LogTool logTool;
	private File errorLogSaveFile;

	/**
	 * Determines how logs will printed
	 */
	private LogLevel logLevel = LogLevel.FULL;

	public Settings hideThreadInfo() {
		showThreadInfo = false;
		return this;
	}

	public Settings methodCount(int methodCount) {
		if (methodCount < 0) {
			methodCount = 0;
		}
		this.methodCount = methodCount;
		return this;
	}

	public Settings logLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public Settings methodOffset(int offset) {
		this.methodOffset = offset;
		return this;
	}

	public Settings errorLogFile(File file) {
		this.errorLogSaveFile = file;
		return this;
	}

	public Settings logTool(LogTool logTool) {
		this.logTool = logTool;
		return this;
	}

	public int getMethodCount() {
		return methodCount;
	}

	public boolean isShowThreadInfo() {
		return showThreadInfo;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public int getMethodOffset() {
		return methodOffset;
	}

	public LogTool getLogTool() {
		if (logTool == null) {
			logTool = new AndroidLogTool();
		}
		return logTool;
	}

	public File getErrorLogSaveFile() {
		return errorLogSaveFile;
	}
}
