import matplotlib.pyplot as plt
import numpy as np

from kmeans import assign_clusters, kmeans


def initialize_centers(cluster, k):
    low_y = np.min(cluster[:, 1])
    high_y = np.max(cluster[:, 1])
    low_x = np.min(cluster[:, 0])
    high_x = np.max(cluster[:, 0])

    center = np.random.uniform(low=(low_x, low_y), high=(high_x, high_y), size=(k, 2))

    return center


def plotting_clusters(cluster, assignments):
    blue = []
    red = []
    green = []
    gray = []
    yellow = []

    for i in range(np.shape(cluster)[0]):
        if assignments[i] == 0:
            blue.append(cluster[i])
        elif assignments[i] == 1:
            red.append(cluster[i])
        elif assignments[i] == 2:
            green.append(cluster[i])
        elif assignments[i] == 3:
            gray.append(cluster[i])
        elif assignments[i] == 4:
            yellow.append(cluster[i])

    blue = np.array(blue)
    red = np.array(red)
    green = np.array(green)
    gray = np.array(gray)
    yellow = np.array(yellow)

    return blue, red, green, gray, yellow


def main_for_clustering(cluster, k):
    final_y = []
    y = float('inf')
    assignments = []
    cluster_centers_list = []
    for j in range(20):
        cluster_centers = initialize_centers(cluster, k)
        cl_c, obj_func = kmeans(cluster, cluster_centers)
        temp_assignments = assign_clusters(cluster, cl_c)
        if obj_func < y:
            y = obj_func
            cluster_centers_list = cl_c
            assignments = temp_assignments
    final_y.append(y)

    blue, red, green, gray, yellow = plotting_clusters(cluster, assignments)
    # blue cluster
    blue_class_center = cluster_centers_list[0]
    plt.scatter(blue[:, 0], blue[:, 1], color='cyan', marker='.')
    plt.plot(blue_class_center[0], blue_class_center[1], color='cyan', marker='s', markeredgecolor='black',
             markersize=15)

    # red cluster
    red_class_center = cluster_centers_list[1]
    plt.scatter(red[:, 0], red[:, 1], color='red', marker='.')
    plt.plot(red_class_center[0], red_class_center[1], color='red', marker='s', markeredgecolor='black', markersize=15)
    if k > 2:
        # green cluster
        green_class_center = cluster_centers_list[2]
        plt.scatter(green[:, 0], green[:, 1], color='green', marker='.')
        plt.plot(green_class_center[0], green_class_center[1], color='green', marker='s', markeredgecolor='black',
                 markersize=15)
    if k > 3:
        # gray cluster
        gray_class_center = cluster_centers_list[3]
        plt.scatter(gray[:, 0], gray[:, 1], color='gray', marker='.')
        plt.plot(gray_class_center[0], gray_class_center[1], color='gray', marker='s', markeredgecolor='black',
                 markersize=15)
    if k > 4:
        yellow_class_center = cluster_centers_list[4]
        plt.scatter(yellow[:, 0], yellow[:, 1], color='yellow', marker='.')
        plt.plot(yellow_class_center[0], yellow_class_center[1], color='yellow', marker='s', markeredgecolor='black',
                 markersize=15)

    plt.xlabel('x-axis')
    plt.ylabel('y-axis')
    plt.show()


def main_for_elbow(cluster):
    final_y = []
    y = float('inf')
    for i in range(1, 11):
        for j in range(10):
            cluster_centers = initialize_centers(cluster, i)
            cl_c, obj_func = kmeans(cluster, cluster_centers)
            if obj_func < y:
                y = obj_func
        final_y.append(y)

    x = [*range(1, 11, 1)]
    plt.plot(x, final_y)
    ax = plt.subplot(111)
    ax.set_xlim(1, 10)
    dim = np.arange(1, 11, 1)
    plt.xticks(dim)
    plt.grid()
    plt.xlabel('k')
    plt.ylabel('objective function')
    plt.show()


if __name__ == "__main__":
    clustering1 = np.load('hw2_data/kmeans/clustering1.npy')
    clustering2 = np.load('hw2_data/kmeans/clustering2.npy')
    clustering3 = np.load('hw2_data/kmeans/clustering3.npy')
    clustering4 = np.load('hw2_data/kmeans/clustering4.npy')

    main_for_clustering(clustering4, 5)
    # main_for_elbow(clustering1)