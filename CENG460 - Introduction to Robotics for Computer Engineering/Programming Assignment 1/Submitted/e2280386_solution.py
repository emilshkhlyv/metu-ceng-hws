#!/usr/bin/env python
import rospy
from geometry_msgs.msg import Twist
import tf2_ros
from ceng460hw1.srv import *
from ceng460hw1_utils import *
import numpy as np
from tf2_msgs.msg import TFMessage
from tf.transformations import euler_from_quaternion


class Robot:

    def __init__(self):
        rospy.init_node('robot')
        self.tfBuffer = tf2_ros.Buffer()
        self.listener = tf2_ros.TransformListener(self.tfBuffer)
        self.pub = rospy.Publisher('cmd_vel', Twist,queue_size=10)
        self.zeroTime = rospy.Time()
    
    def send_diff_drive_vel_msg(self, linear, angular):
        msg = Twist()
        msg.linear.x = linear
        msg.angular.z = angular
        self.pub.publish(msg)
    
    def get_global_transformStamped(self):
        try:
            trans = self.tfBuffer.lookup_transform("odom", "base_footprint", self.zeroTime)
            return trans
        except (tf2_ros.LookupException, tf2_ros.ConnectivityException, tf2_ros.ExtrapolationException):
            return None

    # P_vel is 0.15 and P_angular is 1.5
    def move_to(self, x_goal, y_goal, P_vel, P_angular, rate=20, tolerance=0.02):
        # get pose of robot
        trans = self.get_global_transformStamped()
        # distance between robot and goal point
        distance = np.sqrt(((x_goal - trans.transform.translation.x)**2)+((y_goal - trans.transform.translation.y)**2))
        # Rate
        rater = rospy.Rate(rate)
        while distance > tolerance:
            # Radial Velocity calculation from book
            RobotRadialVelocity = np.sqrt(((x_goal - trans.transform.translation.x)**2)+((y_goal - trans.transform.translation.y)**2))
            
            # Angle calculation from book
            RobotAngle = np.arctan2((y_goal-trans.transform.translation.y), (x_goal-trans.transform.translation.x))
            
            # Yaw angle from robot's pose and calculation of angle between robot and goal point
            z = euler_from_quaternion((trans.transform.rotation.x, trans.transform.rotation.y, trans.transform.rotation.z, trans.transform.rotation.w))[2]
            angle = RobotAngle-z
            angle = np.mod(angle+np.pi, np.pi*2)-np.pi 

            # Send message
            self.send_diff_drive_vel_msg(P_vel*RobotRadialVelocity, P_angular*angle)

            rater.sleep()

            # Get new pose of robot and calculate distance between robot and goal point
            trans = self.get_global_transformStamped()
            distance = np.sqrt(((x_goal - trans.transform.translation.x)**2)+((y_goal - trans.transform.translation.y)**2))
        return
    
    # P_angular value is 1.5
    def rotate_to(self, x_goal, y_goal, P_angular, rate=20, tolerance=0.02):
        # get Pose of Robot
        trans = self.get_global_transformStamped()

        # Angle calculation from Book
        RobotAngle = np.arctan2((y_goal-trans.transform.translation.y), (x_goal-trans.transform.translation.x))
        
        # Yaw angle from robot's pose and calculation of angle between robot and goal point
        z = euler_from_quaternion((trans.transform.rotation.x, trans.transform.rotation.y, trans.transform.rotation.z, trans.transform.rotation.w))[2]
        angle = RobotAngle-z
        angle = np.mod(angle+np.pi, np.pi*2)-np.pi 
        
        rater = rospy.Rate(rate)
        while np.abs(angle) > tolerance:
            # Send message
            self.send_diff_drive_vel_msg(0, P_angular*angle)

            rater.sleep()

            # Calculate angle between robot and goal point again 
            trans = self.get_global_transformStamped()
            RobotAngle = np.arctan2((y_goal-trans.transform.translation.y), (x_goal-trans.transform.translation.x))
            z = euler_from_quaternion((trans.transform.rotation.x, trans.transform.rotation.y, trans.transform.rotation.z, trans.transform.rotation.w))[2]
            angle = RobotAngle-z
            angle = np.mod(angle+np.pi, np.pi*2)-np.pi 
        return

    def main(self):
        rospy.wait_for_message('tf', TFMessage)
        rospy.wait_for_service('spot_announcement')
        announce_spot = rospy.ServiceProxy('spot_announcement', SpotAnnouncement)
        first_announcement = SpotAnnouncementRequest()
        
        # created TransformStamped object for array initialization
        trans = TransformStamped()
        trans.header.frame_id = -1
        array = np.full((14, 14), trans)

        # Boolean array for keeping went treasures
        went = np.full(14, False)
        went[0] = True

        # First announcement from origin
        first_announcement.spot.child_frame_id = "0"
        first_resp = announce_spot(first_announcement)
        if not first_resp.success:
            rospy.logerr('something is wrong!')
            exit()

        # keep clues in 2D array in row which is frame_id and column which is child_frame_id
        for i in range(len(first_resp.clues)):
            array[int(first_resp.clues[i].header.frame_id)][int(first_resp.clues[i].child_frame_id)] = first_resp.clues[i]
        
        # Boolean for Array manipulation finish
        change = True
        while change == True:
            change = False
            for i in range(len(array[0])):
                if int(array[0][i].header.frame_id) != -1:
                    for j in range(len(array[i])):
                        # if there's transformStamped for a coordinate system from 0th coordinate system do necessary calculations
                        if int(array[i][j].header.frame_id) != -1 and int(array[0][j].header.frame_id) == -1:
                            transforMatbase = TransformStamped_to_transform_matrix(array[0][i])
                            transformMat    = TransformStamped_to_transform_matrix(array[i][j])
                            transformated   = np.matmul(transforMatbase, transformMat)
                            array[0][j]     = transform_matrix_to_TransformStamped(transformated, 0, j)
                            change = True

        finish = False
        while finish == False:
            nearest = 0
            distance = 10000000
            trans = self.get_global_transformStamped()
            # Find the nearest coordinate frame
            for i in range(len(array[0])):
                if array[0][i].header.frame_id != -1 and went[i] == False:
                    mesafe = np.sqrt(((array[0][i].transform.translation.x - trans.transform.translation.x)**2)+((array[0][i].transform.translation.y - trans.transform.translation.y)**2))
                    if mesafe < distance:
                        nearest = i
            
            # rotate around a point
            self.rotate_to(array[0][nearest].transform.translation.x, array[0][nearest].transform.translation.y, 1.5)

            # move to nearest coordinate frame point
            self.move_to(array[0][nearest].transform.translation.x, array[0][nearest].transform.translation.y, 0.15, 1.5)

            # mark this coordinate frame as went
            went[nearest] = True
            
            # send message to referee
            first_announcement.spot.child_frame_id = str(nearest)
            first_announcement.spot.transform = array[0][nearest].transform
            first_resp = announce_spot(first_announcement)
            if not first_resp.success:
                rospy.logerr('something is wrong!')
                exit()

            # put new clues to array
            for k in range(len(first_resp.clues)):
                array[int(first_resp.clues[k].header.frame_id)][int(first_resp.clues[k].child_frame_id)] = first_resp.clues[k]
            
            # make transformation calculations in array
            change = True
            while change == True:
                change = False
                for i in range(len(array[0])):
                    if int(array[0][i].header.frame_id) != -1:
                        for j in range(len(array[i])):
                            if int(array[i][j].header.frame_id) != -1 and int(array[0][j].header.frame_id) == -1:
                                transforMatbase = TransformStamped_to_transform_matrix(array[0][i])
                                transformMat    = TransformStamped_to_transform_matrix(array[i][j])
                                transformated   = np.matmul(transforMatbase, transformMat)
                                array[0][j]     = transform_matrix_to_TransformStamped(transformated, 0, j)
                                change = True

            # If there's no coordinate frame left finish while loop
            finish = True
            for i in range(len(array[0])):
                if went[i] == False and array[0][i].header.frame_id != -1:
                    finish = False

if __name__ == "__main__":
    try:
        robot = Robot()
        robot.main()
    except rospy.ROSInterruptException:
        pass
