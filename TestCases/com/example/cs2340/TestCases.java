package com.example.cs2340;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.example.cs2340.MainGame.checkIfInRange;
import static org.junit.jupiter.api.Assertions.*;

class TestCases {

    private MainGame easyGame;
    private MainGame mediumGame;
    private MainGame hardGame;

    @BeforeEach
    void setUp() {
        easyGame = new MainGame(1);
        mediumGame = new MainGame(2);
        hardGame  = new MainGame(3);

    }

    /**
     * M2 Test Different Difficulty levels save and that a difficulty of
     * easy game != medium game != hard game
     * @result test difficulty is varied
     *
     */
    @Test
    void getGameDifficulty() {
        assertEquals(easyGame.getGameDifficulty(), 1);
        assertEquals(mediumGame.getGameDifficulty(), 2);
        assertEquals(hardGame.getGameDifficulty(), 3);
        assertNotEquals(easyGame.getGameDifficulty(), mediumGame.getGameDifficulty());
        assertNotEquals(easyGame.getGameDifficulty(), hardGame.getGameDifficulty());
        assertNotEquals(mediumGame.getGameDifficulty(), hardGame.getGameDifficulty());
    }
    /**
     * M2 Test Different Health levels save and that a health
     * of easy game != medium game != hard game
     * @result test health is varied
     *
     */
    @Test
    void getGameHealth() {
        assertEquals(easyGame.getHealth(), 150);
        assertEquals(mediumGame.getHealth(), 100);
        assertEquals(hardGame.getHealth(), 50);
        assertNotEquals(easyGame.getHealth(), mediumGame.getHealth());
        assertNotEquals(easyGame.getHealth(), hardGame.getHealth());
        assertNotEquals(mediumGame.getHealth(), hardGame.getHealth());
    }
    /**
     * M2 Test Different Money is save and that a money of easy game != medium game != hard game
     * @result test money is varied
     *
     */
    @Test
    void getMoney() {
        assertEquals(easyGame.getMoney(), 3000);
        assertEquals(mediumGame.getMoney(), 2000);
        assertEquals(hardGame.getMoney(), 1000);
        assertNotEquals(easyGame.getMoney(), mediumGame.getMoney());
        assertNotEquals(easyGame.getMoney(), hardGame.getMoney());
        assertNotEquals(mediumGame.getMoney(), hardGame.getMoney());
    }

    /**
     * Tests to see if different tower prices correspond to respective difficulties.
     * - If difficulty is set to 1 (easy): all tower prices are 300
     * - If difficulty is set to 2 (medium): all tower prices are 400
     * - If difficulty is set to 3 (hard): all tower prices are 500
     * - Easy/Medium/Hard respective tower price does not equal each other
     */
    @Test
    void getTowerPrice() {
        assertEquals(easyGame.getTowerPrice(), 300);
        assertEquals(mediumGame.getTowerPrice(), 400);
        assertEquals(hardGame.getTowerPrice(), 500);
        assertNotEquals(easyGame.getTowerPrice(), mediumGame.getTowerPrice());
        assertNotEquals(easyGame.getTowerPrice(), hardGame.getTowerPrice());
        assertNotEquals(mediumGame.getTowerPrice(), hardGame.getTowerPrice());
    }

    /**
     * Tests to see if buying towers at different difficulties with their respective prices
     * successfully updates the total money the player has.
     * - Easy game: buying 1 tower would result in the player having $2700
     * - Medium game: buying 2 towers would result in the player having $1200
     * - Hard game: buying 2 towers would result in the player having $0
     * - Does not test: negative money, towers at different prices
     */
    @Test
    void buyTowerUpdatesMoney() {
        assertEquals(easyGame.getMoney(), 3000);
        assertEquals(mediumGame.getMoney(), 2000);
        assertEquals(hardGame.getMoney(), 1000);

        easyGame.buyTower();
        mediumGame.buyTower();
        mediumGame.buyTower();
        hardGame.buyTower();
        hardGame.buyTower();

        assertEquals(easyGame.getMoney(), 2700);
        assertEquals(mediumGame.getMoney(), 1200);
        assertEquals(hardGame.getMoney(), 0);
    }

