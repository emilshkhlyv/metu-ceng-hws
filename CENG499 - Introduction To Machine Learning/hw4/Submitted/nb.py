import numpy as np


def vocabulary(data):
    """
    Creates the vocabulary from the data.
    :param data: List of lists, every list inside it contains words in that sentence.
                 len(data) is the number of examples in the data.
    :return: Set of words in the data
    """
    return_list = set()
    for i in range(len(data)):
        for j in range(len(data[i])):
            return_list.add(data[i][j])
    return return_list


def estimate_pi(train_labels):
    """
    Estimates the probability of every class label that occurs in train_labels.
    :param train_labels: List of class names. len(train_labels) is the number of examples in the training data.
    :return: pi. pi is a dictionary. Its keys are class names and values are their probabilities.
    """
    pi = {}
    for label in train_labels:
        if label in pi:
            pi[label] += 1
        else:
            pi[label] = 1
    pi = {x: pi[x] / len(train_labels) for x in pi}

    return pi


def estimate_theta(train_data, train_labels, vocab):
    """
    Estimates the probability of a specific word given class label using additive smoothing with smoothing constant 1.
    :param train_data: List of lists, every list inside it contains words in that sentence.
                       len(train_data) is the number of examples in the training data.
    :param train_labels: List of class names. len(train_labels) is the number of examples in the training data.
    :param vocab: Set of words in the training set.
    :return: theta. theta is a dictionary of dictionaries. At the first level, the keys are the class names. At the
             second level, the keys are all the words in vocab and the values are their estimated probabilities given
             the first level class name.
    """
    label_dict = {}
    for i in range(len(train_labels)):
        if train_labels[i] not in label_dict:
            label_dict[train_labels[i]] = []
        label_dict[train_labels[i]].extend(train_data[i])
    label_list = list(label_dict)
    theta = {}
    for v in vocab:
        for i in range(len(label_dict)):
            if label_list[i] not in theta:
                theta[label_list[i]] = {}
            numerator = label_dict[label_list[i]].count(v) + 1
            denominator = len(label_dict[label_list[i]]) + len(vocab)
            theta[label_list[i]][v] = numerator / denominator
    return theta


def test(theta, pi, vocab, test_data):
    """
    Calculates the scores of a test data given a class for each class. Skips the words that are not occurring in the
    vocabulary.
    :param theta: A dictionary of dictionaries. At the first level, the keys are the class names. At the second level,
                  the keys are all of the words in vocab and the values are their estimated probabilities.
    :param pi: A dictionary. Its keys are class names and values are their probabilities.
    :param vocab: Set of words in the training set.
    :param test_data: List of lists, every list inside it contains words in that sentence.
                      len(test_data) is the number of examples in the test data.
    :return: scores, list of lists. len(scores) is the number of examples in the test set. Every inner list contains
             tuples where the first element is the score and the second element is the class name.
    """
    length = len(test_data)
    scores = []
    for data in range(length):
        scores.append([])
        for label in pi:
            val = 0
            for vocab_word in vocab:
                val += np.log(theta[label][vocab_word]) * test_data[data].count(vocab_word)
            scores[data].append((np.log(pi[label]) + val, label))
    return scores
