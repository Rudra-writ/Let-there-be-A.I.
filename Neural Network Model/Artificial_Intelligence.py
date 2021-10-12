
import random
import os
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torch.autograd as autograd
from torch.autograd import Variable

# Creating the architecture of the Neural Network

class NeuralNetwork(nn.Module):
    
    def __init__(self, input_size, output_size):
        super(NeuralNetwork, self).__init__()
        self.input_size = input_size
        self.output_size = output_size
        self.fc1 = nn.Linear(input_size, 36)
      
        self.fc2 = nn.Linear(36, output_size)
    
    def forward(self, input_state):
        x = F.relu(self.fc1(input_state))
       
        q_values = self.fc2(x)
        return q_values

# Implementing Experience Replay

class ExperienceReplay(object):
    
    def __init__(self, total):
        self.total = total
        self.experiences= []
    
    def store(self, event):
        self.experiences.append(event)
        if len(self.experiences) > self.total:
            del self.experiences[0]
    
    def batches(self, batch_size):
        batch = zip(*random.sample(self.experiences, batch_size))
        return map(lambda x: Variable(torch.cat(x, 0)), batch)

# Implementing Deep Q Learning

class AI():
    
    def __init__(self, input_size, output_size, gamma):
        self.gamma = gamma
        self.model = NeuralNetwork(input_size, output_size)
        self.memory = ExperienceReplay(100000)
        self.optimizer = optim.Adam(self.model.parameters(), lr = 0.002)
        self.last_state = torch.Tensor(input_size).unsqueeze(0)
        self.last_action = 0
        self.last_reward = 0
    
    def final_action(self, input_state):
        probabilities = F.softmax(self.model(Variable(input_state, volatile = True))*70) # T=100
        action = probabilities.multinomial(1)
        return action.data[0,0]
    
    def training(self, batch_state, batch_next_state, batch_reward, batch_action):
        batch_outputs = self.model(batch_state).gather(1, batch_action.unsqueeze(1)).squeeze(1)
        batch_next_outputs = self.model(batch_next_state).detach().max(1)[0]
        batch_targets = self.gamma*batch_next_outputs + batch_reward
        batch_loss = F.smooth_l1_loss(batch_outputs, batch_targets)
        self.optimizer.zero_grad()
        batch_loss.backward(retain_graph = True)
        self.optimizer.step()
    
    def update(self, reward, new_signal):
        new_state = torch.Tensor(new_signal).float().unsqueeze(0)
        self.memory.store((self.last_state, new_state, torch.LongTensor([int(self.last_action)]), torch.Tensor([self.last_reward])))
        action = self.final_action(new_state)
        if len(self.memory.experiences) > 200:
            batch_state, batch_next_state, batch_action, batch_reward = self.memory.batches(200)
            self.training(batch_state, batch_next_state, batch_reward, batch_action)
        self.last_action = action
        self.last_state = new_state
        self.last_reward = reward
        return action
   
    def save(self):
        torch.save({'state_dict': self.model.state_dict(),
                    'optimizer' : self.optimizer.state_dict(),
                   }, 'memory.pth')
    
    def load(self):
        if os.path.isfile('memory.pth'):
            checkpoint = torch.load('memory.pth')
            self.model.load_state_dict(checkpoint['state_dict'])
            self.optimizer.load_state_dict(checkpoint['optimizer'])
            print("last memory loaded")
        else:
            print("no memory...")