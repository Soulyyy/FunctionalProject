package model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ResponseHolder {

	private static final DateTimeFormatter formatter = DateTimeFormatter
		.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
		.withZone(ZoneId.systemDefault());

	private final Instant begin;
	private final Instant end;
	private final Object data;

	public ResponseHolder(Instant begin, Object data) {
		this.begin = begin;
		this.end = Instant.now();
		this.data = data;
	}

	public String getBegin() {
		return formatter.format(begin);
	}

	public String getEnd() {
		return formatter.format(end);
	}

	public Object getData() {
		return data;
	}
}
