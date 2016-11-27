import cv2
import imutils
import numpy as np

img = cv2.imread('./images/skew.jpeg',-1)

frame = imutils.resize(img, width=600)

hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    # define range of blue color in HSV
lower_blue = np.array([30,50,100])
upper_blue = np.array([130,255,255])
    # Threshold the HSV image to get only blue colors
mask = cv2.inRange(hsv, lower_blue, upper_blue)

    # Bitwise-AND mask and original image
res = cv2.bitwise_and(frame,frame, mask= mask)

cv2.imshow('frame',frame)
cv2.imshow('mask',mask)
cv2.imshow('res',res)
k = cv2.waitKey(5000000)

cv2.destroyAllWindows()
