#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Jul 11 11:57:06 2020

@author: niki
"""

#importing packages_________________________________________

import tsfel
import statistics 
import zipfile
import math
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import itertools as it
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import GaussianNB


df_positive = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/processed/10padcev.txt",names=list(range(0,17)))

df_negative = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiFALSE/processed/random3m0.txt",names=list(range(0,17)))


data_positive = df_positive.to_numpy()
data_negative = df_negative.to_numpy()


data_positive = np.delete(data_positive,0,0)
data_negative = np.delete(data_negative,0,0)

data_negative = data_negative.astype(int)
data_positive = data_positive.astype(int)


##all data in one 2D array
data = np.concatenate((data_negative,data_positive))



X = data[:,range(16)] #atributi senzorjev
y = data[:,16] #CLASS atribute


## split data into train and test set
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)


model = GaussianNB()
model.fit(X_train, y_train);


y_predicted = model.predict(X_test)

count = 0

for t,p in zip(y_test,y_predicted):
    if t==p:
        count+=1

tocnost = count / len(y_test)

print(tocnost)




###import other test Data




