import numpy as np
from sklearn.metrics.pairwise import euclidean_distances
from sklearn.decomposition import PCA
import pylab as pl
import matplotlib.cm as cm

#from sklearn.metrics.cluster import adjusted_rand_score

#dataset2: level 2
#dataset1: level 3
#cho: 10
#iyer: 5

#opened file and read lines into genes array
file = open('new_dataset_2.txt' , 'r')
genes=file.readlines();

level=5

#got the number of elements in genes
genecount=genes.__len__()

#splitting first row to get number of expressions
expcount=len(genes[0].split('\t'))-2

print 'size of data ', genecount, ', ', expcount

#creating a matrix for storing gene and exp values
geneexp=np.zeros((genecount, expcount+3),dtype=np.float64 )

#print geneexp.size

#inserted data into geneexp by splitting data in genes
for i in range (0, genecount):
    exps=[]
    exps=genes[i].split('\t')
    exps=np.array(exps)
    for j in range (0, expcount+2):
        geneexp[i][j]=exps[j]

#calculated euclidean distance using the function for data of size 386*386
geneexpdist= euclidean_distances(geneexp[0:genecount,2:expcount+2], geneexp[0:genecount,2:expcount+2])
geneexpdist=np.append(geneexpdist,np.ones([len(geneexpdist),1]),1)
geneexpdist=np.append(geneexpdist,np.ones([len(geneexpdist),1]),1)

#giving geneID to each row
for j in range (0, genecount):
    geneexpdist[j][genecount]=j+1
    geneexpdist[j][genecount+1]=-1


clustercount=genecount+1
clusters={}
genecountcopy=genecount
isnotempty='true'


#forming cluster for each level i.e. Dendrogram
def mergecluster(geneexpdistance):
    global clustercount
    global clusters
    global genecountcopy
    global isnotempty

    minval=1000
    mink=-1
    minl=-1
    k=0
    l=0
    for k in range (0, genecountcopy):
        for l in range (0, genecountcopy):
            if(k!=l):
                if(minval>geneexpdistance[k][l]):
                    minval=geneexpdistance[k][l]
                    mink=k
                    minl=l

    if(mink==-1 or minl==-1):
        isnotempty='false'
        return

    l1=[]
    l1.append(geneexpdistance[mink][genecountcopy])
    l1.append(geneexpdistance[minl][genecountcopy])

    clusters[clustercount]=l1
    geneexpdistance[mink][genecountcopy]=clustercount
    clustercount=clustercount+1

    for m in range (0, genecountcopy):
        if(m!=mink or m!=minl):
            geneexpdistance[mink][m]=min(geneexpdistance[mink][m],geneexpdistance[minl][m])
            geneexpdistance[m][mink]=geneexpdistance[mink][m]

    geneexpdistance=np.delete(geneexpdistance,minl,0)
    geneexpdistance=np.delete(geneexpdistance,minl,1)
    genecountcopy=genecountcopy-1

    mergecluster(geneexpdistance)

    return

mergecluster(geneexpdist)
#print (clusters)

clusterinfo=np.zeros(shape=(clustercount-level+2,1))
clusterinfo[0]=1
startlevel=clustercount-level
finalclusters={}
clusternumber=1

#forming clusters for given level
while (startlevel>0):
    l2=[]
    l3=[]
    l2=clusters[startlevel]
    clusterinfo[startlevel]=1
    while (len(l2)>0):
        l4=l2.pop()
        clusterinfo[l4]=1
        if(l4<=genecount):
            l3.append(l4)
        else:
            l5=[]
            l5=clusters[l4]
            for j in range (0, len(l5)):
                l2.append(l5[j])
    finalclusters[clusternumber]=l3
    clusternumber=clusternumber+1

    while(clusterinfo[startlevel]==1):
        if (startlevel>genecount+1):
            startlevel=startlevel-1
        else:
            startlevel=0
            break

print finalclusters

#final cluster naming for each data point
clustername=1

for key in finalclusters:
    l= finalclusters[key]
    for j in range(0, len(l)):
        geneexp[l[j]-1][expcount+2]=clustername
    clustername=clustername+1

clustercount=np.zeros(shape=(clustername,1))
for i in range (0, genecount):
    clustercount[geneexp[i][expcount+2]]= clustercount[geneexp[i][expcount+2]]+1

print 'total cluster number is ', clustername
for i in range (0, clustername):
    print 'data in cluster ',i, ' is ', clustercount[i]


#Rand Index calculation
jcmatrixgt=np.zeros(shape=(genecount,genecount), dtype=int)
jcmatrixnc=np.zeros(shape=(genecount,genecount), dtype=int)
countmatch=0

for i in range (0, genecount):
    for j in range (i, genecount):
        if(geneexp[i][1]==geneexp[j][1]):
            jcmatrixgt[i][j]=1
            jcmatrixgt[j][i]=1
        if(geneexp[i][expcount+2]==geneexp[j][expcount+2]):
            jcmatrixnc[i][j]=1
            jcmatrixnc[j][i]=1
        if(jcmatrixgt[i][j]==jcmatrixnc[i][j]):
            if(i==j):
                countmatch=countmatch+1
            else:
                countmatch=countmatch+2

print
rand= float(countmatch)/(float(genecount)*float(genecount))
print 'rand: ', rand


target=geneexp[:,[expcount+2]]#expcount+2
#target=geneexp[:,[1]] 
pca=PCA(n_components=2)
pca.fit(geneexp[:,2:expcount+2])
X=pca.transform(geneexp[:,2:expcount+2])

x = np.arange(10)
ys = [i+x+(i*x)**3 for i in range(10)]
colors = cm.rainbow(np.linspace(0, 1, len(ys)))

for y, c in zip(ys, colors):
    pl.scatter(X[:,0],X[:,1], c=target, color=c, edgecolors='none', s=20)

#pl.axis([-60, 15, -10, 20])

pl.show()
