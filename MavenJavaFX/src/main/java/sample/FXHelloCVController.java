package sample;

import javafx.event.Event;
import javafx.scene.control.CheckBox;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import  sample.Utils.Utils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class FXHelloCVController {

    @FXML
    private Button cameraButton;
    @FXML
    private ImageView originalFrame;
    @FXML
    private CheckBox haarClassifier;

    private ScheduledExecutorService timer;
    private VideoCapture capture;
    private boolean cameraActive;

    private CascadeClassifier faceCascade;
    private CascadeClassifier cascadeEyeClassifier;
    private int absoluteFaceSize;


    protected void init()
    {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.cascadeEyeClassifier = new CascadeClassifier();
        this.absoluteFaceSize = 0;

        originalFrame.setFitWidth(600);
        originalFrame.setPreserveRatio(true);
    }


    @FXML
    protected void startCamera()
    {
        if (!this.cameraActive)
        {
            this.haarClassifier.setDisable(true);

            this.capture.open(0);

            if (this.capture.isOpened())
            {
                this.cameraActive = true;

                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run()
                    {
                        Mat frame = grabFrame();
                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                this.cameraButton.setText("Stop Camera");
            }
            else
            {
                System.err.println("Failed to open the camera connection...");
            }
        }
        else
        {
            this.cameraActive = false;
            this.cameraButton.setText("Start Camera");
            this.haarClassifier.setDisable(false);

            this.stopAcquisition();
        }
    }


    private Mat grabFrame()
    {
        Mat frame = new Mat();

        if (this.capture.isOpened())
        {
            try
            {
                this.capture.read(frame);

                if (!frame.empty())
                {
                    this.detectAndDisplay(frame);
                }

            }
            catch (Exception e)
            {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    private void detectAndDisplay(Mat frame)
    {
        Mat grayFrame = new Mat();
        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        if (this.absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());





        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);



        cascadeEyeClassifier.detectMultiScale(frame, eyes);
        for (Rect rect : eyes.toArray()) {
            //Sol üst kö?esine metin yaz
            Imgproc.putText(frame, "Eye", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));
            //Kare çiz
            Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(200, 200, 100),2);
        }


    }



    @FXML
    protected void haarSelected(Event event){
        this.faceCascade.load("/home/sweetiki/IdeaProjects/MavenJavaFX/target/classes/haarClassifier/haarcascade_frontalface_alt.xml");

        this.checkboxSelection("/home/sweetiki/IdeaProjects/MavenJavaFX/src/main/resources/haarClassifier/haarcascade_eye.xml");
    }





    private void checkboxSelection(String classifierPath)
    {
        // load the classifier(s)
        cascadeEyeClassifier.load(classifierPath);
        // now the video capture can start
        this.cameraButton.setDisable(false);
    }


    private void stopAcquisition()
    {
        if (this.timer!=null && !this.timer.isShutdown())
        {
            try
            {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened())
        {
            this.capture.release();
        }
    }


    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }


    protected void setClosed()
    {
        this.stopAcquisition();
    }


}