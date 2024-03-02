import cv2


def get_face_position(video_path):
    # Open the video file
    cap = cv2.VideoCapture(video_path)

    # Check if the video opened successfully
    if not cap.isOpened():
        print("Error: Could not open video.")
        return

    # Read the first frame from the video
    ret, frame = cap.read()

    # Convert the frame to grayscale for face detection
    gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Load the pre-trained face detection model
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    # Detect faces in the first frame
    faces = face_cascade.detectMultiScale(gray_frame, scaleFactor=1.3, minNeighbors=5)

    if len(faces) > 0:
        # Get the position and size of the first detected face
        x, y, w, h = faces[0]

        # Print the position and size
        print("Face Position (x, y):", x, y)
        print("Face Size (width, height):", w, h)

        # Draw a rectangle around the detected face on the first frame
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

        # Display the first frame with the detected face
        cv2.imshow('Detected Face', frame)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        print("No faces detected in the first frame.")

    # Release the video capture object
    cap.release()
