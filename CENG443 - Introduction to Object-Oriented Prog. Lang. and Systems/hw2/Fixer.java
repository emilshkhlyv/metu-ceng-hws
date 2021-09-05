package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.Robot;
import project.parts.Arm;
import project.parts.payloads.Payload;
import project.utility.Common;

import java.util.ArrayList;
import java.util.List;

public class Fixer extends Logic
{
    @Override public void run ( Robot robot ) {
        // Serial number of robot
        int fixerRobotSerialID = (int) Common.get(robot, "serialNo");

        // fixed parts List
        List<Robot> willBeDeleted = new ArrayList<>();
        synchronized (SimulationRunner.factory.brokenRobots) {
            // wait part
            if (SimulationRunner.factory.brokenRobots.size() == 0) {
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Nothing to fix, waiting!%n", fixerRobotSerialID);
                }
                try {
                    SimulationRunner.factory.brokenRobots.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Fixer woke up, going back to work.%n", fixerRobotSerialID);
                }
                for (Robot mainRobot : SimulationRunner.factory.brokenRobots) {
                    // Parts of robot
                    int     mainRobotSerialID   = (int) Common.get(mainRobot, "serialNo");
                    Arm     mainRobotArm        = (Arm)     Common.get(mainRobot, "arm");
                    Payload mainRobotPayload    = (Payload) Common.get(mainRobot, "payload");
                    Logic   mainRobotLogic      = (Logic)   Common.get(mainRobot, "logic");

                    boolean boolForFix = false;
                    // if Base is armless
                    if (mainRobotArm == null) {
                        Common.set(mainRobot, "arm", Factory.createPart("Arm"));
                        boolForFix = true;
                    }
                    else {
                        // if Base has arm, but not payload
                        if (mainRobotPayload == null) {
                            if (Common.getClassName(mainRobotLogic).equals("project.parts.logics.Supplier")) {
                                Common.set(mainRobot, "payload", Factory.createPart("Gripper"));
                                boolForFix = true;
                            } else if (Common.getClassName(mainRobotLogic).equals("project.parts.logics.Builder")) {
                                Common.set(mainRobot, "payload", Factory.createPart("Welder"));
                                boolForFix = true;
                            } else if (Common.getClassName(mainRobotLogic).equals("project.parts.logics.Inspector")) {
                                Common.set(mainRobot, "payload", Factory.createPart("Camera"));
                                boolForFix = true;
                            } else if (Common.getClassName(mainRobotLogic).equals("project.parts.logics.Fixer")) {
                                Common.set(mainRobot, "payload", Factory.createPart("MaintenanceKit"));
                                boolForFix = true;
                            }
                        }
                        else {
                            // if Base has arm and payload, but not logic
                            if (Common.getClassName(mainRobotPayload).equals("project.parts.payloads.Gripper")) {
                                Common.set(mainRobot, "logic", Factory.createPart("Supplier"));
                                boolForFix = true;
                            }
                            else if (Common.getClassName(mainRobotPayload).equals("project.parts.payloads.Welder")) {
                                Common.set(mainRobot, "logic", Factory.createPart("Builder"));
                                boolForFix = true;
                            }
                            else if (Common.getClassName(mainRobotPayload).equals("project.parts.payloads.Camera")) {
                                Common.set(mainRobot, "logic", Factory.createPart("Inspector"));
                                boolForFix = true;
                            }
                            else if (Common.getClassName(mainRobotPayload).equals("project.parts.payloads.MaintenanceKit")) {
                                Common.set(mainRobot, "logic", Factory.createPart("Fixer"));
                                boolForFix = true;
                            }
                        }
                    }
                    // if Robot is Fixed
                    if (boolForFix) {
                        willBeDeleted.add(mainRobot);
                        synchronized (System.out) {
                            System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n", fixerRobotSerialID, mainRobotSerialID);
                        }
                        synchronized (mainRobot) {
                            mainRobot.notify();
                        }
                    }
                }
                SimulationRunner.factory.brokenRobots.removeAll(willBeDeleted);
            }
        }
    }
}
