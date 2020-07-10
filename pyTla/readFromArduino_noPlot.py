#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Jul  9 09:58:41 2020

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

#import for arduino
import serial
import time


#feature extraction funkcije


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
    


####klic zgornjih funkcij

def extract(df):
    columnRes = list()
    for col in df:
        columnRes.append(featureEng(list(df[col])))
    columnIn = list(df.loc[0])
    print("in: "+str(columnIn).strip('[]')+" features: "+str(columnRes).strip('[]'))




#setup serial communication
ser = serial.Serial('/dev/ttyUSB0', 115200)
ser.flushInput()




#Zanka ki bere serial in
vrstaDF = pd.DataFrame([[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]],columns=[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15])

while True:
    try:
        ser_bytes = ser.readline()
        data_serial = ser_bytes[0:len(ser_bytes)-3].decode('utf-8')
        l=data_serial.split(sep=",")
        l=list(map(int,l))
        if len(vrstaDF.index) < 50:
            vrstaDF.loc[len(vrstaDF.index)]=l ##vstavi element na koncu
        else:
            vrstaDF.loc[len(vrstaDF.index)]=l ##vstavi element na koncu
            vrstaDF.drop(0,inplace=True,errors="ignore") #zbrisi prvi element v vrsti in vstavi na konec vrste
            vrstaDF.reset_index(drop=True,inplace=True) #resetira index dataframa
            extract(vrstaDF)
                        
    except:
        print("Keyboard Interrupt")
        break







