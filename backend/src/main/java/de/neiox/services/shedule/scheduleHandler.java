package de.neiox.services.shedule;

import de.neiox.services.shedule.jobs.AiServiceJobs;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScheduleHandler {
    Scheduler scheduler = new StdSchedulerFactory().getScheduler();

    public ScheduleHandler() throws SchedulerException {
    }

    public void getCurrentJobs() throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                //get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();
            }
        }
    }

    public void createJob(String daysOfWeek, String hours, String id) throws SchedulerException {
        scheduler.start();

        String[] daysArray = daysOfWeek.split(", ");
        String[] hoursArray = hours.split(", ");

        for (String day : daysArray) {
            for (String hour : hoursArray) {
                String[] timeParts = hour.split(":");
                String cronHour = timeParts[1] + " " + timeParts[0];
                String cronExpression = "0 " + cronHour + " ? * " + day.toUpperCase() + " *";

                // Construct unique identifiers using both day and time
                String formattedDayTime = day.toUpperCase() + hour.replace(":", "");
                String triggerIdentity = id + formattedDayTime;
                TriggerKey triggerKey = new TriggerKey(triggerIdentity, "group1");

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();

                JobDetail job = JobBuilder.newJob(AiServiceJobs.class)
                        .withIdentity(id + formattedDayTime, "group1")
                        .build();

                System.out.println("CREATING JOB WITH " + cronExpression);
                job.getJobDataMap().put("id", id);
                scheduler.scheduleJob(job, trigger);
            }
        }
    }



    public boolean deleteJob(String jobId) throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        JobKey jobKey = new JobKey(jobId, "group1");
        return scheduler.deleteJob(jobKey);
    }
}
