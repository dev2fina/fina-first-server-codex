package net.fina.server.returns.schedule.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.schedule.api.QuartzJobFactoryLocal;
import net.fina.server.returns.schedule.api.ReturnsScheduleLocal;
import net.fina.server.returns.schedule.jobs.ReturnsCreateJob;
import net.fina.server.returns.schedule.jobs.ReturnsCreateJobContract;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

@Stateless
@Local(QuartzJobFactoryLocal.class)
@Interceptors(RecordingAuditor.class)
public class QuartzJobFactorySession implements QuartzJobFactoryLocal {

    @EJB
    ReturnsScheduleLocal schedulerLocal;

    @EJB
    ReturnVersionLocal returnVersionLocal;

    @EJB
    UserLocal userLocal;

    @EJB
    LanguageLocal languageLocal;

    @EJB
    PropertyLocal propertyLocal;

    @EJB
    ReturnDefinitionLocal returnDefinitionLocal;

    @EJB
    ReturnLocal returnLocal;

    @EJB
    ScheduleLocal scheduleLocal;

    @EJB
    ProcessingLocal processingLocal;

    @EJB
    MDTNodeLocal mdtNodeLocal;

    @Override
    public JobDetail getJob(String identity, String group) {
        JobBuilder jobBuilder = null;

        //Creating job and passing pre-build params

        jobBuilder = JobBuilder.newJob(ReturnsCreateJob.class);

        jobBuilder = jobBuilder.withIdentity(identity, group);

        JobDetail jobDetail = jobBuilder.build();

        //Passing post-build params
        JobDataMap jdm = jobDetail.getJobDataMap();
        jdm.put(ReturnsCreateJobContract.SCHEDULED_TASK_LOCAL, schedulerLocal);
        jdm.put(ReturnsCreateJobContract.RETURN_VERSION_LOCAL, returnVersionLocal);
        jdm.put(ReturnsCreateJobContract.USER_LOCAL, userLocal);
        jdm.put(ReturnsCreateJobContract.LANGUAGE_LOCAL, languageLocal);
        jdm.put(ReturnsCreateJobContract.PROPERTY_LOCAL, propertyLocal);
        jdm.put(ReturnsCreateJobContract.RETURN_DEFINITION_LOCAL, returnDefinitionLocal);
        jdm.put(ReturnsCreateJobContract.RETURN_LOCAL, returnLocal);
        jdm.put(ReturnsCreateJobContract.SCHEDULE_LOCAL, scheduleLocal);
        jdm.put(ReturnsCreateJobContract.PROCESSING_LOCAL, processingLocal);
        jdm.put(ReturnsCreateJobContract.MDT_NODE_LOCAL, mdtNodeLocal);

        return jobDetail;
    }

}
