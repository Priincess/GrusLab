package game.gameboard;


import javafx.beans.property.IntegerProperty;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameObject {
    private ImageView imageView;
    private GameObjectType type;
    private MediaPlayer player;

    public GameObject(GameObjectType type, IntegerProperty size){
        this.type = type;

        switch (type){
            case YELLOWMINION:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/minion1.png").toExternalForm());
                break;
            case PURPLEMINION:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/minion2.png").toExternalForm());
                break;
            case BANANA:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/banana.png").toExternalForm());
                break;
            case GOGGLES:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/goggles.png").toExternalForm());
                break;
            case BEEDO:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/beedo.png").toExternalForm());
                break;
            case REFERENCEPOINT:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/referencePoint.png").toExternalForm());
                break;
            case CAMERAPOINT:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/cameraPoint.png").toExternalForm());
                break;
            default:
                this.imageView = new ImageView(getClass().getResource("gameObjectMedia/dummy.jpg").toExternalForm());
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
                player = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/bababa_banana.mp3").toExternalForm()));
                break;
            case BEEDO:
                player = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/beedo.mp3").toExternalForm()));
                break;
            case GOGGLES:
                player = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/What.mp3").toExternalForm()));
                break;
        }
        if (player != null) {
            player.play();
        }
    }

    public ImageView getImageView(){
        return imageView;
    }

    public GameObjectType getType(){
        return this.type;
    }
}
