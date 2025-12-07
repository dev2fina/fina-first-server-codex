package net.fina.common.shared;

public class ReturnConstants {
	public final static int TABLETYPE_MULTIPLE = 1;
	public final static int TABLETYPE_NORMAL = 2;
	public final static int TABLETYPE_VARIABLE = 3;

	public final static int STATUS_CREATED = 1;
	public final static int STATUS_AMENDED = 2;
	public final static int STATUS_IMPORTED = 3;
	public final static int STATUS_PROCESSED = 4;
	public final static int STATUS_VALIDATED = 5;
	public final static int STATUS_RESETED = 6;
	public final static int STATUS_ACCEPTED = 7;
	public final static int STATUS_REJECTED = 8;
	public final static int STATUS_ERRORS = 9;
	public final static int STATUS_LOADED = 10;
	public final static int STATUS_QUEUED = 11;

	public final static String STATUS_CREATED_STR = "net.fina.returns.status.created";
	public final static String STATUS_AMENDED_STR = "net.fina.returns.status.amended";
	public final static String STATUS_IMPORTED_STR = "net.fina.returns.status.imported";
	public final static String STATUS_PROCESSED_STR = "net.fina.returns.status.processed";
	public final static String STATUS_VALIDATED_STR = "net.fina.returns.status.validated";
	public final static String STATUS_RESETED_STR = "net.fina.returns.status.reseted";
	public final static String STATUS_ACCEPTED_STR = "net.fina.returns.status.accepted";
	public final static String STATUS_REJECTED_STR = "net.fina.returns.status.rejected";
	public final static String STATUS_LOADED_STR = "net.fina.returns.status.loaded";
	public final static String STATUS_ERRORS_STR = "net.fina.returns.status.errors";
	public final static String STATUS_QUEUED_STR = "net.fina.returns.status.queued";

	public final static int EVAL_SUM = 1;
	public final static int EVAL_AVERAGE = 2;
	public final static int EVAL_MIN = 3;
	public final static int EVAL_MAX = 4;
	public final static int EVAL_EQUATION = 5;
}
