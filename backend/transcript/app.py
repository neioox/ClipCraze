from datetime import timedelta
import os
from flask import Flask, jsonify
from flask_cors import CORS
from moviepy.editor import VideoFileClip, TextClip, CompositeVideoClip
from moviepy.video.tools.subtitles import SubtitlesClip
from moviepy.video.fx.all import crop
import stable_whisper
from langchain.llms import Ollama
from langchain.callbacks.manager import CallbackManager
from langchain.callbacks.streaming_stdout import StreamingStdOutCallbackHandler 


app = Flask(__name__)

CORS(app, resources={r"/*": {"origins": "*"}})


whisper_model = "large-v3"
ollama_model = "llama2-uncensored"

print("Loading ai models...")
print("Loading Whisper ai... model: "+ whisper_model)
model = stable_whisper.load_model(whisper_model)  # Loading whisper model
print("Whisper model loaded.")
print("loading ollama model: "+ ollama_model)

llm = Ollama(model= ollama_model, callback_manager= CallbackManager([StreamingStdOutCallbackHandler]))
print("ollama model loaded")
print("DONE!")


def success_response(message):
    return jsonify({"success": True, "message": message})


def error_response(message):
    return jsonify({"success": False, "error": message})


@app.route("/crop4tiktok/<filename>", methods=["POST"])
def crop4tiktok(filename):
    clips_path = "../Clips"
    input_file = os.path.join(clips_path, filename)

    try:
        clip = VideoFileClip(input_file)
        (w, h) = clip.size

        crop_width = h * 9 / 16
        x1, x2 = (w - crop_width) // 2, (w + crop_width) // 2
        y1, y2 = 0, h
        cropped_clip = crop(clip, x1=x1, y1=y1, x2=x2, y2=y2)

        output_file = os.path.join(clips_path, filename.replace(".mp4", "_4_tiktok.mp4"))
        cropped_clip.write_videofile(output_file)

        return success_response("Cropped video for TikTok")
    except Exception as e:
        return error_response(str(e))


@app.route("/addsubtitles2vid/<filename>", methods=["GET", "POST"])
def convertclip2tt(filename):
    clips_path = "../Clips"
    input_file = os.path.join(clips_path, filename)



    try:
        output_file = os.path.join(clips_path, filename.replace(".mp4", "_w_subs.mp4"))
        subtitle_file = os.path.join("SrtFiles", filename + ".srt")

        def split_text(txt):
            words = txt.split()
            lines = [' '.join(words[i:i+3]) for i in range(0, len(words), 3)]
            return '\n'.join(lines)

        generator = lambda txt: TextClip(split_text(txt), 
                                         font='komika',
                                         fontsize=40,
                                         color='white',
                                      
                                           )
        subtitles = SubtitlesClip(subtitle_file, generator)

        video = VideoFileClip(input_file)
    
        result = CompositeVideoClip([video, subtitles.set_pos(('center', 'center'))])

        result.write_videofile(output_file, codec="libx264", audio_codec="aac")

        return success_response("Video with subtitles created")
    except Exception as e:
        return error_response(str(e))

def transcribe_audio(path, name):
  
    results = model.transcribe(audio=path)

    stable_whisper.result_to_srt_vtt(result=results, segment_level= False , word_level= True, filepath =  os.path.join("SrtFiles", name + ".srt"))

    return "Subtitels generated"



@app.route("/generateText/<topic>", methods=["POST"])
def generateText(topic):
    prompt = """Given the following video topic, generate a catchy 
    and short TikTok description  you can use hashtags and emojis: 
    """ + topic

    response = llm(prompt)
   
    return jsonify({"success": True, "description": response})




@app.route("/genSubtitle/<filename>", methods=["POST"])
def generate_subtitle(filename):
    folder_path = "../Clips"
    file_path = os.path.join(folder_path, filename)

    if os.path.exists(file_path):
        try:
            print("File exists. Generating subtitles...")
            transcribe_audio(file_path, filename)
            print("Subtitles generated successfully.")
            return success_response("Subtitle generated")
        except Exception as e:
            print(f"Error generating subtitles: {e}")
            return error_response(str(e))

    else:
        print("File not found.")
        return error_response("File not found.")


if __name__ == "__main__":
    app.run(debug=False)
