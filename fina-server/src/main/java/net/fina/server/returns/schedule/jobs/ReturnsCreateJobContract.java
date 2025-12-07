package net.fina.server.returns.schedule.jobs;

import net.fina.common.shared.schedule.JobDataConstants;
import org.quartz.Job;

public interface ReturnsCreateJobContract extends JobDataConstants, Job {

    public static final String JOB_NAME = "CREATE-RETURNS";
    public static final String SCHEDULED_TASK_LOCAL = "SCHEDULER_LOCAL";
    public static final String RETURN_VERSION_LOCAL = "RETURN_VERSION_LOCAL";
    public static final String USER_LOCAL = "USER_LOCAL";
    public static final String LANGUAGE_LOCAL = "LANGUAGE_LOCAL";
    public static final String PROPERTY_LOCAL = "PROPERTY_LOCAL";
    public static final String RETURN_DEFINITION_LOCAL = "RETURN_DEFINITION_LOCAL";
    public static final String RETURN_LOCAL = "RETURN_LOCAL";
    public static final String SCHEDULE_LOCAL = "SCHEDULE_LOCAL";
    public static final String PROCESSING_LOCAL = "PROCESSING_LOCAL";
    public static final String MDT_NODE_LOCAL = "MDT_LOCAL";

}
