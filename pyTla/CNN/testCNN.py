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
import numpy as np
import itertools as it
from sklearn.model_selection import train_test_split
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
from mpl_toolkits.mplot3d import Axes3D
import seaborn as sns
from tqdm import tqdm
import dask.dataframe as dd
from functions import df_to_cnn_rnn_format 


from keijzer import setup_multi_gpus, create_corr_matrix, reduce_memory, resample_df


from keras.models import Sequential
from keras.layers.core import Dense, Activation, Dropout, Flatten
from keras.layers import TimeDistributed
from keras.layers.recurrent import LSTM
from keras.layers import Dense, Conv1D, MaxPool2D, Flatten, Dropout,  Conv2D, MaxPooling2D
from keras.callbacks import EarlyStopping, TensorBoard, ModelCheckpoint, EarlyStopping
from keras.optimizers import Adam, SGD, Nadam
from time import time
from keras.layers.advanced_activations import LeakyReLU, PReLU
import tensorflow as tf
from tensorflow.python.client import device_lib
from sklearn.preprocessing import StandardScaler
from keras.models import load_model

#????
from keijzer import *

####
#sns.set()
#num_gpu = setup_multi_gpus()






## CREATE TRAIN AND TEST SET
df_positive = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/processedCNN/10padcev.txt")

df_negative = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiFALSE/processed/random3m0.txt")


# =============================================================================
# for i in range(2000):
#     df_positive.loc[len(df_positive.index)]=df_negative.iloc[i,:]
# 
# 
# dataSet = df_negative.append(df_positive)
# =============================================================================



###manjsi dataset max 5000 data

dataSet = pd.DataFrame(columns=[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,"class"])

for i in range(2000):
    dataSet.loc[len(dataSet.index)]=np.array(df_negative.iloc[i,:])
       
for i in range(2500):
    dataSet.loc[len(dataSet.index)]=np.array(df_positive.iloc[i,:])
    
for i in range(2000,2500):
    dataSet.loc[len(dataSet.index)]=np.array(df_negative.iloc[i,:])
    
    
X_train, y_train, X_test, y_test = df_to_cnn_rnn_format(dataSet, test_size=0.8, look_back=10, target_column='class', scale_X=False)


#####Adding CHANNEL dimension to the X data.
X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], X_train.shape[2], 1)
X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], X_test.shape[2], 1)
X_train.shape




########## data is ready

########################CNN model


def create_model(X_train, y_train, X_test, y_test):
    input_shape = (X_train.shape[1], X_train.shape[2], X_train.shape[3])

    model = Sequential()
    
    ks1_first = 3
    ks1_second = 8
    
    ks2_first = 4
    ks2_second = 5
    
    model.add(Conv2D(filters=(3), 
                     kernel_size=(ks1_first, ks1_second),
                     input_shape=input_shape, 
                     padding='same',
                     activation="relu",
                     kernel_initializer='TruncatedNormal'))
    model.add(Conv2D(filters=(64), 
                     kernel_size=(ks1_first, ks1_second),
                     activation="relu",)
    model.add(Dropout(0.5))
    model.
    
    for _ in range(2):
        model.add(Conv2D(filters=(4), 
                     kernel_size= (ks2_first, ks2_second), 
                         padding='same',
                     kernel_initializer='TruncatedNormal'))
        model.add(BatchNormalization())
        model.add(LeakyReLU())
        model.add(Dropout(0.280))  
    
    model.add(Flatten())
    
    for _ in range(4):
        model.add(Dense(64 , kernel_initializer='TruncatedNormal'))
        model.add(BatchNormalization())
        model.add(LeakyReLU())
        model.add(Dropout(0.435))
    
    for _ in range(3):
        model.add(Dense(128 , kernel_initializer='TruncatedNormal'))
        model.add(BatchNormalization())
        model.add(LeakyReLU())
        model.add(Dropout(0.372))
  
    model.add(Dense(1024 , kernel_initializer='TruncatedNormal'))
    model.add(BatchNormalization())
    model.add(LeakyReLU())
    model.add(Dropout(0.793))
        
    model.add(Dense(1,activation="sigmoid"))
    
    return model


#print(model.summary())

#multi_model = multi_gpu_model(model, gpus=num_gpu)



###########################


#############Compile the model

"""
Look back , 5
nodes, 35

More only makes the model more complex and harder/slower to train
"""

epochs = 30
bs = 64
lr = 1e-3 #6e-4
print(bs)


model = create_model(X_train, y_train, X_test, y_test)

# 0.05 0.9 0 True
sgd = SGD(lr=0.5, momentum=0.9, decay=0, nesterov=True) # sgd in general yields better results, but needs a lot of tweeking and is slower
adam = Adam(lr=lr)
nadam = Nadam(lr=lr)

model.compile(optimizer='nadam', loss = ['mse'], metrics=[mape, smape, 'mse'])


###FIT the model
early_stopping_monitor = EarlyStopping(patience=5000)



# This is used to save the best model, currently monitoring val_mape
# checkpoint
filepath="models\\CNN.best.hdf5"
checkpoint = ModelCheckpoint(filepath, monitor='val_loss', verbose=1, save_best_only=True, mode='min')


epoch_size = 56
schedule = SGDRScheduler(min_lr= 9e-7 ,max_lr= 4.3e-3 ,steps_per_epoch=np.ceil(epoch_size/bs),lr_decay=0.9, cycle_length=5,mult_factor=1.5)



model.fit(X_train, y_train, epochs=epochs, batch_size=bs, validation_split=0.2,verbose=1, callbacks=[ early_stopping_monitor, checkpoint, schedule])

print(model.summary())




#load a previously saved model
#============================================================================
# Load the architecture
model = load_model('models\\CNN.best.hdf5', custom_objects={'smape': smape, 
                                                    'mape': mape}) # Gave an error when loading without 'custom_objects'.. fixed by https://github.com/keras-team/keras/issues/3911

# Compile with the same settings as it has been saved with earlier
model.compile(loss='mse', metrics=[mape, smape], optimizer=adam)

print('FINISHED')
#=============================================================================

y_pred = model.predict(X_test)

y_pred
