#install.packages("remotes")
#remotes::install_github("coleoguy/evobir")

#detach("package:plyr", unload=TRUE) 
library(remotes)
library(slider)
library(ggeffects)
library(gridExtra)
library(grid)
library(readxl)
library(ggplot2)
library(dplyr)
#library(Rmisc)
library(reshape2)
library(knitr)


myData <- read.delim(file='../../data_firstSample/50hz/padec1',header=FALSE,sep=',')

myData$V17 = NULL #delete last row

myData <- myData %>%
  mutate(n=1:n())


myPlot = c()

for(colu in colnames(myData)){
  if(colu!="n"){
  p <- ggplot(data = myData, aes_string(x="n", y = colu)) +
    geom_line()
  myPlot <- c(myPlot,list(p))
  }
}




do.call(grid.arrange,c(myPlot,nrow=4))

myPlot[5]

SlidingWindow("mean",myData$V5,50,5)


