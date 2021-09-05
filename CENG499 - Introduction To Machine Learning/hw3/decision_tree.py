import numpy as np

from dt import calculate_split_values, chi_squared_test
from graphviz import Digraph

train_data = np.load('hw3_data/iris/train_data.npy')
train_labels = np.load('hw3_data/iris/train_labels.npy')
test_data = np.load('hw3_data/iris/test_data.npy')
test_labels = np.load('hw3_data/iris/test_labels.npy')

g = Digraph('G', filename='decision tree.gv')

df1 = 2.71
df2 = 4.61
v = 0


def printNode(treeNode):
    print(len(treeNode.data),
          len(treeNode.label),
          treeNode.bucket,
          treeNode.dec,
          treeNode.column,
          treeNode.leaf,
          treeNode.split_value)


class Node:
    def __init__(self, data, label):
        self.data = data
        self.label = label
        self.left = None
        self.right = None
        self.column = None
        self.leaf = 0
        self.dec = None
        self.split_value = None


def bucketToString(string, mode, preprune):
    global v
    if (string == [0, 1, 0] and mode == "avg_gini_index" and preprune == 0) or (
            mode == "avg_gini_index" and string == [0, 1, 1] and preprune == 1) or (
            string == [0, 0, 1] and (mode == "info_gain" or mode == "avg_gini_index") and preprune == 0):
        v += 1
        ret = ""
        for char in range(len(string) - 1):
            ret += f'{string[char]} '
        ret += f'{string[len(string) - 1]}'
        return "[" + ret + "]" + (v * " ")
    else:
        ret = ""
        for char in range(len(string) - 1):
            ret += f'{string[char]} '
        ret += f'{string[len(string) - 1]}'
        return "[" + ret + "]"


def decision_tree(data, labels, node, mode, preprune):
    unique_labels = []
    for split_candidate in labels:
        if split_candidate not in unique_labels:
            unique_labels.append(split_candidate)
    bucket = [0] * 3

    tree_node = Node(data, labels)

    for unique_label in unique_labels:
        bucket[unique_label] = (np.count_nonzero(labels == unique_label))
    tree_node.bucket = bucket
    if len(unique_labels) == 1:
        g.edge(node, bucketToString(bucket, mode, preprune))
        tree_node.leaf = 1
        tree_node.dec = np.argmax(np.array(bucket))
        tree_node.bucket = bucket
        # printNode(tree_node)
        return tree_node
    else:
        column = -1
        max_info_gain = np.array([0, float(0)])
        if mode == "info_gain":
            max_info_gain = np.array([0, float('-inf')])
        elif mode == "avg_gini_index":
            max_info_gain = np.array([0, float('inf')])
        attribute_count = len(data[0])

        for attribute_index in range(attribute_count):
            split_value = calculate_split_values(data, labels, 3, attribute_index, mode)
            for split_candidate in range(len(split_value)):
                if mode == "info_gain":
                    if split_value[split_candidate][1] > max_info_gain[1]:
                        max_info_gain = split_value[split_candidate]
                        column = attribute_index
                elif mode == "avg_gini_index":
                    if split_value[split_candidate][1] < max_info_gain[1]:
                        max_info_gain = split_value[split_candidate]
                        column = attribute_index

        left_node_data = []
        left_node_labels = []

        right_node_data = []
        right_node_labels = []

        tree_node.split_value = max_info_gain
        for t in range(len(data)):
            if mode == "info_gain":
                if data[t][column] < max_info_gain[0]:
                    left_node_data.append(data[t])
                    left_node_labels.append(labels[t])
                else:
                    right_node_data.append(data[t])
                    right_node_labels.append(labels[t])
            elif mode == "avg_gini_index":
                if data[t][column] > max_info_gain[0]:
                    right_node_data.append(data[t])
                    right_node_labels.append(labels[t])
                else:
                    left_node_data.append(data[t])
                    left_node_labels.append(labels[t])
        tree_node.column = column
        if node == "root":
            g.node(str(
                "x[" + str(column) + "] < " + str(max_info_gain[0]) + "\n" + bucketToString(bucket, mode, preprune)))

        left_bucket = [0] * 3
        right_bucket = [0] * 3

        for no_of_class in range(3):
            left_bucket[no_of_class] = np.count_nonzero(np.array(left_node_labels) == no_of_class)
            right_bucket[no_of_class] = np.count_nonzero(np.array(right_node_labels) == no_of_class)

        chi, dof = chi_squared_test(left_bucket, right_bucket)
        if dof == 1 and chi > df1 or (dof == 2 and chi > df2) or preprune == 0:
            if node != "root":
                g.edge(node,
                       "x[" + str(column) + "] < " + str(max_info_gain[0]) + "\n" + bucketToString(bucket, mode,
                                                                                                   preprune))
            tree_node.left = decision_tree(np.array(left_node_data), np.array(left_node_labels),
                                           str("x[" + str(column) + "] < " + str(
                                               max_info_gain[0]) + "\n" + bucketToString(bucket, mode,
                                                                                         preprune)),
                                           mode, preprune)
            tree_node.right = decision_tree(np.array(right_node_data), np.array(right_node_labels),
                                            str("x[" + str(column) + "] < " + str(
                                                max_info_gain[0]) + "\n" + bucketToString(bucket, mode,
                                                                                          preprune)),
                                            mode, preprune)
        else:
            tree_node.dec = np.argmax(np.array(bucket))
            tree_node.leaf = 1
            g.edge(node, bucketToString(bucket, mode, preprune))
    return tree_node


def make_prediction(data, node):
    if node.leaf == 0:
        if data[node.column] < node.split_value[0]:
            return make_prediction(data, node.left)
        else:
            return make_prediction(data, node.right)
    else:
        return node.dec


def calculate_test_accuracy(root):
    tst = 0
    for i in range(len(test_data)):
        true_label = test_labels[i]
        dec = make_prediction(test_data[i], root)
        if true_label == dec:
            tst += 1

    print(tst / len(test_data))
    return tst / len(test_data)


if __name__ == "__main__":
    # mode = "avg_gini_index"
    mode = "info_gain"
    preprune = 0

    root = decision_tree(train_data, train_labels, "root", mode, preprune)
    calculate_test_accuracy(root)
    g.view()
