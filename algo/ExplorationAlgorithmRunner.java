package algo;

import entities.*;
import connection.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static constant.EntitiesConstants.*;


/**
 * Algorithm for exploration phase (full exploration)
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int CALIBRATION_LIMIT = 3;
    private static final int RIGHT_LIMIT = 3;
    private int calibrationCounter = 0;
    private int rightCounter = 0;
    private ArrayList<String> actionString = new ArrayList<>();
    private ArrayList<String> pattern = new ArrayList<>(Arrays.asList("L","M","L","M","L","M","L","M"));
    public ExplorationAlgorithmRunner(int speed){
        sleepDuration = 1000 / speed;
    }

    @Override
    public void run(GridMap grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();
        //islandTest(robot);
        if (realRun) {
            grid.reset();
            String msg = SocketMgr.getInstance().receiveMessage(false);
            while (!msg.equals("startexp")) {
                msg = SocketMgr.getInstance().receiveMessage(false);
                //SocketMgr.getInstance().sendMessage("A","M");
            }

        }
        // SELECT EITHER ONE OF THE METHODS TO RUN ALGORITHMS.
        runExplorationAlgorithmThorough(grid, robot, realRun);
//        runExplorationLeftWall(grid, robot, realRun);

        // CALIBRATION AFTER EXPLORATION
        calibrateAndTurn(robot, grid, realRun);

//        // GENERATE MAP DESCRIPTOR, SEND TO ANDROID
//        String part1 = grid.generateDescriptorPartOne();
//        String part2 = grid.generateDescriptorPartTwo();
//        SocketMgr.getInstance().sendMessage(TARGET_ANDROID, CommMgr.generateFinalDescriptor(part1, part2));
    }

    private void islandTest(Robot robot) {
        robot.setPosX(4);
        robot.setPosY(16);
        robot.setOrientation(EAST);
    }

    private void calibrateAndTurn(Robot robot, GridMap grid, boolean realRun) {
        if (realRun) {
            while (robot.getOrientation() != NORTH) {
                robot.turn(LEFT);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                senseAndUpdateAndroid(robot,grid, realRun);
            }
            //Do a reset calibration
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "P");
            senseAndUpdateAndroid(robot,grid, realRun);
//            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
//            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
//            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
//            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
        }
    }

    private void runExplorationAlgorithmThorough(GridMap grid, Robot robot, boolean realRun) {
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;

        // CALIBRATE & SENSE
        calibrationCounter = 0;
//        if (realRun) {
//            calibrateAtStart();
//        }
        robot.sense(realRun);

        // INITIAL UPDATE OF MAP TO ANDROID

        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    CommMgr.generateMapDescriptorMsg(grid.generateDescriptorPartOne(),grid.generateDescriptorPartTwo(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getOrientation()));

        // MAIN LOOP (LEFT-WALL-FOLLOWER)
        while (!endZoneFlag || !startZoneFlag) {
            moveAndSense(grid, robot, realRun);
            takePhoto(robot,grid,realRun);
            System.out.println(calibrationCounter+" "+endZoneFlag+ " "+startZoneFlag+ " "+robot.getPosX()+","+robot.getPosY());
            if (GridMap.isInEndZone(robot.getPosX(), robot.getPosY())) {
                endZoneFlag = true;
            }
            if (endZoneFlag && GridMap.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                startZoneFlag = true;
            }

            // IF EXPLORATION COMPLETED & HAVE NOT GO BACK TO START, FIND THE FASTEST PATH BACK TO START POINT
            if(grid.checkExploredPercentage() == 100 && !startZoneFlag){
                findPathAndMove(grid, robot, START_X, START_Y, realRun);

                // ADD FINAL MOVE
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");
                robot.move();
                senseAndUpdateAndroid(robot, grid, realRun);
                if (!realRun)
                    stepTaken();
                actionString.add("M");

                if (endZoneFlag && GridMap.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                    startZoneFlag = true;
                }
                //AT THIS STAGE, ROBOT SHOULD HAVE RETURNED BACK TO START POINT.
            }
        }

        //
        // BELOW IS THE 2ND EXPLORATION !!!!!!!!!!!!!
        //
        // INITIALISE NEW GridMap TO PREVENT CHECKING PREVIOUSLY EXPLORED CELLS.
        GridMap exploreChecker = new GridMap();
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                exploreChecker.setExplored(x, y, grid.getIsExplored(x, y));
                exploreChecker.setIsObstacle(x, y, grid.getIsObstacle(x, y));
            }
        }

        // SWEEPING THROUGH UNEXPLORED, BUT REACHABLE CELLS WITHIN ARENA.
        if (grid.checkExploredPercentage() < 100.0){ // CHECK FOR UNEXPLORED CELLS
            System.out.println("NOT FULLY EXPLORED, DOING A 2ND RUN!");

            for (int y = MAP_ROWS - 1; y >= 0; y--) {
                for (int x = MAP_COLS - 1; x >= 0; x--) {
                    // CHECK FOR UNEXPLORED CELLS && CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                    if (!grid.getIsExplored(x, y) && (findPathAndMove(grid, robot,x+1 , y, realRun)
                            ||findPathAndMove(grid, robot,x-1 , y, realRun)
                            ||findPathAndMove(grid, robot,x , y+1, realRun)
                            ||findPathAndMove(grid, robot,x , y-1, realRun))) {
                        moveFor2ndExp(exploreChecker, grid, robot, realRun);

                        if (grid.checkExploredPercentage() == 100) { // IF FULLEST EXPLORED, EXIT AND GO TO START
                            break;
                        }
                    }
                }
            }
        }
        /*
        FASTEST PATH BACK TO START ONCE THE EXPLORATION IS COMPLETED.
        */
        if(!GridMap.isInStartZone(robot.getPosX()+2, robot.getPosY()+2)){
            findPathAndMove(grid, robot, START_X, START_Y, realRun);
            // ADD FINAL MOVE
            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");
            robot.move();
            senseAndUpdateAndroid(robot, grid, realRun);
            if (!realRun)
                stepTaken();
            actionString.add("M");
        }
        System.out.println("EXPLORATION COMPLETED!");
        System.out.println("PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
        //robot.reset();
    }

    private void phantomBlockTest(Robot robot, GridMap map) {
        if (robot.getPosX()==4 && robot.getPosY() ==12){
            map.setIsObstacle(3,13,false);
        }
    }

    private void breakIslandLoop(Robot robot, boolean realRun ,GridMap grid){
        //Break Isolated Island Loop
        if (grid.isInIslandLoop(robot.getPosX(),robot.getPosY(),robot.getOrientation())){
            System.out.println("Break Isolated Island Loop");
            while (!robot.isObstacleAhead()){
                // MOVE FORWARD
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");
                robot.move();
                senseAndUpdateAndroid(robot, grid, realRun);
                if (!realRun)
                    stepTaken();
                actionString.add("M");
            }
            if (!robot.isObstacleRight()){
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                senseAndUpdateAndroid(robot, grid, realRun);
                if (!realRun)
                    stepTaken();
                actionString.add("R");
            }
        }
    }

    private void breakLoop(Robot robot, boolean realRun ,GridMap grid) {
        for (int i=0; i<8;i++){
            if (!actionString.get(actionString.size()-8+i).equals(pattern.get(i)))
                return;
        }

        // Roll Back one step
        if (realRun) {
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "D");
            senseAndUpdateAndroid(robot, grid, realRun);
        }
        robot.moveBackward();
        actionString.add("D");

        if (!realRun)
            stepTaken();
    }

    private boolean findPathAndMove(GridMap grid, Robot robot, int x, int y, boolean realRun) {
        Robot fakeRobot = new Robot(grid, new ArrayList<>());
        fakeRobot.setPosX(robot.getPosX());
        fakeRobot.setPosY(robot.getPosY());
        fakeRobot.setOrientation(robot.getOrientation());
        List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), x, y, grid, fakeRobot);

        if (returnPath != null) {
            //Remove last move
            if (returnPath.get(returnPath.size()-1).equals("M")){
                returnPath.remove(returnPath.size()-1);
            } else {
                returnPath.remove(returnPath.size()-2);
            }

            System.out.println("A* Algorithm finished, executing actions");
            System.out.println(returnPath.toString());
            if (realRun) {
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setOrientation(robot.getOrientation());
                String compressed = AlgorithmRunner.compressPathForExploration(returnPath, fakeRobot);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "F");
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressed);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "F");
                senseAndUpdateAndroid(robot, grid, realRun);
            }

            for (String action : returnPath) {
                if (action.equals("M")) {
                    robot.move();
                } else if (action.equals("L")) {
                    robot.turn(LEFT);
                } else if (action.equals("R")) {
                    robot.turn(RIGHT);
                } else if (action.equals("U")) {
                    robot.turn(LEFT);
                    robot.turn(LEFT);
                }
//                        robot.sense(realRun);
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                            CommMgr.generateMapDescriptorMsg(grid.generateDescriptorPartOne(),grid.generateDescriptorPartTwo(),
                                    robot.getCenterPosX(), robot.getCenterPosY(), robot.getOrientation()));
                if (!realRun)
                    stepTaken();
            }
            return true;
        } else {
            System.out.println("Fastest path not found!");
            return false;
        }
    }

    private void moveAndSense(GridMap grid, Robot robot, boolean realRun) {
        boolean turned = leftWallFollower(robot, grid, realRun);

        if (turned) {
            takePhoto(robot,grid,realRun);
            senseAndUpdateAndroid(robot, grid, realRun);
        }

        // MOVE FORWARD
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");

        robot.move();
        actionString.add("M");

        if (!realRun)
            stepTaken();

        //phantomBlockTest(robot,grid);

        // SENSE BEFORE CALIBRATION
        senseAndUpdateAndroid(robot, grid, realRun);

        //DOUBLE CHECK IF FIND UNCERTAINTY ON THE LEFT
        if (robot.isMapChanged()){
            System.out.println("Double check for uncertain left wall..");

//            // Choose to rollback
//            if (realRun)
//                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "D");
//            robot.moveBackward();
            // Use front sensors to check
            if (realRun) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                senseAndUpdateAndroid(robot, grid, realRun);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
            }
            robot.turn(LEFT);
            if (!realRun)
                stepTaken();
            robot.turn(RIGHT);

            if (!realRun)
                stepTaken();
            senseAndUpdateAndroid(robot, grid, realRun);
        }

