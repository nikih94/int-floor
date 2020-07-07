#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jul  3 16:33:51 2020

@author: niki
"""
import tsfel
import statistics 
import zipfile
import math
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import itertools as it
#import serial


df = pd.read_csv("/home/niki/Desktop/tesi/data_firstSample/50hz/korakanje",names=list(range(0,17)))

#df.rolling(50).sum()

#dodaj row number
df['n'] = np.arange(len(df))

#plot out visualization 16 graphs

fig, axs = plt.subplots(16, sharex=True, sharey=False,figsize=(15,45))
fig.suptitle('padec1')
for i in range(16):
    axs[i].plot(df['n'],df[i])


#plot only one graph
oneColumn = df[4]
indexList = list(range(100)) +  list(range(250,350))
plt.plot(df['n'].drop(indexList),oneColumn.drop(indexList))

    




#feature extraction by TSFEL  __________________________

enaKolona = df[6]
enaKolona


cfg_file = tsfel.get_features_by_domain()                                                                              # If no argument is passed retrieves all available features
X_train = tsfel.time_series_features_extractor(cfg_file, enaKolona, fs=50, window_splitter=True, window_size=50)    # Receives a time series sampled at 50 Hz, divides into windows of size 250 (i.e. 5 seconds) and extracts all features

print(X_train)


X_train.to_csv('featurekorakanjeStoj20s.csv')  


#_________________________________________________________



#Moving window ________ function def

def moving_window(x, length, step=1):
    streams = it.tee(x, length)
    return zip(*[it.islice(stream, i, None, step) for stream, i in zip(streams, it.count(step=step))])

#_____________________________________
    

#moving window example:
x=[1,2,3,4,5,6,7,8,9]
x_=list(moving_window(x, 5))
x_=np.asarray(x_)
print(x_)
#____________end example




#define function to execute on moving window

def festureEng(l):
    #find if the data has a peak in the first half
    peak = findPeak(l[:25])
    if not peak:
        return False
    tail = findTail(l[25:])
    if tail:
        return 2
    else:
        return 1
    


#function to evalueate if ve have a stationary tail

def findTail(x):
    mean = sum(x) / len(x)
    std = statistics.stdev(x)
    if (mean > 500) and ( std < 5 ):
        return True
    return False

#_____________________________________________________
  
    

   


#function to find peak in 25 values list

def findPeak(x):
    h = 500 #delta(max,min)>h
    m = 500 #meja za min1 mora biti manjsi od te stevilke drugace ni en peak za padec
    k = 4 #faktor repa
    min1 = np.inf
    min2 = np.inf
    maxx = 0
    for i in range(17): #index za premikanje okna
        for value in x[i:i+10]: #premikajse po 10 
            if (min1 > value) and (maxx == 0) and (math.isinf(min2)):
                min1 = value
            elif (maxx < value) and (math.isinf(min2)):
                maxx = value
            elif min2 > value:
                min2 = value
            elif (maxx < value) and (not math.isinf(min2)):
                #pomisli ce postavit min1 = min2 za zaznati pravi spike drugace zaznamo tudi spice bolj siroke
                maxx = value
                min2 = np.inf
            if ( min1 < m ) and (min1 < min2) and ((maxx - min2) > ((maxx - min1 ) / k)) and ((maxx - min1) > h):
                return True
        min1 = np.inf
        min2 = np.inf
        maxx = 0
    return False
    
  
    
x=[0,0,1,40,10,0,78,200,1000,600,0,0,0,0,0,100,200,4000,500,0,1,2,3,4,5]
y=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,100,1000,10000,6000]
len(y)


findPeak(y)


#__________________________________________



#moving window test on data
enaKolona = df[4]

print(enaKolona)

res = list(moving_window(enaKolona,50))
print(res)

for l in res:
    
    

