package game.gameboard;


import javafx.beans.property.IntegerProperty;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameObject {
    private ImageView _imageView;
    private GameObjectType _type;
    private MediaPlayer _mediaPlayer;

    public GameObject(GameObjectType type, IntegerProperty size){
        _type = type;

        switch (type){
            case YELLOWMINION:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/minion1.png").toExternalForm());
                break;
            case PURPLEMINION:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/minion2.png").toExternalForm());
                break;
            case BANANA:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/banana.png").toExternalForm());
                break;
            case GOGGLES:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/goggles.png").toExternalForm());
                break;
            case BEEDO:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/beedo.png").toExternalForm());
                break;
            case REFERENCEPOINT:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/referencePoint.png").toExternalForm());
                break;
            case CAMERAPOINT:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/cameraPoint.png").toExternalForm());
                break;
            default:
                _imageView = new ImageView(getClass().getResource("gameObjectMedia/dummy.jpg").toExternalForm());
                break;
        }
        setSize(size);
    }

    public void setSize(IntegerProperty size){
        if (size != null) {
            _imageView.setPreserveRatio(true);
            _imageView.fitWidthProperty().bind(size);
            _imageView.fitHeightProperty().bind(size);
        }
    }

    public void setPosition(int x, int y){
        _imageView.setX(x);
        _imageView.setY(y);
    }

    public void playSound(){
        switch (_type){
            case BANANA:
                _mediaPlayer = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/bababa_banana.mp3").toExternalForm()));
                break;
            case BEEDO:
                _mediaPlayer = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/beedo.mp3").toExternalForm()));
                break;
            case GOGGLES:
                _mediaPlayer = new MediaPlayer(new Media(getClass().getResource("gameObjectMedia/What.mp3").toExternalForm()));
                break;
        }
        if (_mediaPlayer != null) {
            _mediaPlayer.play();
        }
    }

    public ImageView getImageView(){
        return _imageView;
    }

    public GameObjectType getType(){
        return _type;
    }
}
