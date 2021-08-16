import math
import numpy as np


def entropy(bucket):
    """
    Calculates the entropy.
    :param bucket: A list of size num_classes. bucket[i] is the number of
    examples that belong to class i.
    :return: A float. Calculated entropy.
    """
    ans = 0
    for i in range(len(bucket)):
        if bucket[i] != 0:
            ans += -(bucket[i] / sum(bucket)) * (math.log2(bucket[i] / sum(bucket)))
    return ans


def info_gain(parent_bucket, left_bucket, right_bucket):
    """
    Calculates the information gain. A bucket is a list of size num_classes.
    bucket[i] is the number of examples that belong to class i.
    :param parent_bucket: Bucket belonging to the parent node. It contains the
    number of examples that belong to each class before the split.
    :param left_bucket: Bucket belonging to the left child after the split.
    :param right_bucket: Bucket belonging to the right child after the split.
    :return: A float. Calculated information gain.
    """
    child_entropies = (sum(left_bucket) / sum(parent_bucket)) * entropy(left_bucket) + \
                      (sum(right_bucket) / sum(parent_bucket)) * entropy(right_bucket)
    return entropy(parent_bucket) - child_entropies


def gini(bucket):
    """
    Calculates the gini index.
    :param bucket: A list of size num_classes. bucket[i] is the number of
    examples that belong to class i.
    :return: A float. Calculated gini index.
    """
    ans = 0
    if sum(bucket) != 0:
        for i in range(len(bucket)):
            ans += (bucket[i] / sum(bucket)) ** 2
    return 1 - ans


def avg_gini_index(left_bucket, right_bucket):
    """
    Calculates the average gini index. A bucket is a list of size num_classes.
    bucket[i] is the number of examples that belong to class i.
    :param left_bucket: Bucket belonging to the left child after the split.
    :param right_bucket: Bucket belonging to the right child after the split.
    :return: A float. Calculated average gini index.
    """
    return ((sum(left_bucket) / (sum(left_bucket) + sum(right_bucket))) * gini(left_bucket)) + \
           (sum(right_bucket) / (sum(left_bucket) + sum(right_bucket))) * gini(right_bucket)


def calculate_split_values(data, labels, num_classes, attr_index, heuristic_name):
    """
    For every possible values to split the data for the attribute indexed by
    attribute_index, it divides the data into buckets and calculates the values
    returned by the heuristic function named heuristic_name. The split values
    should be the average of the closest 2 values. For example, if the data has
    2.1 and 2.2 in it consecutively for the values of attribute index by attr_index,
    then one of the split values should be 2.15.
    :param data: An (N, M) shaped numpy array. N is the number of examples in the
    current node. M is the dimensionality of the data. It contains the values for
    every attribute for every example.
    :param labels: An (N, ) shaped numpy array. It contains the class values in
    it. For every value, 0 <= value < num_classes.
    :param num_classes: An integer. The number of classes in the dataset.
    :param attr_index: An integer. The index of the attribute that is going to
    be used for the splitting operation. This integer indexs the second dimension
    of the data numpy array.
    :param heuristic_name: The name of the heuristic function. It should either be
    'info_gain' of 'avg_gini_index' for this homework.
    :return: An (L, 2) shaped numpy array. L is the number of split values. The
    first column is the split values and the second column contains the calculated
    heuristic values for their splits.
    """
    sorted_data_array = data[np.argsort(data[:, attr_index])]
    sorted_labels = [0] * len(labels)
    for i in range(len(sorted_data_array)):
        for j in range(len(data)):
            if (data[j] == sorted_data_array[i]).all():
                sorted_labels[i] = labels[j]

    attributes = sorted_data_array[:, attr_index]

    result = []
    for i in range(len(attributes) - 1):
        left_bucket = [0] * num_classes
        right_bucket = [0] * num_classes
        parent_bucket = [0] * num_classes
        left = []
        right = []
        value = (attributes[i] + attributes[i + 1]) / 2.0
        for p in range(len(data)):
            if sorted_data_array[p, attr_index] < value:
                left.append(sorted_labels[p])
            else:
                right.append(sorted_labels[p])
        left = np.array(left)
        right = np.array(right)
        parent = np.array(sorted_labels)
        for j in range(num_classes):
            left_bucket[j] = (np.count_nonzero(left == j))
            right_bucket[j] = (np.count_nonzero(right == j))
            parent_bucket[j] = (np.count_nonzero(parent == j))

        if heuristic_name == 'info_gain':
            heuristic_function_result = eval(heuristic_name + "(parent_bucket, left_bucket, right_bucket)")
            result.append([value, heuristic_function_result])

        elif heuristic_name == 'avg_gini_index':
            heuristic_function_result = eval(heuristic_name + "(left_bucket, right_bucket)")
            result.append([value, heuristic_function_result])
    return np.array(result)


def chi_squared_test(left_bucket, right_bucket):
    """
    Calculates chi squared value and degree of freedom between the selected attribute
    and the class attribute. A bucket is a list of size num_classes. bucket[i] is the
    number of examples that belong to class i.
    :param left_bucket: Bucket belonging to the left child after the split.
    :param right_bucket: Bucket belonging to the right child after the split.
    :return: A float and and integer. Chi squared value and degree of freedom.
    """
    left_sum = np.sum(left_bucket)
    right_sum = np.sum(right_bucket)
    parent_sum = left_sum + right_sum

    class_count = len(right_bucket)

    degree_of_freedom = class_count - 1
    for i in range(len(right_bucket)):
        if left_bucket[i] == 0 and right_bucket[i] == 0:
            degree_of_freedom -= 1

    chi = 0
    for i in range(class_count):
        if left_bucket[i] != 0 or right_bucket[i] != 0:
            chi += ((left_bucket[i] - ((left_bucket[i] + right_bucket[i]) * left_sum / parent_sum)) ** 2) / (
                    (left_bucket[i] + right_bucket[i]) * left_sum / parent_sum) + (
                           (right_bucket[i] - ((left_bucket[i] + right_bucket[i]) * right_sum / parent_sum)) ** 2) / (
                           (left_bucket[i] + right_bucket[i]) * right_sum / parent_sum)

    return chi, degree_of_freedom
