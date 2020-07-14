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

df = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/features/10padcev.txt",names=list(range(0,16)))


newDF = pd.DataFrame(columns=[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15])



#zbrise vse 0 
#resi samo vrstice, ki imajo vec kot 2 ali 3 meritve vecj od 0
#rotira 4x za 90stopinj meritve in jih resi v nov df
for row in df.itertuples():
    m = np.array([[row[1],row[2],row[3],row[4]],[row[5],row[6],row[7],row[8]],[row[9],row[10],row[11],row[12]],[row[13],row[14],row[15],row[16]]])  
    i = 0       #vrednosti v arrayu
    for r in m:
        for v in r:
            if v > 0:
                i=i+1
        if i >= 3: #poskusi postavit na 2 ali 3   - oznaci stevilo vrednost vecjih od 0 v polju
            for k in range(4):
                line = np.rot90(m,k).ravel()
                newDF.loc[len(newDF.index)]=line ##vstavi element na koncu



#dodaj vrstico napovedi CLASS
newDF["class"]=1


#resi na datoteko
newDF.to_csv('/home/niki/Desktop/tesi/data_registered/testnipodatkiTRUE/processed/10padcev.txt', index = False)




