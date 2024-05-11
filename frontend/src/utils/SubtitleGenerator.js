class SubtitleGenerator {
    constructor() {
        this.subsExist = false;
        this.responses = [];
        this.isLoading = false;
        this.loadingState = 'starting!';
    }

    async generateSubtitles(event, name) {
        let subtitlesExist = false;
        let attempts = 0;
        const maxAttempts = 15;
   
        while (!subtitlesExist && attempts < maxAttempts) {
            try {
                this.isLoading = true;
                console.log(event);
                let clipName = event.target.dataset.name || name;
                console.log(clipName);

                this.loadingState = "Checking if subtitles exist...";

                let subtitlesExistsResponse = await fetch(`http://localhost:8080/api/checksubtitle/${clipName}`);
                let subtitlesExists = await subtitlesExistsResponse.json();

                this.subsExist = subtitlesExists.Exists;
                this.responses.push(subtitlesExists);
                console.log("test");
                console.log(this.subsExist);

                if (!this.subsExist) {
                    this.loadingState = "Generating Subtitles with AI...";
                    const response = await fetch(`http://localhost:8080/api/genSubtitle/${clipName}`, { method: "POST" });
                    this.responses.push(await response.json());
                } else {
                    subtitlesExist = true;
                  /*  this.loadingState = "Adding the Subtitles to the video...";
                    const formattingConvertResponse = await fetch(`http://localhost:8080/api/addsubtitles2vid/${clipName}`, { method: "POST" });
                    console.log(formattingConvertResponse)
                    this.responses.push(await formattingConvertResponse.json());
                    */
                   
                    this.loadingState = "Cropping it for TikTok...";
                    const Convert4Tiktok = await fetch(`http://localhost:8080/api/convert4tiktok/v2/${clipName}`, { method: "POST" });
                    this.responses.push(await Convert4Tiktok.json());

                    this.loadingState = "Done!";
                    this.isLoading = false;
                }
            } catch (error) {
                console.error("Error generating subtitles:", error);
            }

            attempts++;
            if (!subtitlesExist) {
                await new Promise(resolve => setTimeout(resolve, 1000));
            }
        }

        if (attempts === maxAttempts) {
            console.error("Max attempts reached. Subtitles could not be generated.");
            this.loadingState = "Max attempts reached. Subtitles could not be generated.";
        }
    }
}

export default SubtitleGenerator;
