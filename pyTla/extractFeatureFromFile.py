#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Jul  7 14:46:08 2020

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
#import serial


#import data:_______________________________________________

df = pd.read_csv("/home/niki/Desktop/tesi/data_firstSample/50hz/korakanjeDolgo",names=list(range(0,17)))

#dodaj row number
df['n'] = np.arange(len(df))
#define functions for feature extraction______________________________________



#define function to execute on moving window

def featureEng(l):
    #find if the data has a peak in the first half
    peak = findPeak(l[:25])
    if not peak:
        return 0
    tail = findTail(l[25:])
    if tail:
        return 2
    else:
        return 1
    


#function to evalueate if ve have a stationary tail

def findTail(x):
    mean = sum(x) / len(x)
    std = np.std(x)
    #print("mean: " + str(mean) +" std: " + str(std))
    cv = std / mean #CV -  coefficient of variation
    if (mean > 500 ) and ( cv < 0.20 ): ##cv lahko postavis tudi na 0.1???
        return True
    return False

#_____________________________________________________
  
    

   


#function to find peak in 25 values list
#na zacetku je bil window size 10 sem opazil, da deluje bolje window size 5 tako,da bolje zaznamo peake ozje    


def findPeak(x):
    h = 500 #delta(max,min)>h
    m = np.inf #meja za min1 mora biti manjsi od te stevilke drugace ni en peak za padec (lahko postavis tudi na np.inf)
    k = 8 #faktor repa
    min1 = np.inf
    min2 = np.inf
    maxx = 0
    for i in range(22): #index za premikanje okna prej je bilo 17
        for value in x[i:i+5]: #premikajse po 10 
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
    



#Moving window ________ function def

def moving_window(x, length, step=1):
    streams = it.tee(x, length)
    return zip(*[it.islice(stream, i, None, step) for stream, i in zip(streams, it.count(step=step))])

#_____________________________________
    




#Define function that performs feature extraction on one column




def extractFromColumn(myColumn):
    windowList = list(moving_window(myColumn, 50))
    res =list()
    for item in windowList:
        res.append(featureEng(item))
    return res



def extractFromDF(data):
    res = pd.DataFrame()
    i=0 #index imen colon
    for col in data:
        res.insert(0,i, extractFromColumn(data[col]),True)
        i=1+i
    return res
        
        

###__________________________________________________________________________
    


#test
    

featureOfDF = extractFromDF(df)



##### plot everything out

newDF = df.drop(range(len(featureOfDF.index),len(df.index)))

fig, axs = plt.subplots(16, sharex=True, sharey=False,figsize=(15,45))
fig.suptitle('padec1')
for i in range(16):
    axs[i].plot(newDF['n'],newDF[i])
    axs2 = axs[i].twinx()
    axs2.set_ylim([0,2])
    axs2.plot(newDF['n'],featureOfDF[i],color='tab:red')





















