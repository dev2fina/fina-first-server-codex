package net.fina.common.shared;

import net.fina.common.client.returns.ScheduleModel;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/*
 *
 * Created by oto on 8/2/16.
 */
public class NotSubmitedReturnModel extends ScheduleModel implements Serializable {

    private Map<String, Set<String>> fiReturnCodes;

    private Map<String, Map<Long, Set<String>>> fiPeriodReturnCodes;

    public NotSubmitedReturnModel() {
        super();
    }

    public Map<String, Set<String>> getFiReturnCodes() {
        return fiReturnCodes;
    }

    public void setFiReturnCodes(Map<String, Set<String>> fiReturnCodes) {
        this.fiReturnCodes = fiReturnCodes;
    }

    public Map<String, Map<Long, Set<String>>> getFiPeriodReturnCodes() {
        return fiPeriodReturnCodes;
    }

    public void setFiPeriodReturnCodes(Map<String, Map<Long, Set<String>>> fiPeriodReturnCodes) {
        this.fiPeriodReturnCodes = fiPeriodReturnCodes;
    }
}