//        rightCounter++;
//        // Extra check for right sie
//        if (rightCounter >= RIGHT_LIMIT ) {
//            System.out.println("Extra right sense");
//
//            if(realRun) {
//                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
//                senseAndUpdateAndroid(robot, grid, realRun);
//                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
//                senseAndUpdateAndroid(robot, grid, realRun);
//                rightCounter = 0;
//            }
//            else{
//                robot.turn(RIGHT);
//                stepTaken();
//                robot.turn(LEFT);
//                stepTaken();
//                rightCounter = 0;
//            }
//        }

        // CALIBRATION
        if (realRun) {
            calibrationCounter++;
            // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
            if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "A");
                calibrationCounter = 0;

                // SENSE FOR RIGHT SIDE
                robot.turn(RIGHT);
                senseAndUpdateAndroid(robot, grid, realRun);
                robot.turn(LEFT);
            }
            // OTHERWISE CALIBRATE LEFT
            if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "B");
                calibrationCounter = 0;

                // SENSE FOR RIGHT SIDE
                robot.turn(RIGHT);
                senseAndUpdateAndroid(robot, grid, realRun);
                robot.turn(LEFT);
            } else if(calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateFront()) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                calibrationCounter = 0;

                // SENSE FOR RIGHT SIDE
                robot.turn(RIGHT);
                senseAndUpdateAndroid(robot, grid, realRun);
                robot.turn(LEFT);
            }
        }
        else {
            //SHOW CALIBRATION PROCESS ON SIMULATOR
            // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
            calibrationCounter++;
            if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                System.out.println("CALIBRATION FRONT LEFT");
                robot.turn(LEFT);
                stepTaken();
                robot.turn(RIGHT);
                calibrationCounter = 0;
            }
            // OTHERWISE CALIBRATE LEFT
            if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                System.out.println("CALIBRATION LEFT");
                robot.turn(LEFT);
                stepTaken();
                robot.turn(RIGHT);
                calibrationCounter = 0;
            }else if(calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateFront()) {
                System.out.println("CALIBRATION FRONT");
                calibrationCounter = 0;

                // SENSE BEFORE CALIBRATION
                senseAndUpdateAndroid(robot, grid, realRun);
            }
        }

        // Aditional Checks for loops
        breakIslandLoop(robot, realRun, grid);
        if (actionString.size()>8)
            breakLoop(robot, realRun, grid);
    }

    private void moveFor2ndExp(GridMap exploreChecker,GridMap grid, Robot robot, boolean realRun) {
        // Make last move and sense
        if (realRun)
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");
        robot.move();
        senseAndUpdateAndroid(robot, grid, realRun);
        if (!realRun)
            stepTaken();
        actionString.add("M");
        calibrationCounter++;

        int x;
        int y;
        while (true){
            int found = 0;
            x = robot.getPosX();
            y = robot.getPosY();
            for (int i = 0; i<3;i++){
                if (robot.getOrientation()==NORTH){
                    if (exploreChecker.getIsExplored(x+i,y-1) != grid.getIsExplored(x+i,y-1)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y-2) != grid.getIsExplored(x+i,y-2)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x-1,y+i) != grid.getIsExplored(x-1,y+i)){
                        found = 2;
                        break;
                    } else if (exploreChecker.getIsExplored(x-2,y+i) != grid.getIsExplored(x-2,y+i)){
                        found = 2;
                        break;
                    }
                } else if (robot.getOrientation()==SOUTH){
                    if (exploreChecker.getIsExplored(x+i,y+3) != grid.getIsExplored(x+i,y+3)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y+4) != grid.getIsExplored(x+i,y+4)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+3,y+i) != grid.getIsExplored(x+3,y+i)){
                        found = 2;
                        break;
                    } else if (exploreChecker.getIsExplored(x+4,y+i) != grid.getIsExplored(x+4,y+i)){
                        found = 2;
                        break;
                    }
                } else if (robot.getOrientation()==EAST){
                    if (exploreChecker.getIsExplored(x+3,y+i) != grid.getIsExplored(x+3,y+i)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+4,y+i) != grid.getIsExplored(x+4,y+i)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y-1) != grid.getIsExplored(x+i,y-1)){
                        found = 2;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y-2) != grid.getIsExplored(x+i,y-2)){
                        found = 2;
                        break;
                    }
                } else if (robot.getOrientation()==WEST){
                    if (exploreChecker.getIsExplored(x-1,y+i) != grid.getIsExplored(x-1,y+i)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x-2,y+i) != grid.getIsExplored(x-2,y+i)){
                        found = 1;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y+3) != grid.getIsExplored(x+i,y+3)){
                        found = 2;
                        break;
                    } else if (exploreChecker.getIsExplored(x+i,y+4) != grid.getIsExplored(x+i,y+4)){
                        found = 2;
                        break;
                    }
                }
            }


            if (found == 0 ) {
                for (int i = 0; i<3;i++) {
                    if (robot.getOrientation() == WEST) {
                        if (!grid.getIsExplored(x + i, y - 1)) {
                            found = 3;
                            break;
                        } else if (!grid.getIsExplored(x + i, y - 2)) {
                            found = 3;
                            break;
                        }
                    } else if (robot.getOrientation() == EAST) {
                        if (!grid.getIsExplored(x + i, y + 3)) {
                            found = 3;
                            break;
                        } else if (!grid.getIsExplored(x + i, y + 4)) {
                            found = 3;
                            break;
                        }
                    } else if (robot.getOrientation() == NORTH) {
                        if (!grid.getIsExplored(x + 3, y + i)) {
                            found = 3;
                            break;
                        } else if (!grid.getIsExplored(x + 4, y + i)) {
                            found = 3;
                            break;
                        }
                    } else if (robot.getOrientation() == SOUTH) {
                        if (!grid.getIsExplored(x - 1, y + i)) {
                            found = 3;
                            break;
                        } else if (!grid.getIsExplored(x - 2, y + i)) {
                            found = 3;
                            break;
                        }
                    }
                }
                if (found == 3) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.turn(RIGHT);
                    senseAndUpdateAndroid(robot, grid, realRun);
                    if (!realRun)
                        stepTaken();
                    actionString.add("R");
                }
            } else if (found == 2 && !robot.isObstacleLeft()){
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                senseAndUpdateAndroid(robot, grid, realRun);
                if (!realRun)
                    stepTaken();
                actionString.add("L");
            }

            // Update reference map
            for (int m = x-2; m < x+5; m++) {
                for (int n = y-2; n < y+5; n++) {
                    exploreChecker.setExplored(m, n, grid.getIsExplored(m, n));
                    exploreChecker.setIsObstacle(m, n, grid.getIsObstacle(m, n));
                }
            }

            if (found == 0 && robot.isObstacleAhead()){
                break;
            }
            else if (!robot.isObstacleAhead()){
                // MOVE FORWARD
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M");
                robot.move();
                senseAndUpdateAndroid(robot, grid, realRun);
                if (!realRun)
                    stepTaken();
                actionString.add("M");

                calibrationCounter++;
                // CALIBRATION
                if (calibrationCounter >= CALIBRATION_LIMIT) {
                    // OTHERWISE CALIBRATE LEFT
                    if (realRun){
                        // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                        if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "A");
                            calibrationCounter = 0;

                            // SENSE FOR RIGHT SIDE
                            robot.turn(RIGHT);
                            senseAndUpdateAndroid(robot, grid, realRun);
                            robot.turn(LEFT);
                        }
                        else if (robot.canCalibrateFront() && robot.canCalibrateRight()) {
                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "G");
                            calibrationCounter = 0;

                            // SENSE FOR RIGHT SIDE
                            robot.turn(RIGHT);
                            senseAndUpdateAndroid(robot, grid, realRun);
                            robot.turn(LEFT);
                        }
                        else if (robot.canCalibrateLeft()) {
                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "B");
                            calibrationCounter = 0;

                            // SENSE FOR RIGHT SIDE
                            robot.turn(RIGHT);
                            senseAndUpdateAndroid(robot, grid, realRun);
                            robot.turn(LEFT);
                        } else if(robot.canCalibrateFront()) {
                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                            calibrationCounter = 0;

                            // SENSE FOR RIGHT SIDE
                            robot.turn(RIGHT);
                            senseAndUpdateAndroid(robot, grid, realRun);
                            robot.turn(LEFT);
                        } else if(robot.canCalibrateRight()) {
                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "E");
                            calibrationCounter = 0;
                            // SENSE FOR RIGHT SIDE
                            robot.turn(RIGHT);
                            senseAndUpdateAndroid(robot, grid, realRun);
                            robot.turn(LEFT);
                        }
                    }
                    else {
                        //SHOW CALIBRATION PROCESS ON SIMULATOR
                        // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                        if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                            System.out.println("CALIBRATION FRONT LEFT");
                            robot.turn(LEFT);
                            stepTaken();
                            robot.turn(RIGHT);
                            calibrationCounter = 0;
                        }
                        else if (robot.canCalibrateFront() && robot.canCalibrateRight()) {
                            System.out.println("CALIBRATION FRONT Right");
                            robot.turn(RIGHT);
                            stepTaken();
                            robot.turn(LEFT);
                            calibrationCounter = 0;
                        }
                        // OTHERWISE CALIBRATE LEFT
                        else if (robot.canCalibrateLeft()) {
                            System.out.println("CALIBRATION LEFT");
                            robot.turn(LEFT);
                            stepTaken();
                            robot.turn(RIGHT);
                            calibrationCounter = 0;
                        }else if(robot.canCalibrateFront()) {
                            System.out.println("CALIBRATION FRONT");
                            calibrationCounter = 0;

                            // SENSE BEFORE CALIBRATION
                            senseAndUpdateAndroid(robot, grid, realRun);
                        } else if (robot.canCalibrateFront() && robot.canCalibrateRight()) {
                            System.out.println("CALIBRATION Right");
                            robot.turn(RIGHT);
                            stepTaken();
                            robot.turn(LEFT);
                            calibrationCounter = 0;
                        }
                    }
                }
            }
        }

        // Aditional Checks for loops
        breakIslandLoop(robot, realRun, grid);
        if (actionString.size()>8)
            breakLoop(robot, realRun, grid);
    }

    /**
     * Checks if a turn is necessary and which direction to turn
     * @param robot
     * @param grid
     * @param realRun
     * @return whether a turn is performed
     */
    private boolean leftWallFollower(Robot robot, GridMap grid, boolean realRun){

        if (robot.isObstacleAhead()) {
            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                if (realRun) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    senseAndUpdateAndroid(robot, grid,realRun);
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                }
                actionString.add("R");
                actionString.add("R");
                robot.turn(RIGHT);
                robot.turn(RIGHT);
                if (!realRun)
                    stepTaken();
            } else if (robot.isObstacleLeft()) {
                System.out.println("OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");

                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                actionString.add("R");
                if (!realRun)
                stepTaken();
            } else {
                System.out.println("OBSTACLE DETECTED! (FRONT) TURNING LEFT");

                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                actionString.add("L");
                if (!realRun)
                stepTaken();
            }
            System.out.println("-----------------------------------------------");

            return true; // TURNED
        }
        else if (!robot.isObstacleLeft()) {
            System.out.println("NO OBSTACLES ON THE LEFT! TURNING LEFT");

            if (realRun)
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
            robot.turn(LEFT);
            actionString.add("L");
            if (!realRun)
            stepTaken();
            System.out.println("-----------------------------------------------");

            return true; // TURNED
        }
        return false; // DIDN'T TURN
    }

    private void stepTaken(){
        /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private void calibrateAtStart() {
//        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
//        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
//        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
//        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
//    }

    private void senseAndUpdateAndroid(Robot robot, GridMap grid, boolean realRun) {
        robot.sense(realRun);
        if (realRun) {
            SocketMgr.getInstance().sendMessage(TARGET_ANDROID,
                    CommMgr.generateMapDescriptorMsg(grid.generateDescriptorPartOne(),grid.generateDescriptorPartTwo(),
                            robot.getCenterPosX(), robot.getCenterPosY(), robot.getOrientation()));
        }
    }

    private void takePhoto(Robot robot, GridMap grid, boolean realRun) {

        if (robot.canTakePhotoLeft()){
            int x_obs = -1;
            int y_obs = -1;
            System.out.println("Taking photo for left obstacle.");
            if (realRun) {
                if(robot.getOrientation()== NORTH){
                    x_obs =robot.getPosX()-1;
                    y_obs = robot.getPosY()+1;
                }
                else if(robot.getOrientation()==EAST){
                    x_obs =robot.getPosX()+1;
                    y_obs = robot.getPosY()-1;
                }
                else if(robot.getOrientation()==SOUTH){
                    x_obs =robot.getPosX()+3;
                    y_obs = robot.getPosY()+1;
                }
                else if(robot.getOrientation()==WEST){
                    x_obs =robot.getPosX()+1;
                    y_obs = robot.getPosY()+3;
                }
                String obs = x_obs + "," + y_obs;
                SocketMgr.getInstance().sendMessage(TARGET_RPI, obs); //get x and y
            }
            else{
                stepTaken();
            }
        }
    }
}