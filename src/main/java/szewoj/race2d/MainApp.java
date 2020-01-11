package szewoj.race2d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import szewoj.race2d.controller.GameController;
import szewoj.race2d.view.ViewManager;

import java.io.IOException;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));

        ViewManager mainView = loader.getController();

        GameController mainGameController = new GameController( mainView );

        new AnimationTimer()
        {
            @Override
            public void handle(long currentNanoTime)
            {
                mainGameController.refresh();
            }
        }.start();

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
