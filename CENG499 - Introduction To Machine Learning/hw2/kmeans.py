import numpy as np


def assign_clusters(data, cluster_centers):
    """
    Assigns every data point to its closest (in terms of euclidean distance) cluster center.
    :param data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param cluster_centers: A (K, D) shaped numpy array where K is the number of clusters
    and D is the dimension of the data
    :return: An (N, ) shaped numpy array. At its index i, the index of the closest center
    resides to the ith data point.
    """
    N = np.shape(data)[0]
    M = np.shape(cluster_centers)[0]
    output = []
    closest_distance = float('inf')
    k = -1
    for i in range(N):
        for j in range(M):
            if np.linalg.norm(data[i] - cluster_centers[j]) < closest_distance:
                closest_distance = np.linalg.norm(data[i] - cluster_centers[j])
                k = j
        output.append(k)
        closest_distance = float('inf')
    return np.array(output)


def calculate_cluster_centers(data, assignments, cluster_centers, k):
    """
    Calculates cluster_centers such that their squared euclidean distance to the data assigned to
    them will be lowest.
    If none of the data points belongs to some cluster center, then assign it to its previous value.
    :param data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param assignments: An (N, ) shaped numpy array with integers inside. They represent the cluster index
    every data assigned to.
    :param cluster_centers: A (K, D) shaped numpy array where K is the number of clusters
    and D is the dimension of the data
    :param k: Number of clusters
    :return: A (K, D) shaped numpy array that contains the newly calculated cluster centers.
    """
    output = []
    datas = []
    N = np.shape(data)[0]
    for i in range(k):
        for j in range(N):
            if assignments[j] == i:
                datas.append(data[j])
        if not datas:
            output.append(cluster_centers[i])
        if datas:
            output.append(np.average(datas, axis=0))
            datas.clear()
    return np.array(output)


def kmeans(data, initial_cluster_centers):
    """
    Applies k-means algorithm.
    :param data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param initial_cluster_centers: A (K, D) shaped numpy array where K is the number of clusters
    and D is the dimension of the data
    :return: cluster_centers, objective_function
    cluster_center.shape is (K, D).
    objective function is a float. It is calculated by summing the squared euclidean distance between
    data points and their cluster centers.
    """
    N = np.shape(data)[0]
    M = np.shape(initial_cluster_centers)[0]
    flag = True
    cluster_centers = initial_cluster_centers
    cluster_centers_temp = initial_cluster_centers
    assignments = []
    while flag:
        assignments = assign_clusters(data, cluster_centers)
        cluster_centers = calculate_cluster_centers(data, assignments, cluster_centers, M)
        if np.all(np.abs(cluster_centers - cluster_centers_temp) < 10 ** -5):
            break
        cluster_centers_temp = cluster_centers
    OF = 0
    for i in range(N):
        OF += (np.linalg.norm(data[i] - cluster_centers[assignments[i]])) ** 2

    return np.array(cluster_centers), OF
