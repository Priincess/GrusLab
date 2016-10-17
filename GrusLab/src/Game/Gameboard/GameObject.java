package Game.Gameboard;


import javafx.beans.property.IntegerProperty;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameObject {
    ImageView imageView;
    GameObjectType type;
    MediaPlayer player;

    public GameObject(GameObjectType type, IntegerProperty size){
        this.type = type;

        switch (type){
            case MINION:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/minion.png").toExternalForm());
                break;
            case BANANA:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/banana.png").toExternalForm());
                break;
            case GOGGLES:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/goggles.png").toExternalForm());
                break;
            case BEEDO:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/beedo.png").toExternalForm());
                break;
            case REFERENCEPOINT:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/referencePoint.png").toExternalForm());
                break;
            case CAMERAPOINT:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/cameraPoint.png").toExternalForm());
                break;
            default:
                this.imageView = new ImageView(getClass().getResource("GameObjectMedia/dummy.jpg").toExternalForm());
                break;
        }
        setSize(size);
    }

    public void setSize(IntegerProperty size){
        if (size != null) {
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(size);
            imageView.fitHeightProperty().bind(size);
        }
    }

    public void setPosition(int x, int y){
        imageView.setX(x);
        imageView.setY(y);
    }

    public void playSound(){
        switch (this.type){
            case BANANA:
                player = new MediaPlayer(new Media(getClass().getResource("GameObjectMedia/bababa_banana.mp3").toExternalForm()));
                break;
            case BEEDO:
                player = new MediaPlayer(new Media(getClass().getResource("GameObjectMedia/beedo.mp3").toExternalForm()));
                break;
            case GOGGLES:
                player = new MediaPlayer(new Media(getClass().getResource("GameObjectMedia/What.mp3").toExternalForm()));
                break;
        }
        if (player != null) {
            player.play();
        }
    }
}
