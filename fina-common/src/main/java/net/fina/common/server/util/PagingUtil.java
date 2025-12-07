package net.fina.common.server.util;

public class PagingUtil {
    //TODO Use THis globally
    public static int getOffsetFromPage(int page, int limit) {
        return page > 0 ? ((page - 1) * limit) : 0;
    }
}
