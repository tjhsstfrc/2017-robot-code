import numpy as np
import cv2
import imutils
from imutils.video import WebcamVideoStream
import time
from networktables import NetworkTable as NT
import logging

logging.basicConfig(level=logging.DEBUG)


ip = 'roborio-3455-frc.local'

NT.setIPAddress('10.34.55.96')
NT.setClientMode()
NT.initialize()

table = NT.getTable("SmartDashboard")

#allow connection
time.sleep(3)

cap = WebcamVideoStream(src=0).start()

while(True):
    # Capture frame-by-frame
    frame = cap.read()
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    #TEMP VALUES
    lower_blue = np.array([125, 100, 100])# (B) 80 60 250 (G) 50 20 200
    upper_blue = np.array([150, 170, 255])# (B) 100 255 255 (G) 100 150 255

    
    # Threshold the HSV image to get only blue colors
    mask = cv2.inRange(hsv, lower_blue, upper_blue)
    # Bitwise-AND mask and original image
    cv2.imshow('mask', mask)   
    #Noise
    kernel = np.ones((5,5),np.uint8)
    erosion = cv2.erode(mask,kernel,iterations = 1)

    kernel = np.ones((10,10), np.uint8)
    dilation = cv2.dilate(erosion, kernel, iterations = 4)
    dilation = cv2.erode(dilation, kernel, iterations = 3)
    # Display the resulting frame
   
    cv2.imshow('hsv', hsv)    
    cv2.imshow('dilation',dilation)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    
    cnts = cv2.findContours(dilation.copy(), cv2.RETR_EXTERNAL,
    cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if imutils.is_cv2() else cnts[1]
    arr = []
    for c in cnts:
        arr.append((cv2.contourArea(c), c))
    try:
        arr = sorted(arr)
    except:
        i = 0
    cX, cY = 0, 0
    if(len(arr) > 0):
        c = (arr[-1::-1][0][1])
    
        # loop over the contours
        # compute the center of the contour, then detect the name of the
        # shape using only the contour
        M = cv2.moments(c)
        cX = int((M["m10"] / M["m00"]))
        cY = int((M["m01"] / M["m00"]))

        # multiply the contour (x, y)-coordinates by the resize ratio,
        # then draw the contours and the name of the shape on the image
        c = c.astype("int")
        cv2.drawContours(frame, [c], -1, (0, 255, 0), 2)
        cv2.circle(frame, (cX, cY), 5, (0,0,255), -1)
     # show the output image

    cv2.imshow("Image", frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    
    #Get Center of Imagee
    height, width, channels = frame.shape
    goalX = width/2.0
    goalY = height/2.0
    #Turning (Set x)
    if  goalX - cX > 5 or goalX - cX < -5:
        turning = True #send
        table.putBoolean('Turning', turning)
        error = goalX - cX
        pConst = 1.0/goalX
        turnSpeed = pConst*error #send
        table.putNumber('Turning Value', turnSpeed)
   
    #Back/Forth
    if  goalY - cY > 5 or goalY - cY < -5:
        turning = False #send
        table.putBoolean('Turning', turning)
        error = goalY - cY
        pConst = -1.0/goalY
        linearSpeed = pConst*error #send
        table.putNumber('Linear  Value', linearSpeed)
    
   
# When everything done, release the capture
cv2.destroyAllWindows()
cap.stop()
