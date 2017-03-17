import numpy as np
from sklearn.metrics.pairwise import euclidean_distances
from sklearn.decomposition import PCA
import pylab as pl
import matplotlib.cm as cm

#opened file and read lines into genes array
file = open('new_dataset_1.txt' , 'r')
genes=file.readlines();

#cho: 0.98 4
#iyer: 1.03 4
#dataset_1: 0.4  4
#dataset_2: 1   3

eps=4
minpoints=3


#got the number of elements in genes
genecount=genes.__len__()

#splitting first row to get number of expressions
expcount=len(genes[0].split('\t'))-2

print 'genecount: ',genecount, ' expcount: ', expcount

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

geneexpdist=euclidean_distances(geneexp[0:genecount,2:expcount+2], geneexp[0:genecount,2:expcount+2])
geneexpdist=np.append(geneexpdist,np.zeros([len(geneexpdist),1]),1)
geneexpdist=np.append(geneexpdist,np.zeros([len(geneexpdist),1]),1)
geneexpdist=np.append(geneexpdist,np.zeros([len(geneexpdist),1]),1)

#Expand cluster function
def assigncluster (l, clusternum):
    global geneexpdist
    global genecount
    global eps
    while (len(l)>0):
        l1=[]
        datapoint=l.pop()
        for j in range (0, genecount):
            if(geneexpdist[datapoint][j]<=eps):
                l1.append(j)
        if(len(l1)>=minpoints):
            for l2 in l1:
                if(geneexpdist[l2][genecount+1]==0):
                    l.append(l2)
                    geneexpdist[l2][genecount+1]=1
                    geneexpdist[l2][genecount]=clusternum

    return



clusternumber=1
x=0

#finding core points and assigning clusternumber and calling expand cluster function
while(x<clusternumber):
    i=0
    while(i<genecount):
        l=[]
        count=0
        if(geneexpdist[i][genecount]==0):
            for j in range (0, genecount):
                if(geneexpdist[i][j]<=eps):
                    l.append(j)
            #if (len(l)<2):
            #    geneexpdist[i][genecount]=-1
            #    geneexpdist[i][genecount+1]=1
            if ((len(l)>=minpoints)):
                geneexpdist[i][genecount]=clusternumber
                for l1 in l:
                    geneexpdist[l1][genecount+1]=1
                    geneexpdist[l1][genecount]=clusternumber
                assigncluster(l,clusternumber)
                clusternumber=clusternumber+1
        i=i+1
    x=x+1

#assigning the new cluster number to each datapoint
clustercount=np.zeros(shape=(clusternumber, 1))

for i in range (0, genecount):
    geneexp[i][expcount+2]=geneexpdist[i][genecount]
    clustercount[geneexpdist[i][genecount]]=clustercount[geneexpdist[i][genecount]]+1

#giving count of data points in each cluster
print 'total clusters: ', clusternumber
for i in range (0, clusternumber):
    if (clustercount[i]>1):
        print 'data in cluster ',i,' is ', clustercount[i]


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

rand= float(countmatch)/(float(genecount)*float(genecount))
print ('rand: '), rand


#PCA for new data set
target=geneexp[:,[expcount+2]] #new cluster number
#target=geneexp[:,[1]] 

pca=PCA(n_components=2)
pca.fit(geneexp[:,2:expcount+2])
X=pca.transform(geneexp[:,2:expcount+2])

x = np.arange(10)
ys = [i+x+(i*x)**4 for i in range(10)]
colors = cm.rainbow(np.linspace(0, 1, len(ys)))

for y, c in zip(ys, colors):
    pl.scatter(X[:,0],X[:,1], c=target, color=c, edgecolors='none', s=20)
pl.title('Density Based Clustering')
pl.show()