    /**
     * Tests to see if tower position is set correctly to the specified placement value
     */
    @Test
    void checkTowerPos() {
        double[] testPosition = new double[2];
        testPosition[0] = 30;
        testPosition[1] = 30;
        double[] actualPos = mediumGame.placeTestTower(30, 30);
        for (int i = 0; i < actualPos.length; i++) {
            assertEquals(testPosition[i], actualPos[i]);
        }
    }

    /**
     * Tests to see if tower type is set to the specified value
     */
    @Test
    void checkTowerType() {
        assertEquals(easyGame.placeTestTypeTower("Long Shot"), "Long Shot");
        assertEquals(mediumGame.placeTestTypeTower("Multi Shot"), "Multi Shot");
        assertEquals(hardGame.placeTestTypeTower("Spread Shot"), "Spread Shot");
    }

    /**
     * Tests to see if tower is set to face the specified direction
     */
    @Test
    void checkTowerOrientation() {
        assertEquals(easyGame.placeTestOrientationTower("UP"), 0.0);
        assertEquals(easyGame.placeTestOrientationTower("RIGHT"), 90.0);
        assertEquals(easyGame.placeTestOrientationTower("LEFT"), 270.0);
        assertEquals(hardGame.placeTestOrientationTower("DOWN"), 180.0);
    }

    /**
     * Tests to see if path of enemies compiles without fail
     */
    @Test
    void checkPathCreation() {
        List<javafx.geometry.Point2D> points = new ArrayList<javafx.geometry.Point2D>();
        points.add(new Point2D(2, 10));
        assertNotNull(easyGame.testPath(points));
        assertNotNull(mediumGame.testPath(points));
        assertNotNull(hardGame.testPath(points));
    }

    /**
     * Tests to see if waypoints of path are read from file correctly
     */
    @Test
    void checkWayPointCreation() {
        List<Point2D> path = new ArrayList<>();
        path.add(new Point2D( 200, 680));
        path.add(new Point2D( 200, 520));
        path.add(new Point2D( 1000, 520));
        path.add(new Point2D( 1000, 600));
        path.add(new Point2D( 1160, 600));
        path.add(new Point2D( 1160, 360));
        path.add(new Point2D( 440, 360));
        path.add(new Point2D( 440, 280));
        path.add(new Point2D( 280, 280));
        path.add(new Point2D( 280, 360));
        path.add(new Point2D( 120, 360));
        path.add(new Point2D( 120, 120));
        path.add(new Point2D( 600, 120));
        path.add(new Point2D( 600, 200));
        path.add(new Point2D( 760, 200));
        path.add(new Point2D( 760, 120));
        path.add(new Point2D( 920, 120));
        path.add(new Point2D( 920, 200));
        path.add(new Point2D( 1080, 200));

        assertEquals(path, easyGame.getWayPoints());
        assertEquals(path, mediumGame.getWayPoints());
        assertEquals(path, hardGame.getWayPoints());
    }


    /**
     * Test to ensure that projectiles are spawned correctly
     */
    @Test
    void checkProjectileCreation() {
        int spawnNumb = 10;
        int spawnedProjectiles = easyGame.testProjectileSpawn(spawnNumb);
        assertEquals(spawnNumb, spawnedProjectiles);
    }

    /**
     * Test to ensure that waves are spawned with the correct enemy amounts
     */
    @Test
    void checkWaveSpawn() {
        int waveSize = 30;
        int spawnNumber = easyGame.testWaveSpawn(waveSize);
        assertEquals(waveSize, spawnNumber);
    }

    /**
     * Test to ensure that waves are spawned with the correct boss amounts
     */
    @Test
    void checkBossSpawn() {
        int waveSize = 1;
        int spawnNumber = easyGame.testBossSpawn(waveSize);
        assertEquals(waveSize, spawnNumber);
    }

    /**
     * Test to ensure correct damage and range values for tower spawn (before upgrade)
     */
    @Test
    void checkStartingTowerAttributes() {
        assertEquals(1, easyGame.getDamage());
        assertEquals(1, mediumGame.getDamage());
        assertEquals(1, hardGame.getDamage());
        assertEquals(200, easyGame.getRange());
        assertEquals(200, mediumGame.getRange());
        assertEquals(200, hardGame.getRange());
    }

