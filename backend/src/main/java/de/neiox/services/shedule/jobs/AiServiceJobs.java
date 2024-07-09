package de.neiox.services.shedule.jobs;

import de.neiox.services.AIService;
import de.neiox.services.VideoEditorHandler;
import de.neiox.services.getClips;
import org.quartz.*;

public class AiServiceJobs implements Job {

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
         //  aiService.convertclip2tt(clip);
         //  aiService.crop4tiktok(clip, "uncropped");
            VideoEditorHandler.convertClipToShortVid(clip);
        }catch (Exception e){


            throw new JobExecutionException(e);
        }
    }
}
