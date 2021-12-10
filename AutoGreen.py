import RPi.GPIO as GPIO
import pyrebase
import time
import board
import busio as io
import adafruit_tsl2591
from adafruit_seesaw.seesaw import Seesaw

# Initialize i2c interface
i2c = board.I2C()
ss = Seesaw(i2c, addr=0x36)
ts = adafruit_tsl2591.TSL2591(i2c)
# Initialize WaterPump Gpio
WATERPUMP_PIN = 17
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(WATERPUMP_PIN,GPIO.OUT)
# Initialize Grow Light Gpios
GROWLIGHT_PIN = 27
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(GROWLIGHT_PIN,GPIO.OUT)
# Initialize firebase
config = {     
  "apiKey": "3ioPphwPpMB4hasTElP0aM9KR8kEWhZD39GduQbR", # AIzaSyBTsevL9WXuUSVtVYHyz2l8txQ0gNtrCKk
  "authDomain": "automatedgreenhouse-edb75.firebaseapp.com",
  "databaseURL": "https://automatedgreenhouse-edb75-default-rtdb.firebaseio.com/",
  "storageBucket": "automatedgreenhouse-edb75.appspot.com"
}
firebase = pyrebase.initialize_app(config)
database = firebase.database()
# Notify that pi is currently sending data to Firebase
print("Send Data to Firebase Using Raspberry Pi")
print("----------------------------------------")
print()

#Watering Variables
isWaiting = False          #Indicates a wait before allowing to water again
isWatering = False         #Indicates if currently executing watering procedure
pumpOn = False             #Indicates if pump is currently actuated
WATER_INTERVAL = 12         #Constant for how long to run the watering procedure
waterIterator = 0          #Iterator for "WATER_INTERVAL"
actuatePump = False        #Tells pump to actuate
#Watering Variables for wait time between waterings
startWaitTime = 0 #
WAIT_TIME = 900   #

#Database Variables
CYCLE_SLEEP_TIME = 2 #Constant for duration in seconds the program sleeps each cycle
RW_INTERVAL = 2      #Constant for how often the program will read/write to/from database
rwIterator = 0       #Iterator for 'RW_INTERVAL'

#Sensor Variables
moisture = 0       #
temp = 0           # 
lux = 0            #computed light lux value
visible = 0        #visible light level... the higher the number the more visible light (32-bit unsigned value with no units)
infrared = 0       #infrared light level... the higher the number the more infrared light (16-bit unsigned value with no units)
full_spectrum = 0  #visible & infrared light level... The higher the number the more full spectrum light (32-bit value with no units)
raw_luminosity = 0 #2-tuple of raw sensor visible+IR and IR only light levels... 16-bit value with no units where the higher the value the more light.
light = False
maxLightDuration = 0
lightOnStart = 0

"""

"""
def readSensors():
    global moisture
    global temp
    global ts
    global lux  
    global visible    
    global infrared 
    global full_spectrum 
    global raw_luminosity 
    moisture = ss.moisture_read()
    temp = ss.get_temp()
    #Light Values
    #lux = sensor.lux
    visible = sensor.visible
    #infrared = sensor.infrared
    #full_spectrum = sensor.full_spectrum
    #raw_luminosity = sensor.raw_luminosity
    #Print moisture and temp to console
    print("temp: " + str(temp) + " moisture: " + str(moisture))
    #print("lux: " + str(lux))
    print("visible: " + str(visible))
    #print("infrared: " + str(infrared))
    #print("full_spectrum: " + str(full_spectrum))
    #print("raw_luminosity: " + str(raw_luminosity))

"""

"""
def rwDatabase():
    global rwIterator
    global actuatePump
    if rwIterator >= RW_INTERVAL:
        #Send moisture and temp values to database
        database.child("Greenhouse").child("moisture").set(moisture)
        database.child("Greenhouse").child("temperature").set(temp)
        database.child("Greenhouse").child("sunlight").set(visible)
        #Get water boolean from database
        actuatePump = database.child("Greenhouse").child("water").get().val()
        #Get light boolean from database
        light = database.child("Greenhouse").child("light").get().val()
        rwIterator = 0
    else: 
        rwIterator+=1

"""

"""
def wateringProcedure():
    global isWatering
    global waterIterator
    global isWaiting
    global startWaitTime
    global actuatePump
    if actuatePump and (isWatering == False):
        database.child("Greenhouse").child("has_watered").set(True)
        isWatering = True
        beginWatering()
    elif isWatering == True:
        if waterIterator < WATER_INTERVAL:
            beginWatering()
        else:
            GPIO.output(WATERPUMP_PIN, GPIO.LOW)
            waterIterator = 0
            isWatering = False
            actuatePump = False
            isWaiting = True
            startWaitTime = time.time()
            database.child("Greenhouse").child("water").set(False)
            waitForPush = database.child("Greenhouse").child("water").get().val()
            while waitForPush == True:
                waitForPush = database.child("Greenhouse").child("water").get().val()

def beginWatering():
    global waterIterator
    if pumpOn == False or (waterIterator % 2 == 0):
        runPump()
    else: 
        stopPump()
    waterIterator += 1

def runPump():
    global pumpOn
    GPIO.output(WATERPUMP_PIN, GPIO.HIGH)
    pumpOn = True

def stopPump():
    global pumpOn
    GPIO.output(WATERPUMP_PIN, GPIO.LOW)
    pumpOn = False

def checkWait():
    waitedTime = abs(time.time() - startWaitTime)
    if waitedTime >= WAIT_TIME:
        database.child("Greenhouse").child("has_watered").set(False)

def lightFunc():
    global light
    global lightOn
    global lightOnStart
    if light: 
        GPIO.output(GROWLIGHT_PIN, GPIO.HIGH)
        if lightOn == False:
            lightOnStart = time.time()
            lightOn = True
        elif (time.time() - lightOnStart) > maxLightDuration:
            database.child("Greenhouse").child("light").set(False)
    else: 
        GPIO.output(GROWLIGHT_PIN, GPIO.LOW)
        lightOn = False

# Run program
while True:
    #Read sensor data
    readSensors()
    #Read and Write data to/from the database
    rwDatabase()
    #Determine watering behavior
    wateringProcedure()
    if (isWaiting):
        checkWait()
    #Determind grow light behavior
    lightFunc()
    #Put program to sleep
    time.sleep(CYCLE_SLEEP_TIME)

GPIO.cleanup()