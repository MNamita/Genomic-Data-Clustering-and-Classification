import numpy as np
from sklearn.metrics.pairwise import euclidean_distances

from sklearn.decomposition import PCA
import pylab as pl
import matplotlib.cm as cm
import matplotlib.pyplot as pp

#pp.plot(a,len(a) * [1], "x")
#pp.show()
#opened file and read lines into genes array
file = open('cho.txt' , 'r')
genes=file.readlines();


#got the number of elements in genes
genecount=genes.__len__()

#splitting first row to get number of expressions
expcount=len(genes[0].split('\t'))-2

print expcount,  genecount

#creating a matrix for storing gene and exp values
geneexp=np.zeros((genecount, expcount+3),dtype=np.float64 )

#print geneexp.size

#inserted data into geneexp by splitting data in genes
for i in range (0, genecount):
    exps=[]
    exps=genes[i].split('\t')
    exps=np.array(exps)
    #print exps
    for j in range (0, expcount+2):
        geneexp[i][j]=exps[j]

#Read output file written by JAVA code
file = open('cho_hadoop.txt' , 'r')
genes=file.readlines();
print genes[0]

genelabel=[]
genelabel=genes[0].split('\t')
genelabel=np.array(genelabel)
print genelabel
a =np.zeros((genecount,1),dtype=np.float64)
for i in range(0,genecount):
    a[i][0]=genelabel[i]

print a

for i in range (0, genecount):
    print geneexp[i][1]

#PCA
target= a
pca=PCA(n_components=2)
pca.fit(geneexp[:,2:expcount+2])
X=pca.transform(geneexp[:,2:expcount+2])

x = np.arange(10)
ys = [i+x+(i*x)**3 for i in range(10)]
colors = cm.rainbow(np.linspace(0, 1, len(ys)))

for y, c in zip(ys, colors):
    pl.scatter(X[:,0],X[:,1], c=target, color=c, edgecolors='none', s=20)

#pl.title('KMeans Clustering Hadoop')
pl.show()
