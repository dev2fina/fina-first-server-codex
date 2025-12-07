package net.fina.server.processing;

import java.util.Date;

public interface JSTreeDateProcessor {
    double periodValue(String nodeCode, long fiId, long versionId, Date fromDate, Date toDate);
}
