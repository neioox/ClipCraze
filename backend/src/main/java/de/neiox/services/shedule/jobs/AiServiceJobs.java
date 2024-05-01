package de.neiox.services.shedule.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import de.neiox.services.AIService;
import de.neiox.services.getClips;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Date;
import java.util.List;

public class AiServiceJobs implements Job {

    AIService aiService = new AIService();



    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {

            System.out.println("Starting JOB!!!");

            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();


            AIService aiService =  new AIService();


            String id = dataMap.getString("id");

            // Now you can use 'id' within your execute method
            System.out.println("The id is: " + id);

            getClips getClips = new getClips();
            getClips.requestClips(id);


           String clip =  getClips.getRandomClipFromUser(id);

           aiService.generate_subtitle(clip);
           aiService.convertclip2tt(clip);
           aiService.crop4tiktok(clip, "uncropped");

        }catch (Exception e){


            throw new JobExecutionException(e);
        }
    }
}
