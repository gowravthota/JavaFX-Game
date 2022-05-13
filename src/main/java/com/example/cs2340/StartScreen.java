package com.example.cs2340;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


/**
 * StartScreen
 */
public class StartScreen extends Application {

    private int difficultyLevel = 2; // 1 is Easy, 2 is Medium, 3 is Hard
    private Scene gameScreen = null;
    private String playerName = "";

    //Load Image Vars
    private Background bGround;
    private ImageView controllerImageView;
    private ImageView controllerPressedImageView;
    private ImageView gameTitleView;
    private ImageView welcomeTitleView;

    //Difficulty Selection Vars
    private Pane characterInitializationRoot;

    //Name Entry Vars
    private TextField nameInputField;

    //Quit Button Vars
    private HBox quitButtonBox2;

    /**
     * Start
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // LOAD ALL IMAGES
        loadImages();

        // V CHARACTER INITIALIZATION SCREEN STARTS HERE V
        // Difficulty Selection
        difficultySelection();

        // Name entry
        nameEntry();

        // Start Button
        startButton();

        // Quit Button
        quitButton();

        primaryStage.setTitle("Start Screen");
        Scene characterInitializationScene = new Scene(characterInitializationRoot, 1280, 720);
        // ^ CHARACTER INITIALIZATION SCREEN ENDS HERE ^

        // V WELCOME SCENE STARTS HERE V
        Pane welcomeRoot = new Pane();
        welcomeRoot.getChildren().add(quitButtonBox2);
        welcomeRoot.setBackground(bGround);

        Button welcomeButton = new Button("Click to begin");
        welcomeButton.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD,
                FontPosture.ITALIC, 40));
        welcomeButton.setStyle("-fx-text-fill: white; -fx-border-color: white");
        welcomeButton.setBackground(null);
        welcomeButton.setLayoutX(470);
        welcomeButton.setLayoutY(500);

        welcomeTitleView.setLayoutX(0);
        welcomeTitleView.setLayoutY(0);
        welcomeRoot.getChildren().add(welcomeTitleView);

        welcomeRoot.getChildren().add(welcomeButton);
        welcomeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.setScene(characterInitializationScene);
            }
        });

        Scene welcomeScene = new Scene(welcomeRoot, 1280, 720);
        primaryStage.setScene(welcomeScene);

        primaryStage.show();

    }

    /**
     * Load Images
     */
    private void loadImages() {
        Image img = new Image("file:resources/startScreenBackground.jpg");
        BackgroundSize bSize = new BackgroundSize(1280, 720, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        bGround = new Background(bImg);
        Image controllerImage = new Image("file:resources/controllerPNGFixed.png",
                100, 100, false, false);
        controllerImageView = new ImageView(controllerImage);
        Image controllerPressed = new Image("file:resources/controllerPNGPressed.png",
                100, 100, false, false);
        controllerPressedImageView = new ImageView(controllerPressed);
        Image gameTitle = new Image("file:resources/gameTitle.png", 888, 500, false, false);
        gameTitleView = new ImageView(gameTitle);
        Image welcomeTitle = new Image("file:resources/titleScreenLogo.png",
                1280, 720, false, false);
        welcomeTitleView = new ImageView(welcomeTitle);
    }

    /**
     * Difficulty Selection
     */
    private void difficultySelection() {
        Font difficultButtonsFont = Font.font("Helvetica", FontWeight.BOLD,
                FontPosture.REGULAR, 35);
        characterInitializationRoot = new Pane();
        characterInitializationRoot.setBackground(bGround);

        gameTitleView.setLayoutX(180);
        gameTitleView.setLayoutY(-100);
        characterInitializationRoot.getChildren().add(gameTitleView);

        // Difficulty Selection
        ToggleGroup difficultyButtons = new ToggleGroup();

        RadioButton easyButton = new RadioButton("Easy");
        easyButton.setToggleGroup(difficultyButtons);
        easyButton.setSelected(true);
        easyButton.setFont(difficultButtonsFont);
        easyButton.setStyle("-fx-text-fill: white;");

        RadioButton mediumButton = new RadioButton("Medium");
        mediumButton.setToggleGroup(difficultyButtons);
        mediumButton.setFont(difficultButtonsFont);
        mediumButton.setStyle("-fx-text-fill: white;");

        RadioButton hardButton = new RadioButton("Hard");
        hardButton.setToggleGroup(difficultyButtons);
        hardButton.setFont(difficultButtonsFont);
        hardButton.setStyle("-fx-text-fill: white;");

        easyButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov,
                                                   Boolean old_val, Boolean new_val) -> {
            difficultyLevel = 1;
        });
        mediumButton.selectedProperty().addListener((ObservableValue<? extends
                Boolean> ov, Boolean old_val, Boolean new_val) -> {
            difficultyLevel = 2;
        });
        hardButton.selectedProperty().addListener((ObservableValue<? extends
                Boolean> ov, Boolean old_val, Boolean new_val) -> {
            difficultyLevel = 3;
        });

