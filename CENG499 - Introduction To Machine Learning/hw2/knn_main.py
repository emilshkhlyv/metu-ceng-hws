import numpy as np
import matplotlib.pyplot as plt

from knn import cross_validation, knn

if __name__ == "__main__":
    train_data = np.load("hw2_data/knn/train_data.npy")
    train_labels = np.load("hw2_data/knn/train_labels.npy")
    test_data = np.load("hw2_data/knn/test_data.npy")
    test_labels = np.load("hw2_data/knn/test_labels.npy")

    points = []
    for i in range(1, 200):
        points.append(cross_validation(train_data, train_labels, i, 10))

    nays_k = max(points)
    nays_k_index = points.index(nays_k)+1
    print("Suitable k value: ", nays_k_index, " Accuracy: ", knn(train_data, train_labels, test_data, test_labels, nays_k_index))
    x = [*range(1, 200, 1)]
    y = points

    plt.plot(x, y)
    plt.xlabel('k_knn values')
    plt.ylabel('average accuracies')

    ax = plt.subplot(111)
    ax.set_xlim(1, 200)
    dim = np.arange(1, 200, 66)
    plt.xticks(dim)
    plt.grid()
    plt.title('k-fold cross-validation plot')
    plt.show()
