import numpy as np


def single_linkage(c1, c2):
    """
    Given clusters c1 and c2, calculates the single linkage criterion.
    :param c1: An (N, D) shaped numpy array containing the data points in cluster c1.
    :param c2: An (M, D) shaped numpy array containing the data points in cluster c2.
    :return: A float. The result of the calculation.
    """
    min_val = float('inf')
    for i in c1:
        for j in c2:
            if np.linalg.norm(i - j) < min_val:
                min_val = np.linalg.norm(i - j)
    return min_val


def complete_linkage(c1, c2):
    """
    Given clusters c1 and c2, calculates the complete linkage criterion.
    :param c1: An (N, D) shaped numpy array containing the data points in cluster c1.
    :param c2: An (M, D) shaped numpy array containing the data points in cluster c2.
    :return: A float. The result of the calculation.
    """
    max_val = -1
    for i in c1:
        for j in c2:
            if np.linalg.norm(i - j) > max_val:
                max_val = np.linalg.norm(i - j)
    return max_val


def average_linkage(c1, c2):
    """
    Given clusters c1 and c2, calculates the average linkage criterion.
    :param c1: An (N, D) shaped numpy array containing the data points in cluster c1.
    :param c2: An (M, D) shaped numpy array containing the data points in cluster c2.
    :return: A float. The result of the calculation.
    """
    N = np.shape(c1)[0]
    M = np.shape(c2)[0]
    NM = N*M
    max_val = 0
    for i in c1:
        for j in c2:
            max_val += np.linalg.norm(i - j)
    val = (max_val/NM)
    return val


def centroid_linkage(c1, c2):
    """
    Given clusters c1 and c2, calculates the centroid linkage criterion.
    :param c1: An (N, D) shaped numpy array containing the data points in cluster c1.
    :param c2: An (M, D) shaped numpy array containing the data points in cluster c2.
    :return: A float. The result of the calculation.
    """
    c1_all = np.true_divide(np.sum(c1, axis=0), np.shape(c1)[0])
    c2_all = np.true_divide(np.sum(c2, axis=0), np.shape(c2)[0])
    return np.linalg.norm(c1_all - c2_all)


def hac(data, criterion, stop_length):
    """
    Applies hierarchical agglomerative clustering algorithm with the given criterion on the data
    until the number of clusters reaches the stop_length.
    :param data: An (N, D) shaped numpy array containing all of the data points.
    :param criterion: A function. It can be single_linkage, complete_linkage, average_linkage, or
    centroid_linkage
    :param stop_length: An integer. The length at which the algorithm stops.
    :return: A list of numpy arrays with length stop_length. Each item in the list is a cluster
    and a (Ni, D) sized numpy array.
    """
    output = []
    for i in range(len(data)):
        output.append([])
        output[i].append(data[i])
        output[i] = np.array(output[i])

    i_index = -1
    j_index = -1
    dist = float('inf')
    pena = float('inf')
    while len(output) != stop_length:
        for i in range(len(output)):
            for j in range(len(output)):
                if   criterion == single_linkage:
                    pena = single_linkage  (output[i], output[j])
                elif criterion == complete_linkage:
                    pena = complete_linkage(output[i], output[j])
                elif criterion == average_linkage:
                    pena = average_linkage (output[i], output[j])
                elif criterion == centroid_linkage:
                    pena = centroid_linkage(output[i], output[j])
                if i != j and dist > pena:
                    dist = pena
                    i_index = i
                    j_index = j
        index = np.append(output[i_index], output[j_index], axis=0)
        output.append(index)
        output.pop(i_index)
        if i_index > j_index:
            output.pop(j_index)
        elif i_index < j_index:
            output.pop(j_index-1)
        dist = float('inf')
        i_index = float('inf')
        j_index = float('inf')
    return output
