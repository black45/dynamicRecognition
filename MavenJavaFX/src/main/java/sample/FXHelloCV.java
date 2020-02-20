package sample;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class FXHelloCV extends Application{

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sample.fxml"));
            BorderPane root = (BorderPane) loader.load();
            root.setStyle("-fx-background-color: whitesmoke;");
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Face Detection and Tracking");
            primaryStage.setScene(scene);
            primaryStage.show();


            FXHelloCVController controller = loader.getController();
            controller.init();

            primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we)
                {
                    controller.setClosed();
                }
            }));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        nu.pattern.OpenCV.loadShared();
        launch(args);
    }
}