    /**
     * Tests direct collision between enemy and projectile.
     */
    @Test
    void checkProjectileCollision() {
        MainGame.Projectile newProjectile1 = easyGame.getEmptyProjectile();
        newProjectile1.setX((double) 100);
        newProjectile1.setY((double) 100);

        MainGame.Enemy enemy1 = easyGame.getEmptyEnemy();
        enemy1.setX(100.0);
        enemy1.setY(100.0);

        MainGame.Projectile newProjectile2 = mediumGame.getEmptyProjectile();
        newProjectile2.setX((double) 10);
        newProjectile2.setY((double) 10);

        assertEquals(easyGame.testProjectileCollision(newProjectile1, enemy1), true);
        assertEquals(mediumGame.testProjectileCollision(newProjectile2, enemy1), false);
    }

    /**
     * Tests enemy health upon collision between enemy and projectile.
     */
    @Test
    void checkEnemyHealthUponCollision() {
        MainGame.Projectile newProjectile1 = easyGame.getEmptyProjectile();
        newProjectile1.setX((double) 100);
        newProjectile1.setY((double) 100);

        MainGame.Enemy enemy1 = easyGame.getEmptyEnemy();
        enemy1.setX(100.0);
        enemy1.setY(100.0);

        //testProjectileCollision simulates actual collision and reduces enemy health by 1
        assertEquals(easyGame.testProjectileCollision(newProjectile1, enemy1), true);
        assertEquals(enemy1.getHealth(), 2);
    }

    /**
     * Tests if the range checker functions properly.
     */
    @Test
    void testRangeChecker() {
        assertEquals(checkIfInRange(100, 100, 150, 150, 200), true);
        assertEquals(checkIfInRange(100, 100, 400, 400, 200), false);
    }

    /**
     * Tests if range is updated
     */
    @Test
    void testRangeUpgrade() {
        MainGame.Tower tower1 = easyGame.getEmptyTower();
        tower1.setX(100);
        tower1.setY(100);

        MainGame.Enemy enemy1 = easyGame.getEmptyEnemy();
        enemy1.setX(251.0);
        enemy1.setY(251.0);

        assertEquals(checkIfInRange(tower1.getPositionX(), tower1.getPositionY(), enemy1.getX(), enemy1.getY(), easyGame.getRange()), false);
        easyGame.upgradeRange();
        assertEquals(easyGame.getRange(), 250);
        assertEquals(checkIfInRange(tower1.getPositionX(), tower1.getPositionY(), enemy1.getX(), enemy1.getY(), easyGame.getRange()), true);
    }