        mediumButton.setSelected(true);

        Label difficultyLabel = new Label("Difficulty: ");
        difficultyLabel.setStyle("-fx-text-fill: green;");
        difficultyLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40));

        VBox difficultyPane = new VBox(difficultyLabel, easyButton, mediumButton, hardButton);
        difficultyPane.setSpacing(45);
        difficultyPane.setPadding(new Insets(0, 0, 0, 50));
        difficultyPane.setLayoutX(150);
        difficultyPane.setLayoutY(230);
        //difficultyPane.setStyle("-fx-border-color: white;");
        characterInitializationRoot.getChildren().add(difficultyPane);
    }

    /**
     * Name Entry
     */
    private void nameEntry() {
        //Creating a GridPane container
        VBox nameEntry = new VBox();
        //nameEntry.setStyle("-fx-border-color: white;");
        nameEntry.setSpacing(20);
        Label nameLabel = new Label("Enter player name: ");
        nameLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40));
        nameLabel.setStyle("-fx-text-fill: green;");
        nameEntry.getChildren().add(nameLabel);

        HBox box = new HBox();
        nameInputField = new TextField();
        nameInputField.setStyle("-fx-background-color: transparent; "
                + "-fx-text-fill: white; -fx-border-color:white;");
        nameInputField.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.REGULAR, 30));

        nameEntry.getChildren().add(nameInputField);

        nameEntry.setLayoutX(500);
        nameEntry.setLayoutY(230);
        characterInitializationRoot.getChildren().add(nameEntry);
    }

    /**
     * Start Button
     */
    private void startButton() {
        // Start Button
        Button startButton = new Button(" - START", controllerImageView);
        startButton.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 40));
        startButton.setStyle("-fx-text-fill: white;");
        startButton.setBackground(null);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                playerName = nameInputField.getText();
                if (playerName.trim().equals("")) {
                    Alert nameAlert = new Alert(Alert.AlertType.NONE);
                    nameAlert.setAlertType(Alert.AlertType.INFORMATION);
                    nameAlert.setTitle("Player name is empty");
                    nameAlert.setHeaderText("Please enter a player name");
                    nameAlert.show();
                } else {
                    Button button = (Button) actionEvent.getSource();
                    button.setGraphic(controllerPressedImageView);
                    button.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
                    System.out.println(difficultyLevel);
                    Stage programStage = (Stage) button.getScene().getWindow();
                    MainGame game = new MainGame(difficultyLevel);
                    gameScreen = game.updateScene();
                    programStage.setScene(gameScreen);
                }
            }
        });
        HBox startButtonBox = new HBox();
        startButtonBox.getChildren().add(startButton);
        startButtonBox.setMaxWidth(350);
        startButtonBox.setMaxHeight(250);
        startButtonBox.setLayoutX(970);
        startButtonBox.setLayoutY(600);

        characterInitializationRoot.getChildren().add(startButtonBox);
    }

    /**
     * Quit Button
     */
    private void quitButton() {
        // Quit Button
        Button quitButton = new Button("EXIT");
        quitButton.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 40));
        quitButton.setStyle("-fx-text-fill: red;");
        quitButton.setBackground(null);
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
            }
        });

        // Quit Button
        Button quitButton2 = new Button("EXIT");
        quitButton2.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 40));
        quitButton2.setStyle("-fx-text-fill: red;");
        quitButton2.setBackground(null);
        quitButton2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
            }
        });

        HBox quitButtonBox = new HBox();
        quitButtonBox.getChildren().add(quitButton);
        quitButtonBox.setMaxWidth(350);
        quitButtonBox.setMaxHeight(250);
        quitButtonBox.setLayoutX(0);
        quitButtonBox.setLayoutY(635);

        quitButtonBox2 = new HBox();
        quitButtonBox2.getChildren().add(quitButton2);
        quitButtonBox2.setMaxWidth(350);
        quitButtonBox2.setMaxHeight(250);
        quitButtonBox2.setLayoutX(0);
        quitButtonBox2.setLayoutY(635);

        characterInitializationRoot.getChildren().add(quitButtonBox);
    }
    /**
     *  Get Difficulty
     */



    /**
     * Main
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
