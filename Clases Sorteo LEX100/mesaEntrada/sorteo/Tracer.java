package com.base100.lex100.mesaEntrada.sorteo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.seam.log.Log;
import org.jboss.seam.util.Strings;

public class Tracer {
	
	private static final int MAX_INFO_LENGTH = 120;
	private static final int MAX_LABEL_LENGTH = 22;


	private static final int MAX_TRACE_LENGTH = 132;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	private StringBuffer informacion = new StringBuffer();
	private Date startDate;
	private Log log;
	
	public Tracer() {
		startDate = new Date();
		informacion = new StringBuffer();
	}
	
	public Tracer(Log log) {
		this();
		setLog(log);
	}
	
	public void reset() {
		startDate = new Date();
		informacion = new StringBuffer();
	}

	public void trace(String label, String msg) {
		if (!Strings.isEmpty(label) || !Strings.isEmpty(msg) ) {
			if(label == null) {
				label = "";
			}
			if(msg == null) {
				msg = "";
			}
			if (log != null) {
				log.info(label +": "+ msg);
			}
			notify(label, msg);
		}
	}
	
	public void traceLn() {
		if (log != null) {
			log.info("");
		}
		notify(null, null);		
	}
	
	public String notify(String label, String msg){
		String text = "";
		int labelLength = 0;
		if(label != null && msg != null){
			StringBuffer sb = new StringBuffer();
			sb.append(simpleDateFormat.format(new Date()));
			sb.append("- ");		
			sb.append(padRight(label == null ? "" : label, 15));
			sb.append(" : ");
			labelLength = sb.length();
			sb.append(msg == null ? "" : msg.trim());
			text = sb.toString();
		}
		String margin = null;
		int isBlank;
		while (text.length() > MAX_TRACE_LENGTH) {
			int idx = text.lastIndexOf(' ', MAX_TRACE_LENGTH);
			if ((idx == 0) || (idx <= labelLength)) {
				idx = MAX_TRACE_LENGTH;
				isBlank = 0;
			} else {
				isBlank = 1;
			}
			informacion.append(text.substring(0, idx));
			informacion.append("\n");
			text = text.substring(idx+isBlank);
			text = padLeft(text, text.length() + labelLength);
		}
		informacion.append(text);
		informacion.append("\n");
		return text;
	}
	
	public void trace(String msg) {
		if (!Strings.isEmpty(msg) ) {
			if (log != null) {
				log.info(msg);
			}
			informacion.append(msg);
			informacion.append("\n");
		}
	}
	
	public static void addInfo(StringBuffer resultBuffer, String label, String info) {
		String text = "";
		int labelLength = 0;

		StringBuffer sb = new StringBuffer();
		sb.append(padRight(label == null ? "" : label, MAX_LABEL_LENGTH));
		sb.append(" : ");
		labelLength = sb.length();
		sb.append(info == null ? "" : info);
		text = sb.toString();

		while (text.length() > MAX_INFO_LENGTH) {
			int idx = text.lastIndexOf(' ', MAX_INFO_LENGTH);
			if (idx > 0) {
				resultBuffer.append(text.substring(0, idx));
				resultBuffer.append("\n");
				text = text.substring(idx + 1);
				text = padLeft(text, text.length() + labelLength);
			} else {
				break;
			}
		}
		resultBuffer.append(text);
		resultBuffer.append("\n");
	}

	public static String padRight(String s, int n) {
		if (s != null) {
			return String.format("%-" + n + "s", s);
		}
		return null;
	}

	public static String padLeft(String s, int n) {
		if (s != null) {
			return String.format("%" + n + "s", s);
		}
		return null;
	}

	public String getInformacion() {
		return informacion.toString();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}

	public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
		this.simpleDateFormat = simpleDateFormat;
	}

}
