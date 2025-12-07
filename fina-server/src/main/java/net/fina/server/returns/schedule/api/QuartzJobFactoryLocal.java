package net.fina.server.returns.schedule.api;

import org.quartz.JobDetail;

public interface QuartzJobFactoryLocal {

    public JobDetail getJob(String identity, String group);

}
