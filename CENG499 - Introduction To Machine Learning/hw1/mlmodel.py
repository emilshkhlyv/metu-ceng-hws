import torch
import torch.nn as nn
import torch.nn.functional as F


class MLModel(nn.Module):
    def __init__(self, layerCount, activationFunction, layerSize):
        super(MLModel, self).__init__()
        if layerCount   == 2:
            self.linear1 = nn.Linear(4800, 10)
            self.layerCount = 2
        elif layerCount == 3:
            self.linear1 = nn.Linear(4800, layerSize)
            self.linear2 = nn.Linear(layerSize, 10)
            self.layerCount = 3
            self.activationFunction = activationFunction
        elif layerCount == 4:
            self.linear1 = nn.Linear(4800, layerSize)
            self.linear2 = nn.Linear(layerSize, layerSize)
            self.linear3 = nn.Linear(layerSize, 10)
            self.layerCount = 4
            self.activationFunction = activationFunction

    def forward(self, x):
        x = self.linear1(x.view(x.size(0), -1))
        if self.layerCount > 2:
            if self.activationFunction   == 'sigmoid':
                x = self.linear2(F.sigmoid(x))
            elif self.activationFunction == 'tanh':
                x = self.linear2(F.tanh(x))
            elif self.activationFunction == 'relu':
                x = self.linear2(F.relu(x))
        if self.layerCount > 3:
            if self.activationFunction   == 'sigmoid':
                x = self.linear3(F.sigmoid(x))
            elif self.activationFunction == 'tanh':
                x = self.linear3(F.tanh(x))
            elif self.activationFunction == 'relu':
                x = self.linear3(F.relu(x))
        return torch.log_softmax(x, dim=1)
