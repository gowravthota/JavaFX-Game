package com.example.cs2340;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
//import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
//import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * MainGame
 */
public class MainGame {
    private ArrayList<Enemy> activeEnemiesReal = new ArrayList<Enemy>();
    private ArrayList<Projectile> activeProjectilesReal = new ArrayList<Projectile>();
    private ArrayList<ImageView> activeTowerList = new ArrayList<ImageView>();
    private ArrayList<ImageView> activeProjectiles = new ArrayList<ImageView>();
    private LinkedList<Tower> towerList = new LinkedList<Tower>();
    private int gameDifficulty;
    private int health;
    private int money;
    private int towerPrice;
    private Pane gamePane = null;
    private boolean inCombat = false;
    private ArrayList<ImageView> activeEnemies = new ArrayList();
    private ArrayList<PathTransition> activeAnimations = new ArrayList<PathTransition>();
    private ArrayList<Timeline> animationStarters = new ArrayList<>();
    private int moneyEarned = 0;
    private int enemiesSpawned = 0;
    private int timeElapsed = 0;
    private int damage = 1;
    private int range = 200;

    /**
     * MainGame
     * @param difficulty the selected difficulty
     */
    public MainGame(int difficulty) {
        this.gameDifficulty = difficulty;
        this.money = 4000 - difficulty * 1000;
        this.health = 200 - difficulty * 50;
        this.towerPrice = 200 + difficulty * 100;
    }

    public static boolean checkIfInRange(double towerX, double towerY,
                                         double enemyX, double enemyY, double range) {
        System.out.println(towerX + " " + towerY);
        System.out.println(enemyX + " " + enemyY);
        double aSquared = (enemyX - towerX) * (enemyX - towerX);
        double bSquared = (enemyY - towerY) * (enemyY - towerY);
        double cSquared = aSquared + bSquared;
        double dist = Math.sqrt(cSquared);
        System.out.println(dist);
        return (dist < range);
    }

