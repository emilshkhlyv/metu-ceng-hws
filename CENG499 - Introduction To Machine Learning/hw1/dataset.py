import os
from torch.utils.data import Dataset, DataLoader
from PIL import Image
import torchvision.transforms


class FashionDS(Dataset):
    def __init__(self, dataset_path, split, transforms_as_arg):
        self.infos = []
        self.transforms = transforms_as_arg
        with open(os.path.join(os.path.join(dataset_path, split), 'labels.txt'), 'r') as file:
            if split == 'train':
                for line in file:
                    self.infos.append((os.path.join(os.path.join(dataset_path, split), line.split()[0]), int(line.split()[1])))
            elif split == 'test':
                for line in file:
                    self.infos.append((os.path.join(os.path.join(dataset_path, split), line.rstrip('\n')), int(0)))

    def __len__(self):
        return len(self.infos)

    def __getitem__(self, index):
        return self.transforms(Image.open(self.infos[index][0])), self.infos[index][1]
