import numpy as np
import cv2
from pyimagesearch.shapedetector import ShapeDetector


cap = cv2.VideoCapture(0)

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    #TEMP VALUES
    #lower_blue = np.array([80,60,250])
    #upper_blue = np.array([100,250,255])
    
    lower_blue = np.array([30,50,100])
    upper_blue = np.array([130,255,255])

    # Threshold the HSV image to get only blue colors
    mask = cv2.inRange(hsv, lower_blue, upper_blue)
    # Bitwise-AND mask and original image
    res = cv2.bitwise_and(frame, frame, mask=mask)
   
    #Noise
    kernel = np.ones((5,5),np.uint8)
    erosion = cv2.erode(mask,kernel,iterations = 1)

    kernel = np.ones((10,10), np.uint8)
    dilation = cv2.dilate(erosion, kernel, iterations = 1)

    # Display the resulting frame
    cv2.imshow('res',res)
    cv2.imshow('Noise Reduction',dilation)    
    cv2.imshow('frame',frame)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
