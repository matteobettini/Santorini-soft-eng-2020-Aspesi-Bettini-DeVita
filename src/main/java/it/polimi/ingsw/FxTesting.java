package it.polimi.ingsw;



import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Box;

public class FxTesting extends Application {

    private static final float WIDTH = 1000;
    private static final float HEIGHT = 600;

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);


    private static class SmartGroup extends Group{
        Rotate r;
        Transform t;

        void rotateByX(int ang){
            r = new Rotate(ang, new Point3D(0,50,0));
            t = t.createConcatenation(r);
            this.getTransforms().addAll(t);
        }

        void rotateByY(int ang){
            r = new Rotate(ang, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().addAll(t);
        }


    }

    public static void  main(String[] args){
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        SmartGroup smartGroup = new SmartGroup();
        smartGroup.getChildren().addAll(createShape());
        smartGroup.getChildren().addAll(createLights());




        Group groupRoot = new Group();
        groupRoot.getChildren().addAll(smartGroup);
        Camera camera = new PerspectiveCamera();
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateX(-WIDTH/2);
        camera.setTranslateY(-HEIGHT/2);


        Scene scene = new Scene(groupRoot, WIDTH, HEIGHT, true);
        scene.setFill(Color.SILVER);
        scene.setCamera(camera);


        initMouseControl(smartGroup, scene);



        primaryStage.setTitle("Matteo's experiment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private Node[] createShape(){
        Sphere ball1 = new Sphere(20);
        Sphere ball2 = new Sphere(20);
        Cylinder body = new Cylinder(15,100);
        Sphere head = new Sphere(15);


        PhongMaterial material = new PhongMaterial(Color.BLANCHEDALMOND);
        material.setBumpMap(new Image("textures/bump2.jpeg"));

        ball1.getTransforms().add(new Translate(-13, 0, 0));
        ball2.getTransforms().add(new Translate(13, 0, 0));
        body.getTransforms().add(new Translate(0, -50, 0));
        head.getTransforms().add(new Translate(0, -100, 0));

        ball1.setMaterial(material);
        ball2.setMaterial(material);
        body.setMaterial(material);
        head.setMaterial(material);

        Node[] nodes = new Node[]{ball1, ball2, body, head};
        for(Node n : nodes){
            n.getTransforms().add(new Translate(0, 56, 0));
        }

        return nodes;
    }

    private Node[] createLights(){
        PointLight light1 = new PointLight(Color.GREEN);
        PointLight light2 = new PointLight(Color.YELLOW);
        PointLight light3 = new PointLight(Color.RED);
        Sphere sphereLight1 = new Sphere(5);
        Sphere sphereLight2 = new Sphere(5);
        Sphere sphereLight3 = new Sphere(5);

        light1.getTransforms().addAll(new Translate(-80,0,0));
        light2.getTransforms().addAll(new Translate(-80,-60,0));
        light3.getTransforms().addAll(new Translate(-80,60,0));

        light1.setRotationAxis(Rotate.Y_AXIS);
        light2.setRotationAxis(Rotate.Y_AXIS);
        light3.setRotationAxis(Rotate.Y_AXIS);

        sphereLight1.rotationAxisProperty().bind(light1.rotationAxisProperty());
        sphereLight2.rotationAxisProperty().bind(light2.rotationAxisProperty());
        sphereLight3.rotationAxisProperty().bind(light3.rotationAxisProperty());

        sphereLight1.rotateProperty().bind(light1.rotateProperty());
        sphereLight2.rotateProperty().bind(light2.rotateProperty());
        sphereLight3.rotateProperty().bind(light3.rotateProperty());

        sphereLight1.getTransforms().addAll(light1.getTransforms());
        sphereLight2.getTransforms().addAll(light2.getTransforms());
        sphereLight3.getTransforms().addAll(light3.getTransforms());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                light1.setRotate(light1.getRotate() + 3);
                light2.setRotate(light2.getRotate() + 6);
                light3.setRotate(light3.getRotate() + 2);
            }
        };
        timer.start();

        Node[] lights = new Node[]{light1,light2,light3, sphereLight1,sphereLight2,sphereLight3};
        return lights;
    }



    void initMouseControl(SmartGroup smartGroup, Scene scene){
        Rotate xRotate = new Rotate(0,Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0,Rotate.Y_AXIS);
        smartGroup.getTransforms().addAll(xRotate , yRotate);
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        scene.setOnMousePressed(event ->{
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY - (anchorX - event.getSceneX()));
        });

       scene.addEventHandler(ScrollEvent.SCROLL, event ->{
           double deltaY = event.getDeltaY();
           smartGroup.translateZProperty().set(smartGroup.getTranslateZ() + deltaY);
       });
    }
}

