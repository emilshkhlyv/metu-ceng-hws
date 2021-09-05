package project.parts.logics;

import project.SimulationRunner;
import project.components.Robot;
import project.utility.Common;

import java.util.ArrayList;
import java.util.List;

public class Inspector extends Logic
{
    @Override public void run ( Robot robot )
    {
        try {
            boolean broke = false;
            int robotSerialNo = (int) Common.get(robot, "serialNo");
            synchronized (SimulationRunner.factory.robots){
                for(Robot inspectedRobot: SimulationRunner.factory.robots){
                    // if robot lose some part
                    if( Common.get(inspectedRobot, "arm")       == null
                    ||  Common.get(inspectedRobot, "payload")   == null
                    ||  Common.get(inspectedRobot, "logic")     == null){
                        if(!SimulationRunner.factory.brokenRobots.contains(inspectedRobot)) {
                            synchronized (SimulationRunner.factory.brokenRobots){
                                SimulationRunner.factory.brokenRobots.add(inspectedRobot);
                            }
                            synchronized (System.out) {
                                System.out.printf("Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n", robotSerialNo, (int) Common.get(inspectedRobot, "serialNo"));
                            }
                            broke = true;
                        }
                    }
                }
            }
            if(broke){
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Notifying waiting fixers.%n", robotSerialNo);
                }
                synchronized (SimulationRunner.factory.brokenRobots) {
                    SimulationRunner.factory.brokenRobots.notifyAll();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
