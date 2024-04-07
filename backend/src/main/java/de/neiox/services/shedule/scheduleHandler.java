package de.neiox.services.shedule;

import de.neiox.services.shedule.jobs.AiServiceJobs;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class scheduleHandler {

    JobDetail job = JobBuilder.newJob(AiServiceJobs.class)
            .withIdentity("myJob", "group1")
            .build();

    public void createJob(String[] daysOfWeek, String hours, String id) throws SchedulerException {

        StringBuilder days = new StringBuilder();
        for (String day : daysOfWeek) {
            days.append(day).append(",");
        }
        // Remove the last comma
        days.deleteCharAt(days.length() - 1);

        String cronExpression = "0 0 " + hours + " ? * " + days + " *";

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();


        job.getJobDataMap().put("id", id);

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

}
