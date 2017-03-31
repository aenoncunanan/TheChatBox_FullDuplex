package ph.com.aenon;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aenon on 30/03/2017.
 */
public class Util {

    public static ImageView loadImage2View(String path, double width, double height){

        ImageView img = null;
        try (InputStream is = Files.newInputStream(Paths.get(path))){
            img = new ImageView(new Image(is));
            img.setFitWidth(width + 10);
            img.setFitHeight(height + 10);
        } catch (IOException e){
            System.out.println("Failed to load image!");
        }

        return img;

    }

}
