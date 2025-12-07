package net.fina.server.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 7/6/13
 * Time: 3:20 PM
 */
public interface ResultFilter<T, P> {
    List<T> filter(List<T> result, P p);
}
