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

df = pd.read_csv("/home/niki/Desktop/tesi/data_registered/testnipodatkiFALSE/features/random3m0.txt",names=list(range(0,16)))

#odvzemi index vseh podatkov kjer so samo 0
indexNames = df[ (df[0] == 0) & (df[1] == 0)  & (df[2] == 0) & (df[3] == 0) & (df[4] == 0) & (df[5] == 0) & (df[6] == 0) & (df[7] == 0) & (df[8] == 0) & (df[9] == 0) & (df[10] == 0) & (df[11] == 0) & (df[12] == 0) & (df[13] == 0) & (df[14] == 0) & (df[15] == 0)].index

#odstrani vsaki dugo niclo
ind = list(indexNames)
del ind[::3]

#zbrisi izbrane vrstice iz df
df = df.drop(ind )


#dodaj vrstico napovedi CLASS
df["class"]=0


#resi na datoteko
df.to_csv('/home/niki/Desktop/tesi/data_registered/testnipodatkiFALSE/processed/random3m0.txt', index = False)
