from nb import test, vocabulary, estimate_pi, estimate_theta


def func():
    train_data = open('hw4_data/sentiment/train_data.txt', 'r').read()
    train_data = [line.split() for line in train_data.split("\n") if line]
    train_labels = open('hw4_data/sentiment/train_labels.txt', 'r').read().splitlines()

    test_data = open('hw4_data/sentiment/test_data.txt', 'r').read()
    test_data = [line.split() for line in test_data.split("\n") if line]
    test_labels = open('hw4_data/sentiment/test_labels.txt', 'r').read().splitlines()

    vocab = vocabulary(train_data)
    pi = estimate_pi(train_labels)
    theta = estimate_theta(train_data, train_labels, vocab)
    scores = test(theta, pi, vocab, test_data)

    predicted_test_labels = []
    for i in range(len(scores)):
        maximum = scores[i][0][0]
        index = 0
        for j in range(1, len(scores[i])):
            if scores[i][j][0] > maximum:
                maximum = scores[i][j][0]
                index = j
        predicted_test_labels.append(scores[i][index][1])

    val = 0
    for i in range(len(predicted_test_labels)):
        if predicted_test_labels[i] == test_labels[i]:
            val += 1
    print(val/len(predicted_test_labels))


if __name__ == "__main__":
    func()
