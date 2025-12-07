package net.fina.common.client.returns;

import java.util.Arrays;

public enum ProcessStatus {
    /*0 */ STATUS_SCHEDULED("net.fina.returns.status.scheduled"),
    /*1 */ STATUS_CREATED("net.fina.returns.status.created"),
    /*2 */ STATUS_AMENDED("net.fina.returns.status.amended"),
    /*3 */ STATUS_IMPORTED("net.fina.returns.status.imported"),
    /*4 */ STATUS_PROCESSED("net.fina.returns.status.processed"),
    /*5 */ STATUS_VALIDATED("net.fina.returns.status.validated"),
    /*6 */ STATUS_RESETED("net.fina.returns.status.reseted"),
    /*7 */ STATUS_ACCEPTED("net.fina.returns.status.accepted"),
    /*8 */ STATUS_REJECTED("net.fina.returns.status.rejected"),
    /*9 */ STATUS_ERRORS("net.fina.returns.status.errors"),
    /*10*/ STATUS_LOADED("net.fina.returns.status.loaded"),
    /*11*/ STATUS_QUEUED("net.fina.returns.status.queued");

    private final String code;

    private ProcessStatus(String code) {
        this.code = code;
    }

    public static final boolean canChangeStatus(ProcessStatus oldStatus, ProcessStatus newStatus) {
        boolean can = true;
        switch (newStatus) {
            case STATUS_ACCEPTED:
                switch (oldStatus) {
                    case STATUS_CREATED:
                    case STATUS_AMENDED:
                    case STATUS_IMPORTED:
                    case STATUS_VALIDATED:
                    case STATUS_RESETED:
                    case STATUS_ACCEPTED:
                    case STATUS_REJECTED:
                    case STATUS_ERRORS:
                    case STATUS_LOADED:
                        can = false;
                        break;
                }
                break;
            case STATUS_REJECTED:
                switch (oldStatus) {
                    case STATUS_CREATED:
                    case STATUS_IMPORTED:
                    case STATUS_VALIDATED:
                    case STATUS_RESETED:
                    case STATUS_ACCEPTED:
                    case STATUS_REJECTED:
                    case STATUS_ERRORS:
                    case STATUS_LOADED:
                        can = false;
                        break;
                }
                break;
            case STATUS_RESETED:
                switch (oldStatus) {
                    case STATUS_CREATED:
                    case STATUS_AMENDED:
                    case STATUS_IMPORTED:
                    case STATUS_PROCESSED:
                    case STATUS_RESETED:
                    case STATUS_ERRORS:
                    case STATUS_LOADED:
                        can = false;
                        break;
                }
                break;
            case STATUS_PROCESSED: {
                switch (oldStatus) {
                    case STATUS_CREATED:
                    case STATUS_IMPORTED:
                    case STATUS_VALIDATED:
                    case STATUS_RESETED:
                    case STATUS_ACCEPTED:
                    case STATUS_REJECTED:
                    case STATUS_ERRORS:
                    case STATUS_LOADED:
                        can = false;
                        break;
                }
                break;
            }
            case STATUS_AMENDED: {
                switch (oldStatus) {
                    case STATUS_VALIDATED:
                    case STATUS_ACCEPTED:
                        can = false;
                        break;
                }
            }
        }
        return can;
    }

    public static final boolean canDelete(ProcessStatus status) {
        return !Arrays.asList(
                ProcessStatus.STATUS_ACCEPTED,
                ProcessStatus.STATUS_VALIDATED
        ).contains(status);
    }

    public String getCode() {
        return code;
    }
}
