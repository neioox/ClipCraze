from datetime import timedelta
import os
from flask import Flask, jsonify
from flask_cors import CORS
from moviepy.editor import VideoFileClip, TextClip, CompositeVideoClip
from moviepy.video.tools.subtitles import SubtitlesClip
from moviepy.video.fx.all import crop
from moviepy.editor import *
import stable_whisper
from langchain.llms import Ollama
from langchain.callbacks.manager import CallbackManager
from langchain.callbacks.streaming_stdout import StreamingStdOutCallbackHandler 
import logging
import chardet
from modules.faceDetection import get_face_position


from skimage.filters import gaussian
app = Flask(__name__)

CORS(app, resources={r"/*": {"origins": "*"}})



whisper_model = "medium"
ollama_model = "llama2-uncensored"

print("Loading ai models...")
print("Loading Whisper ai... model: "+ whisper_model)
model =  stable_whisper.load_model(whisper_model)
print("Whisper model loaded.")
print("loading ollama model: "+ ollama_model)

llm = Ollama(model= ollama_model, callback_manager= CallbackManager([StreamingStdOutCallbackHandler]))
print("ollama model loaded")
print("DONE!")
logging.basicConfig(filename='app.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def success_response(message):
    return jsonify({"success": True, "message": message})


def error_response(message):
    return jsonify({"success": False, "error": message})

def blur(image):
    """ Returns a blurred (radius=2 pixels) version of the image """
    return gaussian(image.astype(float), sigma=5)


@app.route("/crop4tiktok/<filename>/<format>", methods=["POST"])
def crop4tiktok(filename, format):
    print("Entering crop4tiktok function")
    clips_path = "../Clips"
    input_file = os.path.join(clips_path, filename)
    print(f"Input file: {input_file}")
    
    if format == "cropped":
        print("Format is cropped")
        try:

            clip = VideoFileClip(input_file)
            print("Clip created")
            (w, h) = clip.size
            print(f"Clip size: {w}x{h}")
            crop_width = h * 9 / 16
            x1, x2 = (w - crop_width) // 2, (w + crop_width) // 2
            y1, y2 = 0, h
            print(f"Crop dimensions: x1={x1}, y1={y1}, x2={x2}, y2={y2}")
            cropped_clip = crop(clip, x1=x1, y1=y1, x2=x2, y2=y2)
            print("Clip cropped")

            output_file = os.path.join(clips_path, filename.replace(".mp4", "_4_tiktok.mp4"))
            print(f"Output file: {output_file}")
            cropped_clip.write_videofile(output_file)
            print("Cropped video written")

            return success_response("Cropped video for TikTok")
        
        except Exception as e:
            print(f"Exception occurred: {str(e)}")
            return error_response(str(e))
    
    elif format == "uncropped":
        print("Format is uncropped")
        try:
            clip = VideoFileClip(input_file)
            repositiond_clip = clip.resize(0.35)
            
            
            print("Clip created")
            (w, h) = clip.size
            print(f"Clip size: {w}x{h}")
            crop_width = h * 9 / 16
            x1, x2 = (w - crop_width) // 2, (w + crop_width) // 2
            y1, y2 = 0, h
            print(f"Crop dimensions: x1={x1}, y1={y1}, x2={x2}, y2={y2}")
           
       
            print("Clip cropped")
            
            blurred_clip = clip.fl_image(blur)  # Adjust the blur intensity as needed
            print("Clip blurred")
            blurred_clip = crop(blurred_clip, x1=x1, y1=y1, x2=x2, y2=y2)  
            print("Blurred clip resized")

            final_clip = CompositeVideoClip([blurred_clip, repositiond_clip.set_position("center")])
            print("Final clip composed")

            output_file = os.path.join(clips_path, filename.replace(".mp4", "_4_tiktok_uncropped.mp4"))
            print(f"Output file: {output_file}")
            final_clip.write_videofile(output_file)
            print("Uncropped video written")

            return success_response("Uncropped video for TikTok")
        except Exception as e: 
            print(f"Exception occurred: {str(e)}")
            return error_response(str(e))
    
        


@app.route("/addsubtitles2vid/<filename>", methods=["GET", "POST"])
def convertclip2tt(filename):
    clips_path = "../Clips"
    input_file = os.path.join(clips_path, filename)

    try:
        output_file = os.path.join(clips_path, filename.replace(".mp4", "_w_subs.mp4"))
        subtitle_file = os.path.join("SrtFiles", filename + ".srt")


        def split_text(txt):
            print(txt)
            words = txt.split()
            lines = [' '.join(words[i:i+3]) for i in range(0, len(words), 3)]
            return '\n'.join(lines)

        generator = lambda txt: TextClip(split_text(txt),
                                         font='komika',
                                         fontsize=40,
                                         color='white')

        subtitles = SubtitlesClip(subtitle_file, generator)

        video = VideoFileClip(input_file)
        result = CompositeVideoClip([video, subtitles.set_pos(('center', 'bottom'))])

        result.write_videofile(output_file, codec="libx264", audio_codec="aac")

        return jsonify({"success": True, "message": "Video with subtitles created"})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)})

def transcribe_audio(path, name):
    logger.info('Transcription started for file: %s', path)
    
    results = model.transcribe(audio=path)

    if not path or not name or not os.path.isfile(path):
        logger.error('Invalid path or name')
        return make_response("Invalid path or name", 400)
    
    test =  stable_whisper.result_to_srt_vtt(result=results, segment_level=False, word_level=True, filepath=os.path.join("SrtFiles", name + ".srt"))
    logger.info('Subtitles generated: %s', test)
    print(test)

    return make_response("Subtitles generated", 200)







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
    logger.info(f"File path: {file_path}")

    if os.path.exists(file_path):
        try:
            #get_face_position(file_path)
            logger.info("File exists. Generating subtitles...")
            transcribe_audio(file_path, filename)
            logger.info("Subtitles generated successfully.")
            return success_response("Subtitle generated")
        except Exception as e:
            logger.error(f"Error generating subtitles: {e}")
            return error_response(str(e))

    else:
        logger.error("File not found.")
        return error_response("File not found.")


if __name__ == "__main__":
    app.run(debug=False)