    /**
     * Update Scene
     * @return the game screen
     */
    public Scene updateScene() {
        Pane gamePanel = new Pane();
        Image img = new Image("file:resources/mapOne.png");
        Image longShotPic = new Image("file:resources/LongShot.png");
        Image multiShotPic = new Image("file:resources/MultiShot.png");
        Image spreadShotPic = new Image("file:resources/SpreadShot.png");
        BackgroundSize bSize = new BackgroundSize(1280, 720, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        Background bGround = new Background(bImg);
        gamePanel.setBackground(bGround);
        Label healthLabel = new Label(Integer.toString(health));
        healthLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 30));
        healthLabel.setStyle("-fx-text-fill: red;");
        healthLabel.setLayoutX(1120);
        healthLabel.setLayoutY(22);
        Label moneyLabel = new Label(Integer.toString(money));
        moneyLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 30));
        moneyLabel.setStyle("-fx-text-fill: orange;");
        moneyLabel.setLayoutX(68);
        moneyLabel.setLayoutY(20);

        Image attackUp = new Image("file:resources/attackUp.png", 50, 50, false, false);
        Image rangeUp = new Image("file:resources/rangeUp.png", 50, 50, false, false);
        ImageView attackUpIcon = new ImageView(attackUp);
        ImageView rangeUpIcon = new ImageView(rangeUp);
        attackUpIcon.setLayoutX(400);
        rangeUpIcon.setLayoutX(700);
        attackUpIcon.setLayoutY(20);
        rangeUpIcon.setLayoutY(20);

        Label attackLabel = new Label(Integer.toString(damage));
        Label rangeLabel = new Label(Integer.toString(range));
        attackLabel.setLayoutX(470);
        attackLabel.setLayoutY(15);

        rangeLabel.setLayoutX(770);
        rangeLabel.setLayoutY(15);

        attackLabel.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.REGULAR, 40));
        attackLabel.setStyle("-fx-text-fill: black;");

        rangeLabel.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.REGULAR, 40));
        rangeLabel.setStyle("-fx-text-fill: black;");

        Image shopImage = new Image("file:resources/shopIcon.png",
                100, 100, false, false);
        ImageView shopIcon = new ImageView(shopImage);
        Button openBuyMenu = new Button("SHOP", shopIcon);
        openBuyMenu(openBuyMenu, gamePanel, moneyLabel);
        EventHandler handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                enemiesSpawned++;
                spawnEnemy(gamePanel, healthLabel);
            }
        };


        Image upgradeImage = new Image("file:resources/Wrench.png",
                100, 100, false, false);
        ImageView upgradeIcon = new ImageView(upgradeImage);

        Button openUpgradeMenu = new Button("UPGRADES", upgradeIcon);
        openUpgradeMenu(openUpgradeMenu, moneyLabel, attackLabel, rangeLabel);

        int enemySpawnDelay = 5000;
        Timeline spawnWave = new Timeline(new KeyFrame(Duration.millis(enemySpawnDelay), handler));
        spawnWave.setCycleCount(Timeline.INDEFINITE);
        animationStarters.add(spawnWave);
        Button startCombatButton = new Button("START COMBAT");
        startCombatButton.setLayoutX(12 * 80 - 10);
        startCombatButton.setLayoutY(680);
        startCombatButton.setPrefWidth(320);
        startCombatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gamePane = gamePanel;
                inCombat = true;
                startMoneyGeneration(moneyLabel);
                Timeline checkForEnemyHit = new Timeline(
                        new KeyFrame(Duration.millis(50),
                                new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                for (int i = 0; i < activeProjectilesReal.size(); i++) {
                                    for (int j = 0; j < activeEnemiesReal.size(); j++) {
                                        if (activeProjectilesReal.get(i).
                                                checkCollision(activeEnemiesReal.get(j))) {
                                            if (activeEnemiesReal.get(j) instanceof Boss) {
                                                ((Boss) activeEnemiesReal.
                                                        get(j)).bossHealth -= damage;
                                                if (((Boss) activeEnemiesReal.
                                                        get(j)).bossHealth >= 0) {
                                                    continue;

                                                } else {
                                                    for (PathTransition anim : activeAnimations) {
                                                        if (anim.getNode().equals(
                                                                activeEnemiesReal.
                                                                        get(j).getImage())) {
                                                            anim.stop();
                                                            activeEnemiesReal.get(j).getImage()
                                                                    .setLayoutX(-1000);
                                                            activeEnemiesReal.get(j).getImage()
                                                                    .setLayoutY(-1000);
                                                        }
                                                    }

                                                    activeEnemiesReal.remove(j--);
                                                    gamePanel.getChildren().clear();
                                                    Stage st = (Stage)
                                                            gamePanel.getScene().getWindow();
                                                    Scene endScreen = buildGameWinWindow();
                                                    st.setScene(endScreen);
                                                }
                                            } else {
                                                activeEnemiesReal.get(j).health -= damage;
                                                if (activeEnemiesReal.get(j).health <= 0) {

                                                    for (PathTransition anim : activeAnimations) {
                                                        if (anim.getNode().equals(
                                                                activeEnemiesReal.
                                                                        get(j).getImage())) {
                                                            anim.stop();
                                                            activeEnemiesReal.get(j).getImage()
                                                                    .setLayoutX(-1000);
                                                            activeEnemiesReal.get(j).getImage()
                                                                    .setLayoutY(-1000);
                                                        }
                                                    }

                                                    activeEnemiesReal.remove(j--);
                                                }
                                                activeProjectilesReal.remove(i--);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }));
                checkForEnemyHit.setCycleCount(Animation.INDEFINITE); // loop forever
                checkForEnemyHit.play();
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1),
                                new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                timeElapsed++;
                                for (ImageView tower: activeTowerList) {
                                    for (Enemy enemy: activeEnemiesReal) {
                                        if (checkIfInRange(tower.getLayoutX(), tower.getLayoutY(),
                                                enemy.getX(), enemy.getY(), range)) {
                                            if (tower.getImage().getPixelReader().getArgb(20, 20)
                                                    == longShotPic.getPixelReader().
                                                    getArgb(20, 20)) {
                                                spawnProjectileLongShot(gamePanel,
                                                        tower.getLayoutX(), tower.getLayoutY(),
                                                        tower.getRotate());
                                            } else if (tower.getImage().getPixelReader()
                                                    .getArgb(20, 20) == multiShotPic.
                                                    getPixelReader().getArgb(20, 20)) {
                                                spawnProjectileMultiShot(gamePanel,
                                                        tower.getLayoutX(), tower.getLayoutY());
                                            } else if (tower.getImage().getPixelReader().getArgb(20,
                                                    20) == spreadShotPic.getPixelReader()
                                                    .getArgb(20, 20)) {
                                                spawnProjectileSpreadShot(gamePanel,
                                                        tower.getLayoutX(),
                                                        tower.getLayoutY(), tower.getRotate());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }));
                timeline.setCycleCount(Animation.INDEFINITE); // loop forever
                timeline.play();
                spawnWave.play();
                Button button = (Button) event.getSource();
                gamePanel.getChildren().remove(button);

                Timeline spawnBoss = new Timeline(
                    new KeyFrame(Duration.millis(60000),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                spawnBoss(gamePanel, healthLabel);
                            }
                        }));

                spawnBoss.play();
            }
        });
        gamePanel.getChildren().addAll(moneyLabel, healthLabel, openBuyMenu, openUpgradeMenu,
                startCombatButton, attackUpIcon, rangeUpIcon, attackLabel, rangeLabel);
        gamePanel.setOnMousePressed(mouseEvent -> {
            if (towerList.isEmpty()) {
                return;
            }
            Tower tempTower = towerList.getLast();
            ImageView towerImageView = tempTower.image;
            if (mouseEvent.isSecondaryButtonDown()) {
                towerImageView.setRotate(towerImageView.getRotate() + 90);
            } else {
                if (tempTower.checkCollisionPath()) {
                    gamePanel.getChildren().remove(tempTower.image);
                    activeTowerList.remove(tempTower.image);
                    towerList.remove(tempTower);
                } else {
                    towerImageView.setOpacity(1);
                    buyTower();
                    towerList.remove(tempTower);
                    moneyLabel.setText(Integer.toString(money));
                }
            }
        });
        Scene gameScreen = new Scene(gamePanel, 1280, 720);
        return gameScreen;
    }

    /**
     * @param moneyLabel the money label
     */
    public void startMoneyGeneration(Label moneyLabel) {
        Timeline moneyPerSecond = new Timeline(
            new KeyFrame(Duration.millis(100 * gameDifficulty),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (inCombat) {
                            money += 1;
                            moneyEarned++;
                            moneyLabel.setText(Integer.toString(money));
                        }
                    }
                }));
        moneyPerSecond.setCycleCount(Animation.INDEFINITE); // loop forever
        moneyPerSecond.play();
    }
    public void startMoneyGeneration() {
        if (inCombat) {
            this.moneyEarned = getGameDifficulty() * 100 * 90;

        }
    }
    public void startCombat() {
        this.inCombat = true;
    }

    /**
     * @param openBuyMenu the buy menu
     * @param gamePanel the game panel
     * @param moneyLabel the money label
     */
    public void openBuyMenu(Button openBuyMenu, Pane gamePanel, Label moneyLabel) {
        openBuyMenu.setLayoutX(3 * 80 + 30);
        openBuyMenu.setLayoutY(600);
        openBuyMenu.setPrefWidth(320);
        openBuyMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage buyWindow = new Stage();
                Scene buyMenu = createBuyMenu(gamePanel, moneyLabel);
                buyWindow.setScene(buyMenu);
                buyWindow.show();
            }
        });
    }

    public void openUpgradeMenu(Button openUpgradeMenu, Label moneyLabel,
                                Label attackLabel, Label rangeLabel) {
        openUpgradeMenu.setLayoutX(8 * 80 + 20);
        openUpgradeMenu.setLayoutY(600);
        openUpgradeMenu.setPrefWidth(270);
        openUpgradeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage upgradeWindow = new Stage();
                Scene upgradeMenu = createUpgradeMenu(moneyLabel, attackLabel, rangeLabel);
                upgradeWindow.setScene(upgradeMenu);
                upgradeWindow.show();
            }
        });
    }

    /**
     * Ends the combat.
     */
    public void endCombat() {
        inCombat = false;
    }

    /**
     * get Difficulty
     * @return game difficulty
     */
    public int getGameDifficulty() {

        return this.gameDifficulty;
    }
    /**
     * get health
     * @return game health
     */
    public int getHealth() {

        return this.health;
    }
    /**
     * get money
     * @return game money
     */
    public int getMoney() {

        return this.money;
    }

    /**
     * get tower price
     * @return game tower price
     */
    public int getTowerPrice() {

        return this.towerPrice;
    }

    public Path testPath(List<Point2D> waypoints) {
        return createSharpPath(waypoints);
    }

    public List<Point2D> getWayPoints() {
        List<Point2D> path = new ArrayList<>();

        try {
            File myObj = new File("resources/waypoints.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextInt()) {
                path.add(new Point2D((double) myReader.nextInt(), (double) myReader.nextInt()));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return path;
    }

    private Path createSharpPath(List<Point2D> waypoints) {

        Path path = new Path();

        for (Point2D point: waypoints) {
            if (path.getElements().isEmpty()) {
                path.getElements().add(new MoveTo(point.getX(), point.getY()));
            } else {
                path.getElements().add(new LineTo(point.getX(), point.getY()));
            }
        }

        return path;
    }

    public int testProjectileSpawn(int n) {
        ArrayList<Projectile> testProjs = new ArrayList<Projectile>();
        for (int i = 0; i < n; i++) {
            testProjs.add(new Projectile());
        }
        int projCount = 0;
        for (Projectile proj : testProjs) {
            if (testProjs != null) {
                projCount++;
            }
        }
        return projCount;
    }

    public int testWaveSpawn(int n) {
        ArrayList<Enemy> testEnemies = new ArrayList<Enemy>();
        for (int i = 0; i < n; i++) {
            testEnemies.add(new Enemy());
        }
        int enemCount = 0;
        for (Enemy enem : testEnemies) {
            if (enem != null) {
                enemCount++;
            }
        }
        return enemCount;
    }

    public int testBossSpawn(int n) {
        ArrayList<Boss> testBosses = new ArrayList<Boss>();
        for (int i = 0; i < n; i++) {
            testBosses.add(new Boss());
        }
        int enemCount = 0;
        for (Boss boss : testBosses) {
            if (boss != null) {
                enemCount++;
            }
        }
        return enemCount;
    }

    private void spawnEnemy(Pane gamePanel, Label healthLabel) {
        int max = 3;
        int min = 1;
        int range = max - min + 1;

        // generate random numbers within 1 to 10
        int randChoice = (int) (Math.random() * range) + min;
        int duration = 10000;
        Image enem;
        ImageView basicEnemy;
        switch (randChoice) {
        case 1:
            enem = new Image("file:resources/Enemy1.png",
                    80, 80, false, false);
            basicEnemy = new ImageView(enem);
            duration = 15000;
            break;
        case 2:
            enem = new Image("file:resources/Enemy2.png",
                    80, 80, false, false);
            basicEnemy = new ImageView(enem);
            duration = 20000;
            break;
        case 3:
            enem = new Image("file:resources/Enemy3.png",
                    80, 80, false, false);
            basicEnemy = new ImageView(enem);
            duration = 25000;
            break;
        default:
            enem = new Image("file:resources/Enemy1.png",
                    80, 80, false, false);
            basicEnemy = new ImageView(enem);
            break;
        }


        List<Point2D> waypoints = getWayPoints();
        Path sharpPath = createSharpPath(waypoints);
        gamePanel.getChildren().add(basicEnemy);



        PathTransition pt = new PathTransition(Duration.millis(duration), sharpPath);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);


        activeEnemies.add(basicEnemy);

        pt.setNode(basicEnemy);

        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gamePanel.getChildren().remove(basicEnemy);
                health = health - 1;
                healthLabel.setText(Integer.toString(health));
                for (int i = 0; i < activeEnemiesReal.size(); i++) {
                    if (activeEnemiesReal.get(i).getImage().equals(basicEnemy)) {
                        activeEnemiesReal.get(i).healthBar.setWidth(0);
                        activeEnemiesReal.remove(i);
                        i--;
                    }
                }
                if (health <= 0) {
                    for (PathTransition anim : activeAnimations) {
                        anim.stop();
                    }
                    animationStarters.get(0).stop();
                    gamePanel.getChildren().clear();
                    Stage st = (Stage) gamePanel.getScene().getWindow();
                    Scene endScreen = buildGameOverWindow();
                    st.setScene(endScreen);
                }
            }
        });

        Enemy newEnem = new Enemy(basicEnemy, gamePanel);
        DoubleProperty xValue = new SimpleDoubleProperty();
        xValue.bind(basicEnemy.translateXProperty());
        xValue.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newEnem.setX((double) t1);
                newEnem.updateHealth(gamePanel);
            }
        });
        DoubleProperty yValue = new SimpleDoubleProperty();
        yValue.bind(basicEnemy.translateYProperty());
        yValue.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newEnem.setY((double) t1);
                newEnem.updateHealth(gamePanel);
            }
        });

        activeEnemiesReal.add(newEnem);

        activeAnimations.add(pt);
        pt.play();
    }

    private void spawnBoss(Pane gamePanel, Label healthLabel) {
        Image enem = new Image("file:resources/Boss.png",
                80, 80, false, false);
        ImageView basicEnemy = new ImageView(enem);
        int duration = 30000;


        List<Point2D> waypoints = getWayPoints();
        Path sharpPath = createSharpPath(waypoints);
        gamePanel.getChildren().add(basicEnemy);



        PathTransition pt = new PathTransition(Duration.millis(duration), sharpPath);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);


        activeEnemies.add(basicEnemy);

        pt.setNode(basicEnemy);

        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gamePanel.getChildren().remove(basicEnemy);
                health = health - 1000;
                healthLabel.setText(Integer.toString(health));
                for (int i = 0; i < activeEnemiesReal.size(); i++) {
                    if (activeEnemiesReal.get(i).getImage().equals(basicEnemy)) {
                        activeEnemiesReal.get(i).healthBar.setWidth(0);
                        activeEnemiesReal.remove(i);
                        i--;
                    }
                }
                if (health <= 0) {
                    for (PathTransition anim : activeAnimations) {
                        anim.stop();
                    }
                    animationStarters.get(0).stop();
                    gamePanel.getChildren().clear();
                    Stage st = (Stage) gamePanel.getScene().getWindow();
                    Scene endScreen = buildGameOverWindow();
                    st.setScene(endScreen);
                }
                if (health > 0) {
                    for (PathTransition anim : activeAnimations) {
                        anim.stop();
                    }
                    animationStarters.get(0).stop();
                    gamePanel.getChildren().clear();
                    Stage st = (Stage) gamePanel.getScene().getWindow();
                    Scene endScreen = buildGameWinWindow();
                    st.setScene(endScreen);
                }
            }
        });

        Boss newEnem = new Boss(basicEnemy, gamePanel);
        DoubleProperty xValue = new SimpleDoubleProperty();
        xValue.bind(basicEnemy.translateXProperty());
        xValue.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newEnem.setX((double) t1);
                newEnem.updateHealth(gamePanel);
            }
        });
        DoubleProperty yValue = new SimpleDoubleProperty();
        yValue.bind(basicEnemy.translateYProperty());
        yValue.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newEnem.setY((double) t1);
                newEnem.updateHealth(gamePanel);
            }
        });

        activeEnemiesReal.add(newEnem);

        activeAnimations.add(pt);
        pt.play();
    }

    /**
     * Simulates collision for test case uses.
     *
     * @param newProjectile new test projectile
     * @param enemy1 new test enemy
     * @return true upon collision, otherwise false
     */
    public boolean testProjectileCollision(Projectile newProjectile, Enemy enemy1) {
        if (newProjectile.checkCollision(enemy1)) {
            enemy1.health -= damage;
            return true;
        }
        return false;
    }

    /**
     * Creates empty projectile for test case uses.
     *
     * @return Projectile
     */
    public Projectile getEmptyProjectile() {
        Projectile empty = new Projectile();
        return empty;
    }

    /**
     * Creates empty enemy for test case uses.
     *
     * @return Enemy
     */
    public Enemy getEmptyEnemy() {
        Enemy empty = new Enemy();
        return empty;
    }

    /**
     * Creates empty Tower for test case uses.
     *
     * @return Enemy
     */
    public Tower getEmptyTower() {
        Tower tower = new Tower();
        return tower;
    }

    private void placeNewTower(Pane gamePanel, String type, Label moneyLabel) {
        Tower tower = new Tower(0, 0, type);
        ImageView towerImageView = tower.getImage();
        towerImageView.setOpacity(.5);
        towerList.add(tower);
        gamePanel.getChildren().add(towerImageView);
        gamePanel.setOnMouseMoved(mouseEvent -> {
            if (!towerList.isEmpty()) {
                final Tower tower2 = towerList.getLast();
                final ImageView towerImageView2 = tower.getImage();
                towerImageView2.setLayoutX(mouseEvent.getX() - 40);
                towerImageView2.setLayoutY(mouseEvent.getY() - 40);
            }
        }
        );
    }



    private void spawnProjectileLongShot(Pane gamePanel, double towerX,
                                         double towerY, double towerRotate) {

        Image bulletPic = new Image("file:resources/Bullet.png",
                20, 20, false, false);
        ImageView bullet = new ImageView(bulletPic);

        List<Point2D> waypoints = new ArrayList<Point2D>();

        double[] xAdd = {300, 0, -300, 0};
        double[] yAdd = {0, 300, 0, -300};

        int selector = (int) towerRotate / 90;

        waypoints.add(new Point2D(towerX + 30, towerY + 30));
        waypoints.add(new Point2D(towerX + 30 + xAdd[selector], towerY + 30 + yAdd[selector]));

        Path sharpPath = createSharpPath(waypoints);
        gamePanel.getChildren().add(bullet);



        PathTransition pt = new PathTransition(Duration.millis(1000), sharpPath);

        pt.setNode(bullet);


        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gamePanel.getChildren().remove(bullet);
            }
        });

        Projectile newProjectile = new Projectile(bullet);

        DoubleProperty xValue = new SimpleDoubleProperty();
        xValue.bind(bullet.translateXProperty());
        xValue.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newProjectile.setX((double) t1);
            }
        });

        DoubleProperty yValue = new SimpleDoubleProperty();
        yValue.bind(bullet.translateYProperty());
        yValue.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                newProjectile.setY((double) t1);
            }
        });

        activeProjectilesReal.add(newProjectile);

        activeAnimations.add(pt);
        pt.play();

    }

    private void spawnProjectileSpreadShot(Pane gamePanel, double towerX, double towerY,
                                           double towerRotate) {

        double[] xAdd = {300, 0, -300, 0};
        double[] yAdd = {0, 300, 0, -300};

        double[] offSetX = {0, -20, 20};
        double[] offSetY = {0, -20, 20};
        int selector = (int) towerRotate / 90;
        for (int i = 0; i < 3; i++) {
            Image bulletPic = new Image("file:resources/Bullet.png",
                    20, 20, false, false);
            ImageView bullet = new ImageView(bulletPic);
            List<Point2D> waypoints = new ArrayList<Point2D>();
            waypoints.add(new Point2D(towerX + 30, towerY + 30));
            waypoints.add(new Point2D(towerX + 30 + xAdd[selector] + offSetX[i],
                    towerY + 30 + yAdd[selector] + offSetY[i]));
            Path sharpPath = createSharpPath(waypoints);
            gamePanel.getChildren().add(bullet);

            PathTransition pt = new PathTransition(Duration.millis(1000), sharpPath);

            Projectile newProjectile = new Projectile(bullet);

            DoubleProperty xValue = new SimpleDoubleProperty();
            xValue.bind(bullet.translateXProperty());
            xValue.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    newProjectile.setX((double) t1);
                }
            });
            DoubleProperty yValue = new SimpleDoubleProperty();
            yValue.bind(bullet.translateYProperty());
            yValue.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    newProjectile.setY((double) t1);
                }
            });
            pt.setNode(bullet);
            pt.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gamePanel.getChildren().remove(bullet);
                }
            });

            activeProjectilesReal.add(newProjectile);
            activeAnimations.add(pt);
            pt.play();
        }

    }

    private void spawnProjectileMultiShot(Pane gamePanel, double towerX, double towerY) {
        double[] xAdd = {300, 0, -300, 0};
        double[] yAdd = {0, 300, 0, -300};
        for (int selector = 0; selector < 4; selector++) {
            Image bulletPic = new Image("file:resources/Bullet.png",
                    20, 20, false, false);
            ImageView bullet = new ImageView(bulletPic);
            List<Point2D> waypoints = new ArrayList<Point2D>();
            waypoints.add(new Point2D(towerX + 30, towerY + 30));
            waypoints.add(new Point2D(towerX + 30 + xAdd[selector], towerY + 30 + yAdd[selector]));
            Path sharpPath = createSharpPath(waypoints);
            gamePanel.getChildren().add(bullet);
            PathTransition pt = new PathTransition(Duration.millis(1000), sharpPath);
            pt.setNode(bullet);
            Projectile newProjectile = new Projectile(bullet);
            DoubleProperty xValue = new SimpleDoubleProperty();
            xValue.bind(bullet.translateXProperty());
            xValue.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    newProjectile.setX((double) t1);
                }
            });
            DoubleProperty yValue = new SimpleDoubleProperty();
            yValue.bind(bullet.translateYProperty());
            yValue.addListener(new ChangeListener() {

                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    newProjectile.setY((double) t1);
                }
            });
            pt.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gamePanel.getChildren().remove(bullet);
                }
            });
            activeProjectilesReal.add(newProjectile);
            activeAnimations.add(pt);
            pt.play();
        }
    }

    private Scene buildGameOverWindow() {
        Pane emptyPanel = new Pane();
        Scene endScreen = new Scene(emptyPanel, 1280, 720);

        Image img = new Image("file:resources/gameOver.png");
        BackgroundSize bSize = new BackgroundSize(1280, 720, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        Background bGround = new Background(bImg);
        emptyPanel.setBackground(bGround);

        Button restartButton = new Button("RETRY?");
        restartButton.setPrefWidth(200);
        restartButton.setLayoutX(540);
        restartButton.setLayoutY(600);
        restartButton.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 25));
        restartButton.setStyle("-fx-text-fill: red;");
        restartButton.setBackground(null);
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
                StartScreen newGame = new StartScreen();
                try {
                    newGame.start(programStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Label moneyEarnedLabel = new Label("Money Earned: " + Integer.toString(moneyEarned));
        Label secondsElapsed = new Label("Time Survived: "
                + Integer.toString(timeElapsed) + " seconds");
        Label enemiesSpawnedLabel = new Label("Enemies Spawned: "
                + Integer.toString(enemiesSpawned));
        moneyEarnedLabel.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 30));
        moneyEarnedLabel.setStyle("-fx-text-fill: white;");
        moneyEarnedLabel.setLayoutX(80);
        moneyEarnedLabel.setLayoutY(500);
        enemiesSpawnedLabel.setFont(Font.font("Helvetica",
                FontWeight.LIGHT, FontPosture.ITALIC, 30));
        enemiesSpawnedLabel.setStyle("-fx-text-fill: white;");
        enemiesSpawnedLabel.setLayoutX(500);
        enemiesSpawnedLabel.setLayoutY(500);
        secondsElapsed.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 30));
        secondsElapsed.setStyle("-fx-text-fill: white;");
        secondsElapsed.setLayoutX(900);
        secondsElapsed.setLayoutY(500);

        emptyPanel.getChildren().addAll(restartButton,
                moneyEarnedLabel, enemiesSpawnedLabel, secondsElapsed);
        return endScreen;
    }

    private Scene buildGameWinWindow() {
        Pane emptyPanel = new Pane();
        Scene endScreen = new Scene(emptyPanel, 1280, 720);

        Image img = new Image("file:resources/gameWin.png");
        BackgroundSize bSize = new BackgroundSize(1280, 720, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        Background bGround = new Background(bImg);
        emptyPanel.setBackground(bGround);

        Button restartButton = new Button("RETRY?");
        restartButton.setPrefWidth(200);
        restartButton.setLayoutX(540);
        restartButton.setLayoutY(600);
        restartButton.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 25));
        restartButton.setStyle("-fx-text-fill: red;");
        restartButton.setBackground(null);
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
                StartScreen newGame = new StartScreen();
                try {
                    newGame.start(programStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Label moneyEarnedLabel = new Label("Money Earned: "
                + Integer.toString(moneyEarned));
        Label secondsElapsed = new Label("Time Survived: "
                + Integer.toString(timeElapsed) + " seconds");
        Label enemiesSpawnedLabel = new Label("Enemies Spawned: "
                + Integer.toString(enemiesSpawned));
        moneyEarnedLabel.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 30));
        moneyEarnedLabel.setStyle("-fx-text-fill: white;");
        moneyEarnedLabel.setLayoutX(180);
        moneyEarnedLabel.setLayoutY(670);
        enemiesSpawnedLabel.setFont(Font.font("Helvetica", FontWeight.LIGHT,
                FontPosture.ITALIC, 30));
        enemiesSpawnedLabel.setStyle("-fx-text-fill: white;");
        enemiesSpawnedLabel.setLayoutX(500);
        enemiesSpawnedLabel.setLayoutY(670);
        secondsElapsed.setFont(Font.font("Helvetica", FontWeight.LIGHT, FontPosture.ITALIC, 30));
        secondsElapsed.setStyle("-fx-text-fill: white;");
        secondsElapsed.setLayoutX(830);
        secondsElapsed.setLayoutY(670);

        emptyPanel.getChildren().addAll(restartButton, moneyEarnedLabel,
                enemiesSpawnedLabel, secondsElapsed);
        return endScreen;
    }

    private Scene createEndScreen(Pane gamePanel) {
        Pane endPane = new Pane();
        endPane.getChildren().addAll();
        Scene endMenu = new Scene(endPane, 1280, 720);
        return endMenu;
    }

    public void showAlert(String type) {
        Alert collisionAlert = new Alert(Alert.AlertType.NONE);
        collisionAlert.setAlertType(Alert.AlertType.INFORMATION);
        collisionAlert.setTitle("There was a " + type + " collision");
        collisionAlert.setHeaderText("Please buy another tower(Money was not deducted)");
        collisionAlert.show();
    }



    /**
     * sets money -= tower price, imitates buying a tower with no active game
     */
    public void buyTower() {
        this.money -= towerPrice;
        Tower test = new Tower(0, 0, "Long Shot", "UP");
    }

    /**
     * Places a tower at a given x and y position
     * @param x selected x-value
     * @param y selected y-value
     * @return whether the tower could have been placed in a real game run
     */
    public double[] placeTestTower(double x, double y) {
        Tower test = new Tower(x, y, "Long Shot", "UP");
        double[] pos = new double[2];
        pos[0] = test.getPositionX();
        pos[1] = test.getPositionY();
        return pos;

    }

    /**
     * Places a specified tower type
     * @param type the type of tower to place
     * @return the actual set type of the tower
     */
    public String placeTestTypeTower(String type) {
        Tower test = new Tower(0, 0, type, "UP");
        return test.getType();

    }

    /**
     * Places a specified tower type
     * @param orientation the orientation of the tower
     * @return the actual set type of the tower
     */
    public double placeTestOrientationTower(String orientation) {
        Tower test = new Tower(0, 0, "Long Shot", orientation);
        return test.getOrientation();
    }

    /**
     * Places a tower at the origin and returns its type
     * @param towerType the type of the tower
     * @return string value of the type of tower
     */
    public String placeTestTower(String towerType) {
        Tower test = new Tower(0, 0, towerType, "UP");
        return test.getType();

    }

    private Scene createBuyMenu(Pane gamePanel, Label moneyLabel) {
        Pane buyPanel = new Pane();

        Image longShotPic = new Image("file:resources/LongShot.png");
        Image multiShotPic = new Image("file:resources/MultiShot.png");
        Image spreadShotPic = new Image("file:resources/SpreadShot.png");
        ImageView longShotIcon = new ImageView(longShotPic);
        ImageView multiShotIcon = new ImageView(multiShotPic);
        ImageView spreadShotIcon = new ImageView(spreadShotPic);

        Image img = new Image("file:resources/buyMenuBackground.jpg");
        BackgroundSize bSize = new BackgroundSize(626, 417, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        Background bGround = new Background(bImg);
        buyPanel.setBackground(bGround);
        Button buyLongShot = new Button("   Buy (1) Long Shot ( $" + towerPrice + " )",
                longShotIcon);
        buyLongShot.setLayoutX(113);
        buyLongShot.setLayoutY(10);
        buyLongShot.setPrefWidth(400);
        buyLongShot.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        buyLongShot.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        buyLongShot.setBackground(null);
        buyLongShot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (money >= towerPrice) {
                    Button button = (Button) actionEvent.getSource();
                    Stage programStage = (Stage) button.getScene().getWindow();
                    programStage.close();
                    placeNewTower(gamePanel, "LongShot", moneyLabel);
                    moneyLabel.setText(Integer.toString(money));
                }

            }
        });

        Button buySpreadShot = new Button("  Buy (1) Spread Shot ( $" + towerPrice + " )",
                spreadShotIcon);
        buySpreadShot.setLayoutX(113);
        buySpreadShot.setLayoutY(110);
        buySpreadShot.setPrefWidth(400);
        buySpreadShot.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        buySpreadShot.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        buySpreadShot.setBackground(null);
        buySpreadShot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (money >= towerPrice) {
                    Button button = (Button) actionEvent.getSource();
                    Stage programStage = (Stage) button.getScene().getWindow();
                    programStage.close();
                    placeNewTower(gamePanel, "SpreadShot", moneyLabel);
                    moneyLabel.setText(Integer.toString(money));
                }
            }
        });


        Button buyMultiShot = new Button("   Buy (1) Multi Shot ( $" + towerPrice + " )",
                multiShotIcon);
        buyMultiShot.setLayoutX(113);
        buyMultiShot.setLayoutY(210);
        buyMultiShot.setPrefWidth(400);
        buyMultiShot.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        buyMultiShot.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        buyMultiShot.setBackground(null);
        buyMultiShot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (money >= towerPrice) {
                    Button button = (Button) actionEvent.getSource();
                    Stage programStage = (Stage) button.getScene().getWindow();
                    programStage.close();
                    placeNewTower(gamePanel, "MultiShot", moneyLabel);
                    moneyLabel.setText(Integer.toString(money));
                }
            }
        });

        Button quitButton = new Button("EXIT SHOP");
        quitButton.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 35));
        quitButton.setStyle("-fx-text-fill: red;");
        quitButton.setBackground(null);
        quitButton.setLayoutX(113);
        quitButton.setLayoutY(320);
        quitButton.setPrefWidth(400);
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
            }
        });

        buyPanel.getChildren().addAll(buyLongShot, buySpreadShot, buyMultiShot, quitButton);

        Scene buyMenu = new Scene(buyPanel, 626, 417);
        return buyMenu;

    }

    void upgradeDamage() {
        damage++;
        money -= 100;
    }

    void upgradeRange() {
        range += 50;
        money -= 50;
    }

    private Scene createUpgradeMenu(Label moneyLabel, Label attackLabel, Label rangeLabel) {
        Pane buyPanel = new Pane();

        Image attackUp = new Image("file:resources/attackUp.png", 80, 80, false, false);
        Image rangeUp = new Image("file:resources/rangeUp.png", 80, 80, false, false);
        ImageView attackUpIcon = new ImageView(attackUp);
        ImageView rangeUpIcon = new ImageView(rangeUp);

        Image img = new Image("file:resources/buyMenuBackground.jpg");
        BackgroundSize bSize = new BackgroundSize(626, 417, false, false, false, false);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bSize);
        Background bGround = new Background(bImg);
        buyPanel.setBackground(bGround);
        Button upgradeDmgButton = new Button("   Upgrade Damage ( $100 )", attackUpIcon);
        upgradeDmgButton.setLayoutX(113);
        upgradeDmgButton.setLayoutY(70);
        upgradeDmgButton.setPrefWidth(400);
        upgradeDmgButton.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 20));
        upgradeDmgButton.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        upgradeDmgButton.setBackground(null);
        upgradeDmgButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (money >= 100) {
                    Button button = (Button) actionEvent.getSource();
                    Stage programStage = (Stage) button.getScene().getWindow();
                    programStage.close();
                    upgradeDamage();
                    attackLabel.setText(Integer.toString(damage));
                    moneyLabel.setText(Integer.toString(money));
                }

            }
        });

        Button upgradeRangeButton = new Button("   Upgrade Range ( $50 )", rangeUpIcon);
        upgradeRangeButton.setLayoutX(113);
        upgradeRangeButton.setLayoutY(210);
        upgradeRangeButton.setPrefWidth(400);
        upgradeRangeButton.setFont(Font.font("Helvetica", FontWeight.BOLD,
                FontPosture.REGULAR, 20));
        upgradeRangeButton.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        upgradeRangeButton.setBackground(null);
        upgradeRangeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (money >= 50) {
                    Button button = (Button) actionEvent.getSource();
                    Stage programStage = (Stage) button.getScene().getWindow();
                    programStage.close();
                    upgradeRange();
                    rangeLabel.setText(Integer.toString(range));
                    moneyLabel.setText(Integer.toString(money));
                }
            }
        });


        Button quitButton = new Button("EXIT SHOP");
        quitButton.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 35));
        quitButton.setStyle("-fx-text-fill: red;");
        quitButton.setBackground(null);
        quitButton.setLayoutX(113);
        quitButton.setLayoutY(320);
        quitButton.setPrefWidth(400);
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button button = (Button) actionEvent.getSource();
                Stage programStage = (Stage) button.getScene().getWindow();
                programStage.close();
            }
        });

        buyPanel.getChildren().addAll(upgradeDmgButton, upgradeRangeButton, quitButton);

        Scene upgradeMenu = new Scene(buyPanel, 626, 417);
        return upgradeMenu;

    }

    public Boss getEmptyBoss() {
        Boss boss = new Boss();
        return boss;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getRange() {
        return this.range;
    }
    public boolean testCheckCollisionPath(double x, double y) {
        ArrayList<Rectangle> pathParts = new ArrayList<Rectangle>();
        Rectangle pathPartOne = new Rectangle(160, 480, 80, 240);
        pathParts.add(pathPartOne);
        Rectangle pathPartTwo = new Rectangle(240, 480, 800, 80);
        pathParts.add(pathPartTwo);
        Rectangle pathPartThree = new Rectangle(960, 560, 240, 80);
        pathParts.add(pathPartThree);
        Rectangle pathPartFour = new Rectangle(1120, 400, 80, 160);
        pathParts.add(pathPartFour);
        Rectangle pathPartFive = new Rectangle(400, 320, 800, 80);
        pathParts.add(pathPartFive);
        Rectangle pathPartSix = new Rectangle(240, 240, 240, 80);
        pathParts.add(pathPartSix);
        Rectangle pathPartSeven = new Rectangle(160, 320, 160, 80);
        pathParts.add(pathPartSeven);
        Rectangle pathPartEight = new Rectangle(80, 80, 80, 320);
        pathParts.add(pathPartEight);
        Rectangle pathPartNine = new Rectangle(160, 80, 6 * 80, 80);
        pathParts.add(pathPartNine);
        Rectangle pathPartTen = new Rectangle(7 * 80, 160, 240, 80);
        pathParts.add(pathPartTen);
        Rectangle pathPartEleven = new Rectangle(9 * 80, 80, 240, 80);
        pathParts.add(pathPartEleven);
        Rectangle pathPartTwelve = new Rectangle(11 * 80, 160, 160, 80);
        pathParts.add(pathPartTwelve);
        Rectangle pathPartThirteen = new Rectangle(13 * 80, 80, 240, 240);
        pathParts.add(pathPartThirteen);

        boolean collided = false;

        for (int i = 0; i < pathParts.size(); i++) {
            Rectangle path = pathParts.get(i);

            if (x == path.getX() && y == path.getY()) {
                return true;
            }
        }


        return collided;
    }
    public boolean testTowerOnTowerCollision(double x1, double y1, double x2, double y2) {
        return x1 == x2 && y1 == y2;
    }
    public double getTestEnemyHealth() {
        Enemy testEnemy = new Enemy();
        return testEnemy.getHealth();
    }
    public double getTestBossHealth() {
        Boss testBoss = new Boss();
        return testBoss.getBossHealth();
    }
    public int getMoneyEarned() {
        return this.moneyEarned;
    }


    public class Enemy {
        protected ImageView img;
        protected Double x;
        protected Double y;
        protected Integer health = 3;
        protected Rectangle healthBar;
        public Enemy() {
            this.x = -50.0;
            this.y = -50.0;
        }
        public Enemy(ImageView image, Pane gamePanel) {
            this.x = -50.0;
            this.y = -50.0;
            this.img = image;
            this.healthBar = new Rectangle(this.x, this.y - 10, (this.health * 27), 5);
            this.healthBar.setFill(Color.GREEN);
            gamePanel.getChildren().add(healthBar);
        }
        public void setX(Double x) {
            this.x = x;
        }
        public void setY(Double y) {
            this.y = y;
        }
        public Double getX() {
            return this.x;
        }
        public Double getY() {
            return this.y;
        }
        public Integer getHealth() {
            return this.health;
        }
        public ImageView getImage() {
            return this.img;
        }
        public void updateHealth(Pane gamePanel) {
            this.healthBar.setX(this.x);
            this.healthBar.setY(this.y - 10);
            this.healthBar.setWidth(this.health * 27);
        }
    }

    public class Boss extends Enemy {
        private Integer health = 0;
        private Integer bossHealth = 50;

        public Boss() {
            this.x = -50.0;
            this.y = -50.0;
        }

        public Boss(ImageView image, Pane gamePanel) {
            super(image, gamePanel);
            this.healthBar = new Rectangle(20, 650, (this.bossHealth * 24), 10);
            Label bossHealthLabel = new Label("BOSS HEALTH: ");
            bossHealthLabel.setLayoutX(30);
            bossHealthLabel.setLayoutY(600);
            bossHealthLabel.setFont(Font.font("Helvetica",
                    FontWeight.BOLD, FontPosture.REGULAR, 30));
            bossHealthLabel.setStyle("-fx-text-fill: red;");
            this.healthBar.setFill(Color.GREEN);
            gamePanel.getChildren().addAll(healthBar, bossHealthLabel);
        }

        public double getBossHealth() {
            return this.bossHealth;
        }

        public void updateHealth(Pane gamePanel) {
            this.healthBar.setWidth(this.bossHealth * 24);
        }
    }

    public class Projectile {
        private ImageView img;
        private Double x;
        private Double y;

        public Projectile() {
            this.x = -50.0;
            this.y = -50.0;
        }

        public Projectile(ImageView image) {
            this.x = -50.0;
            this.y = -50.0;
            this.img = image;
        }

        public void setX(Double x) {
            this.x = x;
        }
        public void setY(Double y) {
            this.y = y;
        }

        public Double getX() {
            return this.x;
        }
        public Double getY() {
            return this.y;
        }
        public ImageView getImage() {
            return this.img;
        }

        public boolean checkCollision(Enemy otherImage) {
            //System.out.println("Projectile Pos: " + this.x + " / " + this.y);
            //System.out.println("Enemy Pos: " + otherImage.x + " / " + otherImage.y);
            double rightBound = otherImage.getX() + 40;
            double leftBound = otherImage.getX();
            double topBound = otherImage.getY();
            double bottomBound = otherImage.getY() + 40;
            if (this.x <= rightBound && this.x >= leftBound) {
                if (this.y >= topBound && this.y <= bottomBound) {
                    return true;
                }
            }
            return false;
        }
    }

    public class Tower {
        private double x = 0;
        private double y = 0;
        private double rotation = 0;
        private ImageView image;
        private String type;

        public Tower() {
            this.x = x;
            this.y = y;
            this.rotation = 0;
            this.type = "";
        }

        public Tower(double x, double y, String type, String orientation) {
            this.x = x;
            this.y = y;
            this.type = type;
            switch (orientation) {
            case "UP":
                this.rotation = 0;
                break;
            case "RIGHT":
                this.rotation = 90;
                break;
            case "DOWN":
                this.rotation = 180;
                break;
            case "LEFT":
                this.rotation = 270;
                break;
            default:
                this.rotation = 0;
                break;
            }
        }
        public Tower(double x, double y, String type) {
            Image baseTower = new Image("file:resources/" + type
                    + ".png", 60, 60, false, false);
            this.type = type;
            ImageView baseTowerImage = new ImageView(baseTower);
            this.image = baseTowerImage;
            activeTowerList.add(baseTowerImage);
        }
        public Tower(double x, double y, Pane gamePanel, double rotation, String type) {
            Image baseTower;
            if (type.equals("longShot")) {
                baseTower = new Image("file:resources/LongShot.png",
                        60, 60, false, false);
                this.type = type;
            } else if (type.equals("multiShot")) {
                baseTower = new Image("file:resources/MultiShot.png",
                        60, 60, false, false);
                this.type = type;
            } else if (type.equals("spreadShot")) {
                baseTower = new Image("file:resources/SpreadShot.png",
                        60, 60, false, false);
                this.type = type;
            } else {
                baseTower = new Image("file:resources/BaseTower.png",
                        60, 60, false, false);
                this.type = "baseTower";
            }

            ImageView baseTowerImage = new ImageView(baseTower);
            this.image = baseTowerImage;
            baseTowerImage.setLayoutX(x - 40);
            baseTowerImage.setLayoutY(y - 40);
            baseTowerImage.setRotate(rotation);
            this.rotation = rotation;

            if (gamePanel != null) {
                activeTowerList.add(baseTowerImage);
                gamePanel.getChildren().add(baseTowerImage);
            }
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setRotation(double rotation) {
            this.rotation = rotation;
        }


        public double getPositionX() {
            return this.x;
        }

        public double getPositionY() {
            return this.x;
        }

        public String getType() {
            return this.type;
        }

        public double getOrientation() {
            return this.rotation;
        }

        private boolean checkCollisionPath() {
            ArrayList<Rectangle> pathParts = new ArrayList<Rectangle>();
            Rectangle pathPartOne = new Rectangle(160, 480, 80, 240);
            pathParts.add(pathPartOne);
            Rectangle pathPartTwo = new Rectangle(240, 480, 800, 80);
            pathParts.add(pathPartTwo);
            Rectangle pathPartThree = new Rectangle(960, 560, 240, 80);
            pathParts.add(pathPartThree);
            Rectangle pathPartFour = new Rectangle(1120, 400, 80, 160);
            pathParts.add(pathPartFour);
            Rectangle pathPartFive = new Rectangle(400, 320, 800, 80);
            pathParts.add(pathPartFive);
            Rectangle pathPartSix = new Rectangle(240, 240, 240, 80);
            pathParts.add(pathPartSix);
            Rectangle pathPartSeven = new Rectangle(160, 320, 160, 80);
            pathParts.add(pathPartSeven);
            Rectangle pathPartEight = new Rectangle(80, 80, 80, 320);
            pathParts.add(pathPartEight);
            Rectangle pathPartNine = new Rectangle(160, 80, 6 * 80, 80);
            pathParts.add(pathPartNine);
            Rectangle pathPartTen = new Rectangle(7 * 80, 160, 240, 80);
            pathParts.add(pathPartTen);
            Rectangle pathPartEleven = new Rectangle(9 * 80, 80, 240, 80);
            pathParts.add(pathPartEleven);
            Rectangle pathPartTwelve = new Rectangle(11 * 80, 160, 160, 80);
            pathParts.add(pathPartTwelve);
            Rectangle pathPartThirteen = new Rectangle(13 * 80, 80, 240, 240);
            pathParts.add(pathPartThirteen);

            boolean collided = false;
            for (int i = 0; i < pathParts.size(); i++) {
                if (this.image.getBoundsInParent().intersects(
                        pathParts.get(i).getBoundsInParent())) {
                    collided = true;
                    showAlert("Path");
                    System.out.println("PATH COLLISION");
                }
            }
            for (int i = 0; i < activeTowerList.size() - 1; i++) {
                if (this.image.getBoundsInParent().intersects(
                        activeTowerList.get(i).getBoundsInParent())) {
                    collided = true;
                    showAlert("Tower");
                    System.out.println("TOWER COLLISION");
                }
            }
            return collided;
        }
        public ImageView getImage() {
            return this.image;
        }
    }
}
