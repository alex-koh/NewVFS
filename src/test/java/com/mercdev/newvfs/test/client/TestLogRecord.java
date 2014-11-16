package com.mercdev.newvfs.test.client;

import java.util.logging.Handler;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static org.mockito.Mockito.mock;


public class TestLogRecord extends LogRecord{
	public final static String logName = "com.mercdev.newvfs.test.client.logger";
	private static Logger logger;
	public static Handler initLoggergetHandler() {
		logger = Logger.getLogger(TestLogRecord.logName);
		// Неработающий обработчик
		Handler handler = mock(Handler.class);
		logger.addHandler(handler);
		// Отменить использование родительского обработчика, 
		// чтобы блокировать печать на экран
		logger.setUseParentHandlers(true);
		return handler;
	}
	public TestLogRecord(Level level, String message) {
		super(level, message);
	}
	public TestLogRecord(Level level, String message, Object[] params) {
		super(level, message);
		setParameters(params);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogRecord) {
			LogRecord record = (LogRecord) obj;
			return	(getLevel()==record.getLevel())&&
					(getMessage()==record.getMessage())&&
					(Arrays.deepEquals(getParameters(),record.getParameters()));
		}
		return false;
	}
	@Override
	public String toString() {
		SimpleFormatter f = new SimpleFormatter();
		return f.format(this);
	}
}
