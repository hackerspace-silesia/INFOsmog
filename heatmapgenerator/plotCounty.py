import numpy as np
import matplotlib.pyplot as plt
from PIL import Image, ImageOps
def plot(data, pngPath):
    #tworzenie heatmapy bicubic

    fig=plt.imshow(data, cmap='hot', interpolation='bicubic', alpha=0.9,)

    plt.axis('off')
    fig.axes.get_xaxis().set_visible(False)
    fig.axes.get_yaxis().set_visible(False)
    #zapisanie tej heatmapy
    plt.savefig("heatmap.png",bbox_inches='tight',
                pad_inches = 0, transparent=True)

    #tworzenie maski invertwoanie jej zeby bylo biala poza ksztaltem i ustawienie jej na binarna
    mask=Image.open(pngPath).convert('L')
    mask=ImageOps.invert(mask)
    mask= mask.point(lambda x: 0 if x==0 else 255, '1')

    #wszytanie powiatow i heatmapy do pila
    countyImg= Image.open(pngPath)
    heatmapImg= Image.open("heatmap.png")

    #zcropowanie maski do odpowiedniego rozmiaru
    mask.thumbnail(heatmapImg.size)
    heatmapImg=ImageOps.fit(heatmapImg, mask.size,centering=(0.5,0.5))
    heatmapImg.putalpha(mask)


    #ustawienie powiatow na rozmiar heatmapy
    countyImg.thumbnail(heatmapImg.size)
    #wklejenie hitmapy na powiaty
    countyImg.paste(heatmapImg,(0,0),heatmapImg)
    countyImg.save("result.png")
    return