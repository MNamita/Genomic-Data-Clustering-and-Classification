import numpy as np
from sklearn.metrics.pairwise import euclidean_distances
from operator import itemgetter
import operator
from sklearn import preprocessing
import math
import itertools
#dataset2 best at 14
#function to check for numeric values
def is_num(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

#open given dataset for reading the features           
file=open('project3_dataset2.txt' ,'r')
features=file.readlines()

k=14
kfolds=10
#splitting on tab to get the features without the labels
samplecount=features.__len__()
attributes=len(features[0].split('\t'))-1

print 'samples:', samplecount,'Attributes;', attributes

#creating matrix for storing the features
samples=np.zeros((samplecount, attributes),dtype=np.float64 )
classes=np.zeros((samplecount,1))

nominalval={}
nominalCount = 0

#insertion of data
for i in range (0,samplecount):
    attri=[]
    attri= features[i].split('\t')
    attri=np.array(attri)
    for j in range (0, attributes):
        if(is_num(attri[j])):
            samples[i][j]=attri[j]
        else:
            if(attri[j] in nominalval):
                samples[i][j]=nominalval[attri[j]]
            else:
                nominalval[attri[j]]=nominalCount
                samples[i][j]=nominalval[attri[j]]
                nominalCount=nominalCount+1
        classes[i]=attri[attributes]

'''
testcount=int(samplecount/10) 
testsample=samples[-testcount:,:]
testclasses=classes[-testcount:,:]

samplecount=samplecount-testcount

'''
crossval = int(samplecount)/kfolds
for i in range (0,kfolds):
    testsample=samples[i*crossval:][:crossval]
    train=samples[:i*crossval]+ samples[(i+1)*crossval:]
print samples.__len__()
print classes.__len__()



#scaling the given dataset 
min_max_scaler = preprocessing.MinMaxScaler()
train_minmax = min_max_scaler.fit_transform(train)
test_minmax = min_max_scaler.fit_transform(testsample)

print 'train',train
print 'Test',testsample
print 'Normalised train',train_minmax
print 'Normalised test',test_minmax
#print 'Classes', classes

distance = euclidean_distances(test_minmax,train_minmax)
print 'distances',distance

distance=distance.transpose(1,0)
print 'reshaped distances', distance


def nearest(k):
    print k
    matchcount=0
    for i in  range(len(test_minmax)):
        enumerated=[]
        count1=0
        count2=0
        newK=k
        #print([sorted(enumerate(distance[i]),key=lambda x:x[1])])
        #enumerated=([sorted(enumerate(distance[i]),key=operator.itemgetter(1))[0][0:k]])
        #print enumerated
        enumerated=(zip(*sorted(enumerate(distance[i]), key=operator.itemgetter(1)))[0][0:k])
        #print enumerated
        tie=True
        
        while(tie):
            for j in range(0,newK):
                if(classes[enumerated[j]] == 1):
                    count1=count1+1
                else:
                    count2=count2+1
            if (count1>count2):
                print '1',classes[i]
                if(classes[i]==1):
                    matchcount+=1
                tie=False
            elif(count2>count1):
                print '0',classes[i]
                if(classes[i]==0):
                    matchcount+=1
                tie=False
            else:
                newK-=1
    print matchcount
    return  
    
nearest(k)    