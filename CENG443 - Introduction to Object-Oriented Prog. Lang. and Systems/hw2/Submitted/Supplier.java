package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.Robot;
import project.parts.Part;
import project.utility.Common;

import java.util.List;

public class Supplier extends Logic {
    private int emptySpace = 0;

    private void findEmptySpace() {
        List<Part> productionLine = SimulationRunner.factory.productionLine.parts;
        if (productionLine.size() == SimulationRunner.factory.maxRobots) {
            emptySpace = -1;
        } else {
            emptySpace = 0;
            for (int i = 0; i < productionLine.size(); ++i) {
                if (productionLine.get(i) == null) {
                    emptySpace = i;
                    break;
                }
            }
        }
    }

    @Override
    public void run(Robot robot) {
        synchronized (SimulationRunner.factory.productionLine.parts) {
            int serialNoRobot = (int) Common.get(robot, "serialNo");
            findEmptySpace();
            // if there's no empty place in ProductionLine
            if (emptySpace == -1) {
                // Random delete part
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Production line is full, removing a random part from production line.%n", serialNoRobot);
                }
                int randomDelete = Common.random.nextInt(SimulationRunner.factory.productionLine.parts.size());
                SimulationRunner.factory.productionLine.parts.remove(randomDelete);
            }
            else {
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Supplying a random part on production line.%n", serialNoRobot);
                }
                int randomAdd = Common.random.nextInt(10);
                if (emptySpace != 0) {
                    SimulationRunner.factory.productionLine.parts.remove(emptySpace);
                }
                // create random parts
                switch (randomAdd) {
                    case 0 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createBase());
                    case 1 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Arm"));
                    case 2 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Camera"));
                    case 3 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Gripper"));
                    case 4 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("MaintenanceKit"));
                    case 5 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Welder"));
                    case 6 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Builder"));
                    case 7 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Fixer"));
                    case 8 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Inspector"));
                    case 9 -> SimulationRunner.factory.productionLine.parts.add(emptySpace, Factory.createPart("Supplier"));
                }
            }
            synchronized (SimulationRunner.productionLineDisplay) {
                SimulationRunner.productionLineDisplay.repaint();
            }
            synchronized (System.out) {
                System.out.printf("Robot %02d : Waking up waiting builders.%n", serialNoRobot);
            }
            SimulationRunner.factory.productionLine.parts.notifyAll();
        }
    }
}