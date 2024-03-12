package de.neiox.services.shedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class scheduleHandler {

    JobDetail job = JobBuilder.newJob()
            .withIdentity("myJob", "group1")
            .build();

    //Usage createJob("TUE-SAT", "8,16");
    public void createJob( String days, String hours) throws SchedulerException {

        String cronExpression = "0 0 " + hours + " ? * " + days + " *";

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();


        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
