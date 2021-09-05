import numpy as np


def forward(A, B, pi, O):
    """
    Calculates the probability of an observation sequence O given the model(A, B, pi).
    :param A: state transition probabilities (NxN)
    :param B: observation probabilites (NxM)
    :param pi: initial state probabilities (N)
    :param O: sequence of observations(T) where observations are just indices for the columns of B (0-indexed)
        N is the number of states,
        M is the number of possible observations, and
        T is the sequence length.
    :return: The probability of the observation sequence and the calculated alphas in the Trellis diagram with shape
             (N, T) which should be a numpy array.
    """
    aSize, oSize, alpha = A.shape[0], O.shape[0], []

    for a in range(aSize):
        alpha.append([])
        for o in range(oSize):
            alpha[a].append(0.0)
    alpha = np.array(alpha)

    for o in range(oSize):
        oi = O[o]
        if o == 0:
            B1 = B[:, oi]
            alpha[:, 0] = pi * B1
        else:
            alpha_calc = alpha[:, o - 1]
            for a in range(aSize):
                alpha[a, o] = sum(alpha_calc * (A[:, a]) * B[a, oi])
    answer = sum(alpha[:, oSize - 1])
    return answer, alpha


def viterbi(A, B, pi, O):
    """
    Calculates the most likely state sequence given model(A, B, pi) and observation sequence.
    :param A: state transition probabilities (NxN)
    :param B: observation probabilites (NxM)
    :param pi: initial state probabilities(N)
    :param O: sequence of observations(T) where observations are just indices for the columns of B (0-indexed)
        N is the number of states,
        M is the number of possible observations, and
        T is the sequence length.
    :return: The most likely state sequence with shape (T,) and the calculated deltas in the Trellis diagram with shape
             (N, T). They should be numpy arrays.
    """
    aSize, oSize, delta, reverse_path, result = A.shape[0], O.shape[0], [], [], []

    for a in range(aSize):
        delta.append([])
        reverse_path.append([])
        for o in range(oSize):
            delta[a].append(0.0)
            reverse_path[a].append(0.0)
            if a == 0:
                result.append(0.0)
    delta = np.array(delta)
    reverse_path = np.array(reverse_path)

    for o in range(oSize):
        oi = O[o]
        if o == 0:
            B1 = B[:, oi]
            delta[:, 0] = B1 * pi
        else:
            deltaCalc = delta[:, o - 1]
            for a in range(aSize):
                delta[a, o] = np.max(deltaCalc * (A[:, a]) * (B[a, oi]))
                reverse_path[a, o] = np.argmax(deltaCalc * (A[:, a]) * (B[a, oi]))

    for i in range(oSize):
        if i == 0:
            result[i] = int(np.argmax(delta[:, oSize - 1]))
        else:
            result[i] = reverse_path[int(result[i - 1]), oSize - i]
    result.reverse()
    return np.array(result), delta
