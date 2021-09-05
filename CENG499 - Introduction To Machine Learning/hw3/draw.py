import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.svm import SVC
from sklearn.model_selection import GridSearchCV


def draw_svm(clf, x, y, x1_min, x1_max, x2_min, x2_max, target=None):
    """
    Draws the decision boundary of an svm.
    :param clf: sklearn.svm.SVC classifier
    :param x: data Nx2
    :param y: label N
    :param x1_min: minimum value of the x-axis of the plot
    :param x1_max: maximum value of the x-axis of the plot
    :param x2_min: minimum value of the y-axis of the plot
    :param x2_max: maximum value of the y-axis of the plot
    :param target: if target is set to path, the plot is saved to that path
    :return: None
    """
    y = y.astype(bool)
    xx, yy = np.meshgrid(np.linspace(x1_min, x1_max, 500),
                         np.linspace(x2_min, x2_max, 500))
    z = clf.decision_function(np.c_[xx.ravel(), yy.ravel()])
    z = z.reshape(xx.shape)
    disc_z = z > 0
    plt.clf()
    plt.imshow(disc_z, interpolation='nearest',
               extent=(xx.min(), xx.max(), yy.min(), yy.max()), aspect='auto',
               origin='lower', cmap=plt.cm.RdBu, alpha=.3)
    plt.contour(xx, yy, z, levels=[-1, 1], linewidths=2,
                linestyles='dashed', colors=['red', 'blue'], alpha=0.5)
    plt.contour(xx, yy, z, levels=[0], linewidths=2,
                linestyles='solid', colors='black', alpha=0.5)
    positives = x[y == 1]
    negatives = x[y == 0]
    plt.scatter(positives[:, 0], positives[:, 1], s=50, marker='o', color="none", edgecolor="black")
    plt.scatter(negatives[:, 0], negatives[:, 1], s=50, marker='s', color="none", edgecolor="black")
    sv_label = y[clf.support_]
    positive_sv = x[clf.support_][sv_label]
    negative_sv = x[clf.support_][~sv_label]
    plt.scatter(positive_sv[:, 0], positive_sv[:, 1], s=50, marker='o', color="white", edgecolor="black")
    plt.scatter(negative_sv[:, 0], negative_sv[:, 1], s=50, marker='s', color="white", edgecolor="black")
    plt.xlim(x1_min, x1_max)
    plt.ylim(x2_min, x2_max)
    plt.gca().set_aspect('equal', adjustable='box')
    if target is None:
        plt.show()
    else:
        plt.savefig(target)


