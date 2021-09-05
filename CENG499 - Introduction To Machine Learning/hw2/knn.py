import numpy as np


def calculate_distances(train_data, test_datum):
    """
    Calculates euclidean distances between test_datum and every train_data
    :param train_data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param test_datum: A (D, ) shaped numpy array
    :return: An (N, ) shaped numpy array that contains distances
    """
    N = np.shape(train_data)[0]
    distances = []
    for i in range(N):
        distances.append(np.linalg.norm(train_data[i] - test_datum))
    return np.asarray(distances)


def majority_voting(distances, labels, k):
    """
    Applies majority voting. If there are more then one major class, returns the smallest label.
    :param distances: An (N, ) shaped numpy array that contains distances
    :param labels: An (N, ) shaped numpy array that contains labels
    :param k: An integer. The number of nearest neighbor to be selected.
    :return: An integer. The label of the majority class.
    """
    label = []
    idx_minimums = sorted(range(len(distances)), key=lambda l: distances[l])[:k]
    for i in idx_minimums:
        label.append(labels[i])
    label.sort()
    return max(label, key=label.count)


def knn(train_data, train_labels, test_data, test_labels, k):
    """
    Calculates accuracy of knn on test data using train_data.
    :param train_data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param train_labels: An (N, ) shaped numpy array that contains labels
    :param test_data: An (M, D) shaped numpy array where M is the number of examples
    and D is the dimension of the data
    :param test_labels: An (M, ) shaped numpy array that contains labels
    :param k: An integer. The number of nearest neighbor to be selected.
    :return: A float. The calculated accuracy.
    """
    m = len(test_data)
    answer = 0
    for i in range(m):
        distances = calculate_distances(train_data, test_data[i])
        label = majority_voting(distances, train_labels, k)
        if label == test_labels[i]:
            answer += 1
    return answer/m


def split_train_and_validation(whole_train_data, whole_train_labels, validation_index, k_fold):
    """
    Splits training dataset into k and returns the validation_indexth one as the
    validation set and others as the training set. You can assume k_fold divides N.
    :param whole_train_data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param whole_train_labels: An (N, ) shaped numpy array that contains labels
    :param validation_index: An integer. 0 <= validation_index < k_fold. Specifies which fold
    will be assigned as validation set.
    :param k_fold: The number of groups that the whole_train_data will be divided into.
    :return: train_data, train_labels, validation_data, validation_labels
    train_data.shape is (N-N/k_fold, D).
    train_labels.shape is (N-N/k_fold, ).
    validation_data.shape is (N/k_fold, D).
    validation_labels.shape is (N/k_fold, ).
    """
    D = np.shape(whole_train_data)[1]
    N = np.shape(whole_train_data)[0]
    N_N_k_fold = N - int(N / k_fold)
    N_k_fold = int(N/k_fold)
    split_train_data = np.array_split(whole_train_data, k_fold)
    split_train_labels = np.array_split(whole_train_labels, k_fold)

    validation_data = split_train_data[validation_index]
    validation_labels = split_train_labels[validation_index]
    validation_data = validation_data.reshape(N_k_fold, D)

    new_train_data = np.delete(split_train_data, validation_index, axis=0)
    new_train_labels = np.delete(split_train_labels, validation_index, axis=0)
    new_train_data = new_train_data.reshape(N_N_k_fold, D)
    new_train_labels = new_train_labels.reshape(-1)

    return new_train_data, new_train_labels, validation_data, validation_labels


def cross_validation(whole_train_data, whole_train_labels, k, k_fold):
    """
    Applies k_fold cross-validation and averages the calculated accuracies.
    :param whole_train_data: An (N, D) shaped numpy array where N is the number of examples
    and D is the dimension of the data
    :param whole_train_labels: An (N, ) shaped numpy array that contains labels
    :param k: An integer. The number of nearest neighbor to be selected.
    :param k_fold: An integer.
    :return: A float. Average accuracy calculated.
    """
    ans = 0
    for i in range(k_fold):
        train_data, train_labels, validation_data, validation_labels = split_train_and_validation(whole_train_data, whole_train_labels, i, k_fold)
        ans += knn(train_data, train_labels, validation_data, validation_labels, k)
    return ans/k_fold


