import numpy as np
from cv2 import aruco
import matplotlib.pyplot as plt
import matplotlib as mpl
from PIL import Image
from matplotlib import cm


def main():
    aruco_dict = aruco.Dictionary_get(aruco.DICT_4X4_250)
    num = 50

# Generate 4 Boards
    for i in range(0,9):
        marker = aruco.drawMarker(aruco_dict, i, 700)
        marker = Image.fromarray(np.uint8(marker))
        img = Image.new('L', (int(marker.width+marker.width/2), int(marker.height+marker.height/2)))
        img.paste((255), [0,0,img.size[0],img.size[1]])
        img.paste(marker, (int(marker.width/4), int(marker.width/4)))

        dst = Image.new('L', (num*img.width, num*img.height))
        for x in range(0, num):
            for y in range(0, num):
                dst.paste(img, (x*img.width, y*img.height))

        dst = dst.resize((2048, 2048), Image.NEAREST)
        dst.save('ArucoBoard' + str(i) + '.png')


if __name__ == "__main__":
    main()