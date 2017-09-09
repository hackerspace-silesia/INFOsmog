import numpy as np
import plotCounty
import Data



def heatGeneratingLoop():
    while(True):
        a= Data.getData()
        #todo zrobic converter ze wsp geograficznych do komorki tablicy heatmapy w zaleznosci do wojewodztwa

        plotCounty.plot(a,"Slaskie_mapa_administracyjna.png")


if __name__ == '__main__':
   heatGeneratingLoop()