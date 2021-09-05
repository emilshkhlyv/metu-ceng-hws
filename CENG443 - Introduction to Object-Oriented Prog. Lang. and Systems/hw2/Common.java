package project.utility;

import project.components.Robot;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.logics.Builder;
import project.parts.logics.Fixer;
import project.parts.logics.Inspector;
import project.parts.logics.Supplier;
import project.parts.payloads.Camera;
import project.parts.payloads.Gripper;
import project.parts.payloads.MaintenanceKit;
import project.parts.payloads.Welder;

import java.util.Random;
import java.lang.Class;
import java.lang.reflect.*;

public class Common
{
    public static Random random = new Random() ;

    public static synchronized Object get (Object object , String fieldName )
    {
        try {
            Class<?> reflectedClass = object.getClass();
            Field field = reflectedClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object returnVal = field.get(object);
            field.setAccessible(false);
            return returnVal;
        } catch (Exception e) {
            throw new SmartFactoryException("Failed: get!");
        }
    }

    public static synchronized void set ( Object object , String fieldName , Object value )
    {
        try {
            Class<?> reflectedClass = object.getClass();
            Field field = reflectedClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new SmartFactoryException("Failed: set!");
        }
    }

    // createPart factory
    public static Part createPart (String name ) {
        try {
            switch(name){
                case "Arm":
                    return new Arm();
                case "Camera":
                    return new Camera();
                case "Gripper":
                    return new Gripper();
                case "MaintenanceKit":
                    return new MaintenanceKit();
                case "Welder":
                    return new Welder();
                case "Builder":
                    return new Builder();
                case "Fixer":
                    return new Fixer();
                case "Inspector":
                    return new Inspector();
                case "Supplier":
                    return new Supplier();
            }
        }
        catch (Exception e) {
            throw new SmartFactoryException("Failed: createPart!");
        }
        return null;
    }

    // create Base factory
    public static Base createBase (int serialNo) {
        return new Base(serialNo);
    }

    // get class name
    public static synchronized String getClassName(Part robot) {
        return robot.getClass().getName();
    }
}