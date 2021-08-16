import os

import numpy as np
import torch
import torchvision
import torch.nn.functional as F
import matplotlib.pyplot as plt

from torch.utils.data import DataLoader
from dataset import FashionDS
from mlmodel import MLModel


def train(model, epochs, train_dataloader, optimizer, valid_dataloader, till_the_best, device):
    train_losses = []
    average_t_losses = []

    validator_losses = []
    average_v_losses = []

    validator_loss = float('inf')
    best_loss_in_this_train = float('inf')
    success = float('inf')
    for epoch in range(epochs):
        ans = 0
        model.train()
        for images, labels in train_dataloader:
            optimizer.zero_grad()
            (F.nll_loss(model(images.to(device)), labels.to(device))).backward()
            optimizer.step()
            train_losses.append(F.nll_loss(model(images.to(device)), labels.to(device)).item())
        model.eval()
        for images, labels in valid_dataloader:
            _, result = torch.max(model(images.to(device)), 1)
            ans += torch.sum(result == labels.to(device))
            validator_loss = F.nll_loss(model(images.to(device)), labels.to(device))
            validator_losses.append(validator_loss.item())

        average_v_losses.append(np.average(validator_losses))
        average_t_losses.append(np.average(train_losses))

        print(
            f'[{epoch:>{(len(str(epochs)))}}/{epochs:>{(len(str(epochs)))}}] ' + f'TL: {np.average(train_losses):.5f} ' + f'VL: {np.average(validator_losses):.5f}')
        if validator_loss < best_loss_in_this_train:
            best_loss_in_this_train = validator_loss
            success = ans
            if best_loss_in_this_train < till_the_best:
                torch.save(model.state_dict(), 'model_state_dict')
    return model, best_loss_in_this_train, success, average_t_losses, average_v_losses, ans


def main():
    use_cuda = False
    device = torch.device('cuda' if use_cuda else 'cpu')
    epochs = 30
    torch.manual_seed(123)

    best_hyperparameters_till_now = (float('-inf'), float('-inf'), float('-inf'), float('-inf'))
    best_hyperparameter_loss_till_now = float('inf')

    transforms = torchvision.transforms.Compose([
        torchvision.transforms.ToTensor(),
        torchvision.transforms.Normalize((0.5,), (0.5,)),
    ])
    train_dataset = FashionDS('data', 'train', transforms)
    test_dataset = FashionDS('data', 'test', transforms)
    train_dataset, validator_dataset = torch.utils.data.random_split(train_dataset, [25000, 5000])

    train_dataloader = DataLoader(train_dataset, batch_size=64, shuffle=True, num_workers=8)
    validator_dataloader = DataLoader(validator_dataset, batch_size=64, shuffle=True, num_workers=8)
    test_dataloader = DataLoader(test_dataset, batch_size=64, shuffle=True, num_workers=8)

    train_loss = []
    valid_loss = []

    learningRates = [0.1, 0.03, 0.01, 0.003, 0.001, 0.0003, 0.0001, 0.00003]
    layerCounts = [2, 3, 4]
    layerSizes = [356, 512, 1024]
    activationFunctions = ['relu', 'sigmoid', 'tanh']

    with open("validator.txt", "w") as validator_file:
        for learningRate in learningRates:
            validator_file.write('\n')
            print('\n')
            for layerCount in layerCounts:
                validator_file.write('\n')
                print('\n')
                for size in layerSizes:
                    validator_file.write('\n')
                    print('\n')
                    if layerCount == 2:
                        validator_file.write(f'Layer Count: {layerCount} AF: none  Size: {size} LR: {learningRate}\n')
                        print(f'Layer Count: {layerCount} AF: none  Size: {size} LR: {learningRate}\n')
                        mlmodel = MLModel(layerCount, 'none', size)
                        mlmodel.to(device)
                        optimizer = torch.optim.Adam(mlmodel.parameters(), lr=learningRate)
                        model, best_loss_in_this_train, success, average_t_losses, average_v_losses, ans = train(
                            mlmodel,
                            epochs,
                            train_dataloader,
                            optimizer,
                            validator_dataloader,
                            best_hyperparameter_loss_till_now,
                            device)
                        validator_file.write(f'SR: {success / len(validator_dataset)} VL: {best_loss_in_this_train}\n')
                        print(f'SR: {success / len(validator_dataset)} VL: {best_loss_in_this_train}')
                        if best_loss_in_this_train < best_hyperparameter_loss_till_now:
                            best_hyperparameter_loss_till_now = best_loss_in_this_train
                            best_hyperparameters_till_now = (layerCount, learningRate, size, 'none')
                            train_loss = average_t_losses
                            valid_loss = average_v_losses

                    elif layerCount == 3 or layerCount == 4:
                        for activationFunction in activationFunctions:
                            validator_file.write(
                                f'Layer Count: {layerCount} AF: {activationFunction}  Size: {size} LR: {learningRate}\n')
                            print(
                                f'Layer Count: {layerCount} AF: {activationFunction}  Size: {size} LR: {learningRate}\n')
                            mlmodel = MLModel(layerCount, activationFunction, size)
                            mlmodel.to(device)
                            optimizer = torch.optim.Adam(mlmodel.parameters(), lr=learningRate)
                            model, best_loss_in_this_train, success, average_t_losses, average_v_losses, ans = train(
                                mlmodel,
                                epochs,
                                train_dataloader,
                                optimizer,
                                validator_dataloader,
                                best_hyperparameter_loss_till_now,
                                device)
                            validator_file.write(
                                f'SR: {success / len(validator_dataset)} VL: {best_loss_in_this_train}\n')
                            print(f'SR: {success / len(validator_dataset)} VL: {best_loss_in_this_train}')
                            if best_loss_in_this_train < best_hyperparameter_loss_till_now:
                                best_hyperparameter_loss_till_now = best_loss_in_this_train
                                best_hyperparameters_till_now = (layerCount, learningRate, size, activationFunction)
                                train_loss = average_t_losses
                                valid_loss = average_v_losses

    fig = plt.figure(figsize=(10, 8))
    plt.plot(train_loss, label='Training Loss')
    plt.plot(valid_loss, label='Validation Loss')
    minposs = valid_loss.index(min(valid_loss))
    plt.axvline(minposs, color="g", label='Early Stopping Checkpoint')

    plt.ylabel('loss')
    plt.xlabel('epochs')
    plt.legend()
    plt.tight_layout()
    fig.savefig('resulting.png', bbox_inches='tight')

    bestOne = MLModel(best_hyperparameters_till_now[0], best_hyperparameters_till_now[3],
                      best_hyperparameters_till_now[2])
    bestOne.load_state_dict(torch.load('model_state_dict'))
    bestOne.eval()

    test_results = []
    num_of_pictures = 0
    for test_images, test_labels in test_dataloader:
        _, ans = torch.max(bestOne(test_images), 1)
        for i in range(ans.size(0)):
            name_of_picture, result = os.path.split(test_dataset.infos[num_of_pictures][0])
            test_results.append(f'{result} ' + f'{ans[i].item()}\n')
            num_of_pictures += 1
    with open("testFile.txt", "w") as f:
        f.writelines(test_results)


if __name__ == '__main__':
    main()