    /**
     * Tests if damage is updated
     */
    @Test
    void testDamageUpgrade() {
        MainGame.Projectile newProjectile1 = easyGame.getEmptyProjectile();
        newProjectile1.setX((double) 100);
        newProjectile1.setY((double) 100);

        MainGame.Enemy enemy1 = easyGame.getEmptyEnemy();
        enemy1.setX(100.0);
        enemy1.setY(100.0);

        easyGame.upgradeDamage();
        assertEquals(easyGame.getDamage(), 2);

        assertEquals(easyGame.testProjectileCollision(newProjectile1, enemy1), true);
        assertEquals(enemy1.getHealth(), 1);
    }
    /**
     * Tests if Tower is on Path
     */
    @Test void testTowerOnPath(){
        double[] actualPos = mediumGame.placeTestTower(80, 80);
        assertEquals(easyGame.testCheckCollisionPath(actualPos[0],actualPos[1]),true);
        actualPos = mediumGame.placeTestTower(40, 80);
        assertEquals(easyGame.testCheckCollisionPath(actualPos[0],actualPos[1]),false);
        actualPos = mediumGame.placeTestTower(160, 80);
        assertEquals(easyGame.testCheckCollisionPath(actualPos[0],actualPos[1]),false);
    }
    /**
     * Tests if Tower is on Tower
     */
    @Test void testTowerOnTower(){
        double[] t1Location = mediumGame.placeTestTower(160, 80);
        double[] t2Location = mediumGame.placeTestTower(80, 80);
        double[] t3Location = mediumGame.placeTestTower(30, 80);
        double[] t4Location = mediumGame.placeTestTower(75, 80);
        assertTrue(mediumGame.testTowerOnTowerCollision(t1Location[0],t1Location[1],t1Location[0],t1Location[1]));
        assertFalse(mediumGame.testTowerOnTowerCollision(t1Location[0],t1Location[1],t2Location[0],t2Location[1]));
        assertFalse(mediumGame.testTowerOnTowerCollision(t2Location[0],t2Location[1],t3Location[0],t3Location[1]));
        assertFalse(mediumGame.testTowerOnTowerCollision(t2Location[0],t2Location[1],t4Location[0],t4Location[1]));
        assertFalse(mediumGame.testTowerOnTowerCollision(t3Location[0],t3Location[1],t4Location[0],t4Location[1]));


    }
    /**
     * Tests if boss health is greater than regular enemy health
     */
    @Test void testBossHealthGreaterThanEnemy(){
        assertTrue(easyGame.getTestBossHealth()>easyGame.getTestEnemyHealth());
    }
    @Test void testMoneyEarnedinGame(){
        int start = easyGame.getMoneyEarned();
        easyGame.startCombat();
        easyGame.startMoneyGeneration();
        int end = easyGame.getMoneyEarned();
        assertTrue(end > start);
         start = mediumGame.getMoneyEarned();
        mediumGame.startCombat();
        mediumGame.startMoneyGeneration();
         end = mediumGame.getMoneyEarned();
        assertTrue(end > start);
         start = hardGame.getMoneyEarned();

        hardGame.startMoneyGeneration();
         end = hardGame.getMoneyEarned();
        assertFalse(end > start);
    }



    /**
     * Tests if enemy attack updates monument health.
     */
    /*
    @Test4
    void enemyAttacksMonument() {
        easyGame.enemyAttack(1);
        assertEquals(easyGame.getHealth(), 149);

        mediumGame.enemyAttack(2);
        assertEquals(mediumGame.getHealth(), 98);

        hardGame.enemyAttack(3);
        assertEquals(hardGame.getHealth(), 47);
    }

     */

    /**
     * Tests if combat has started.
     */
    /*
    @Test void inCombat() {
        easyGame.startCombat();
        easyGame.enemyAttack(150);
        assertEquals(easyGame.isInCombat(), false);

        mediumGame.startCombat();
        easyGame.enemyAttack(10);
        assertEquals(mediumGame.isInCombat(), true);

        assertEquals(hardGame.isInCombat(), false);

    }

    /**
     * Tests if game over occurs at the correct amount of life loss.
     */
    /*
    @Test void correctGameOver() {
        easyGame.startCombat();
        for (int i = 0; i < 150; i++) {
            assertEquals(easyGame.isInCombat(), true);
            easyGame.enemyAttack(1);
        }
        assertEquals(easyGame.isInCombat(), false);

        mediumGame.startCombat();
        for (int i = 0; i < 100; i++) {
            assertEquals(mediumGame.isInCombat(), true);
            mediumGame.enemyAttack(1);
        }
        assertEquals(mediumGame.isInCombat(), false);

        hardGame.startCombat();
        for (int i = 0; i < 50; i++) {
            assertEquals(hardGame.isInCombat(), true);
            hardGame.enemyAttack(1);
        }
        assertEquals(hardGame.isInCombat(), false);
    }

    /**
     * Tests if Tower can be placed on path.
     */
    /*
    @Test void combatStarts(){
        assertEquals(easyGame.isInCombat(),false);
        assertEquals(mediumGame.isInCombat(),false);
        assertEquals(hardGame.isInCombat(),false);
        easyGame.startCombat();
        mediumGame.startCombat();

        assertEquals(easyGame.isInCombat(),true);
        assertEquals(mediumGame.isInCombat(),true);
        assertEquals(hardGame.isInCombat(),false);
    }




     */


}