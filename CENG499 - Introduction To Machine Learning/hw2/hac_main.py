import matplotlib.pyplot as plt
import numpy as np
from hac import single_linkage, complete_linkage, average_linkage, centroid_linkage, hac

if __name__ == "__main__":
    data1 = np.load('hw2_data/hac/data1.npy')
    data2 = np.load('hw2_data/hac/data2.npy')
    data3 = np.load('hw2_data/hac/data3.npy')
    data4 = np.load('hw2_data/hac/data4.npy')

    k = 4
    clusters_out = hac(data4, single_linkage, k)

    black = clusters_out[0]
    red = clusters_out[1]
    if k > 2:
        green = clusters_out[2]
    if k > 3:
        blue = clusters_out[3]

    plt.scatter(black[:, 0], black[:, 1], color='black')
    plt.scatter(red[:, 0], red[:, 1], color='red')
    if k > 2:
        plt.scatter(green[:, 0], green[:, 1], color='green')
    if k > 3:
        plt.scatter(blue[:, 0], blue[:, 1], color='blue')

    plt.xlabel('x-axis')
    plt.ylabel('y-axis')
    plt.show()
