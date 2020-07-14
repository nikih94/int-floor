#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jul 10 15:46:28 2020

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
file1 = open("/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/features/10padcev.txt", 'r') 
Lines = file1.readlines() 

count=0

X_train = list()
fall = list()
row = list()
Y_train = list()

for line in Lines: 
    if line.strip() == "next":
        Y_train.insert(len(Y_train),1)
        X_train.insert(len(X_train),fall)
        fall = list()
    else:
        line = line.strip().split(sep=",")
        row = list()
        for el in line:
            row.insert(len(row),el)
        fall.insert(len(fall),row)
       
#Ustvari rotirane podatke      
orig_len = len(X_train)

for k in range(3):
    for i in range(orig_len):
        fall = X_train[i]
        rotatedFall = list()
        Y_train.insert(len(Y_train),1)
        for row in fall:
            m = np.array([[row[0],row[1],row[2],row[3]],[row[4],row[5],row[6],row[7]],[row[8],row[9],row[10],row[11]],[row[12],row[13],row[14],row[15]]]) 
            line = np.rot90(m,k).ravel()
            rotatedFall.insert(len(rotatedFall),line)
        X_train.insert(len(X_train),rotatedFall)
        

X_train = np.array(X_train,dtype="int64")
Y_train = np.array(Y_train,dtype="int64")  