if __name__ == "__main__":
    # # 2.1 First Part
    train_data = np.load('hw3_data/linsep/train_data.npy')
    train_labels = np.load('hw3_data/linsep/train_labels.npy')

    C_values = [0.01, 0.1, 1, 10, 100]
    for i in range(len(C_values)):
        clf = SVC(C_values[i], kernel='linear')
        clf.fit(train_data, train_labels)
        draw_svm(clf, train_data, train_labels, -3, 3, -3, 3)

    # # 2.2 Second Part
    train_data = np.load('hw3_data/nonlinsep/train_data.npy')
    train_labels = np.load('hw3_data/nonlinsep/train_labels.npy')

    kernel_values = ['rbf', 'linear', 'poly', 'sigmoid']
    for i in range(len(kernel_values)):
        clf = SVC(C=1, kernel=kernel_values[i])
        clf.fit(train_data, train_labels)
        draw_svm(clf, train_data, train_labels, -3, 3, -3, 3)

    # # 2.3 Third Part
    train_data = np.load('hw3_data/fashion_mnist/train_data.npy')
    train_labels = np.load('hw3_data/fashion_mnist/train_labels.npy')
    test_data = np.load('hw3_data/fashion_mnist/test_data.npy')
    test_labels = np.load('hw3_data/fashion_mnist/test_labels.npy')

    train_data = np.true_divide(train_data, 256)
    X = train_data.reshape(len(train_data), -1)
    y = train_labels

    parameters = [{'C': [0.01, 0.1, 1, 10, 100], 'kernel': ['sigmoid', 'poly', 'rbf'],
                   'gamma': [0.00001, 0.0001, 0.001, 0.01, 0.1, 1]},
                  {'C': [0.01, 0.1, 1, 10, 100], 'kernel': ['linear']}]

    clf = GridSearchCV(SVC(), parameters)
    clf.fit(X, y)

    means = clf.cv_results_['mean_test_score']

    print(clf.best_params_)
    for mean, params in zip(means, clf.cv_results_['params']):
        print("%0.3f for %r" % (mean, params))

    # 2.4 Fourth Part
    train_data = np.load('hw3_data/fashion_mnist_imba/train_data.npy')
    train_labels = np.load('hw3_data/fashion_mnist_imba/train_labels.npy')
    test_data = np.load('hw3_data/fashion_mnist_imba/test_data.npy')
    test_labels = np.load('hw3_data/fashion_mnist_imba/test_labels.npy')

    # 2.4.1 Without handling the imbalance problem
    train_data = np.true_divide(train_data, 256)
    test_data = np.true_divide(test_data, 256)

    X = train_data.reshape(len(train_data), -1)
    y = train_labels

    X_test = test_data.reshape(len(test_data), -1)
    y_test = test_labels

    parameters = {'C': [1], 'kernel': ['rbf']}

    clf = GridSearchCV(SVC(), parameters)
    clf.fit(X, y)

    y_true, y_pred = y_test, clf.predict(X_test)

    tn, fp, fn, tp = confusion_matrix(y_true, y_pred).ravel()

    print(classification_report(y_true, y_pred, digits=5))

    print("True negative: %d \nFalse positive: %d \nFalse negative: %d \nTrue positive: %d" %(tn, fp, fn, tp))

    # 2.4.2 Oversampling the minority class
    oversampling_train_data = []
    oversampling_train_label = []

    for i in range(len(train_data)):
        oversampling_train_data.append(train_data[i])
        oversampling_train_label.append(train_labels[i])

    unique_labels = []
    for j in oversampling_train_label:
        if j not in unique_labels:
            unique_labels.append(j)
    bucket = []

    for k in unique_labels:
        bucket.append(np.count_nonzero(oversampling_train_label == k))

    division = int(bucket[0] / bucket[1])
    length = len(oversampling_train_label)
    for i in range(length):
        if oversampling_train_label[i] == 0:
            for j in range(division):
                oversampling_train_label.append(oversampling_train_label[i])
                oversampling_train_data.append(oversampling_train_data[i])

    oversampling_train_label = np.array(oversampling_train_label)
    oversampling_train_data  = np.array(oversampling_train_data)

    # normalize train_data
    X = oversampling_train_data.reshape(len(oversampling_train_data), -1)
    y = oversampling_train_label

    X_test = test_data.reshape(len(test_data), -1)
    y_test = test_labels

    parameters = {'C': [1], 'kernel': ['rbf']}

    clf = GridSearchCV(SVC(), parameters)
    clf.fit(X, y)

    y_true, y_pred = y_test, clf.predict(X_test)

    tn, fp, fn, tp = confusion_matrix(y_true, y_pred).ravel()

    print(classification_report(y_true, y_pred, digits=5))

    print("True negative: %d \nFalse positive: %d \nFalse negative: %d \nTrue positive: %d" % (tn, fp, fn, tp))

    # 2.4.3 Undersampling the majority class
    undersampling_train_data = []
    undersampling_train_label = []

    for i in range(len(train_data)):
        undersampling_train_data.append(train_data[i])
        undersampling_train_label.append(train_labels[i])

    unique_labels = []
    for j in undersampling_train_label:
        if j not in unique_labels:
            unique_labels.append(j)
    bucket = []

    for k in unique_labels:
        bucket.append(np.count_nonzero(undersampling_train_label == k))

    i = 0
    while bucket[1] != bucket[0]:
        if undersampling_train_label[i] == 1:
            del undersampling_train_label[i]
            del undersampling_train_data[i]
            bucket[0] -= 1
        else:
            i += 1

    undersampling_train_label = np.array(undersampling_train_label)
    undersampling_train_data = np.array(undersampling_train_data)

    # normalize train_data
    X = undersampling_train_data.reshape(len(undersampling_train_data), -1)
    y = undersampling_train_label

    X_test = test_data.reshape(len(test_data), -1)
    y_test = test_labels

    parameters = {'C': [1], 'kernel': ['rbf']}

    clf = GridSearchCV(SVC(), parameters)
    clf.fit(X, y)

    y_true, y_pred = y_test, clf.predict(X_test)

    tn, fp, fn, tp = confusion_matrix(y_true, y_pred).ravel()

    print(classification_report(y_true, y_pred, digits=5))

    print("True negative: %d \nFalse positive: %d \nFalse negative: %d \nTrue positive: %d" % (tn, fp, fn, tp))

    # 2.4.4 Setting the class_weight to balanced
    train_data = np.true_divide(train_data, 256)
    test_data = np.true_divide(test_data, 256)

    X = train_data.reshape(len(train_data), -1)
    y = train_labels

    X_test = test_data.reshape(len(test_data), -1)
    y_test = test_labels

    parameters = {'C': [1], 'kernel': ['rbf']}

    clf = GridSearchCV(SVC(class_weight='balanced'), parameters)
    clf.fit(X, y)

    y_true, y_pred = y_test, clf.predict(X_test)

    tn, fp, fn, tp = confusion_matrix(y_true, y_pred).ravel()

    print(classification_report(y_true, y_pred, digits=5))

    print("True negative: %d \nFalse positive: %d \nFalse negative: %d \nTrue positive: %d" %(tn, fp, fn, tp))
