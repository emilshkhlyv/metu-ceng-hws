package project.parts.logics;

import project.SimulationRunner;
import project.components.Robot;
import project.parts.Arm;
import project.parts.Part;
import project.parts.payloads.Payload;
import project.utility.Common;

import java.util.*;

public class Builder extends Logic
{
    @Override public synchronized void run ( Robot robot )
    {
        // boolean value for control if builder can or cannot build anything
        boolean build = false;

        // serial number of robot
        int serialNo = (int) Common.get(robot, "serialNo");

        // List for building parts
        List<Part> willBeDeleted = new ArrayList<>();

        synchronized (SimulationRunner.factory.productionLine.parts) {
            synchronized (System.out) {
                System.out.printf("Robot %02d : Builder woke up, going back to work.%n", serialNo);
            }
            for (Part part : SimulationRunner.factory.productionLine.parts) {
                if (Common.getClassName(part).equals("project.parts.Base")) {
                    // Parts of Base which we inspect for building
                    Arm     armOfPart       = (Arm)     Common.get(part, "arm");
                    Payload payloadOfPart   = (Payload) Common.get(part, "payload");
                    Logic   logicOfPart     = (Logic)   Common.get(part, "logic");

                    // if base is armless
                    if (armOfPart == null) {
                        for (Part armSearch : SimulationRunner.factory.productionLine.parts) {
                            if (Common.getClassName(armSearch).equals("project.parts.Arm")) {
                                Common.set(part, "arm", armSearch);
                                build = true;
                                willBeDeleted.add(armSearch);
                                synchronized (System.out) {
                                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", serialNo);
                                }
                                break;
                            }
                        }
                    } else {
                        // if base has arm but not payload
                        if (payloadOfPart == null) {
                            for (Part payloadSearch : SimulationRunner.factory.productionLine.parts) {
                                if (Common.getClassName(payloadSearch).equals("project.parts.payloads.Camera") ||
                                        Common.getClassName(payloadSearch).equals("project.parts.payloads.Welder") ||
                                        Common.getClassName(payloadSearch).equals("project.parts.payloads.Gripper") ||
                                        Common.getClassName(payloadSearch).equals("project.parts.payloads.MaintenanceKit")) {

                                    build = true;
                                    willBeDeleted.add(payloadSearch);
                                    Common.set(part, "payload", payloadSearch);
                                    synchronized (System.out) {
                                        System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", serialNo);
                                    }
                                    break;
                                }
                            }
                        } else {
                            // if base has arm and payload but not logic
                            if (logicOfPart == null) {
                                for (Part logicSearch : SimulationRunner.factory.productionLine.parts) {
                                    if ((Common.getClassName(logicSearch).equals("project.parts.logics.Supplier") && Common.getClassName(payloadOfPart).equals("project.parts.payloads.Gripper")) ||
                                            (Common.getClassName(logicSearch).equals("project.parts.logics.Fixer") && Common.getClassName(payloadOfPart).equals("project.parts.payloads.MaintenanceKit")) ||
                                            (Common.getClassName(logicSearch).equals("project.parts.logics.Inspector") && Common.getClassName(payloadOfPart).equals("project.parts.payloads.Camera")) ||
                                            (Common.getClassName(logicSearch).equals("project.parts.logics.Builder") && Common.getClassName(payloadOfPart).equals("project.parts.payloads.Welder"))) {

                                        Common.set(part, "logic", logicSearch);
                                        willBeDeleted.add(logicSearch);
                                        build = true;
                                        synchronized (System.out) {
                                            System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", serialNo);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                // if is there any empty in robots Line
                                if (SimulationRunner.factory.robots.size() < SimulationRunner.factory.maxRobots) {
                                    synchronized (SimulationRunner.factory.robots) {
                                        Robot newRobot = (Robot) part;
                                        SimulationRunner.factory.robots.add((Robot) part);
                                        new Thread(newRobot).start();
                                    }
                                    build = true;
                                    willBeDeleted.add(part);
                                } else {
                                    // if there's empty place in storage line
                                    if (SimulationRunner.factory.storage.maxCapacity > SimulationRunner.factory.storage.robots.size()) {
                                        synchronized (SimulationRunner.factory.storage) {
                                            SimulationRunner.factory.storage.robots.add((Robot) part);
                                        }
                                        willBeDeleted.add(part);
                                        build = true;
                                    }
                                    // if storage is full, stop program
                                    if (SimulationRunner.factory.storage.maxCapacity == SimulationRunner.factory.storage.robots.size()) {
                                        SimulationRunner.factory.initiateStop();
                                    }
                                }
                            }
                        }
                    }
                }
                // if it build happened, break the loop
                if(willBeDeleted.size() > 0) break;
            }
            // Delete builded parts
            if(willBeDeleted.size() > 0) {
                for (Part deleted : willBeDeleted) {
                    synchronized (SimulationRunner.factory.productionLine.parts) {
                        SimulationRunner.factory.productionLine.parts.remove(deleted);
                    }
                }
            }
            // Wait part
            if(!build) {
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n", serialNo);
                }
                try {
                    SimulationRunner.factory.productionLine.parts.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized (SimulationRunner.robotsDisplay) {
            SimulationRunner.robotsDisplay.repaint();
        }
        synchronized (SimulationRunner.storageDisplay) {
            SimulationRunner.storageDisplay.repaint();
        }
    }
}