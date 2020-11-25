#!/usr/bin/python3 -u

import os
import sys
import sched,time
import paho.mqtt.client as mqtt
import mqttauth
import threading
from time import sleep
from PyMata.pymata import PyMata
mcu = None
SCH_PIR = 1
shock_active = 0
while mcu is None:
	try:
		mcu = PyMata(port_id="/dev/ttyATH0",baud_rate=115200,bluetooth=False)
	except:
		mcu = None

motor_control_sched = None
taser_control_sched = None
calibration = False
def gmcodeinterputer(gmcode):
	global motor_control_sched
	global taser_control_sched
	global calibration
	command = gmcode.split(' ')[0].upper()
	temp = ""
	chindex = ''
	hashtable={}
	firstrun = 1
	i = 0
	parameter = ""
	try:
		parameter = gmcode.split(' ')[1].upper()
		for ch in parameter:
			i+=1
			if(ch.isalpha() and firstrun!=1):
				hashtable[chindex]=temp
				chindex=ch
				temp = ""
			elif(ch.isalpha() and firstrun == 1):
				chindex = ch
				firstrun = 0
			elif(i == len(parameter)):
				temp+=ch
				hashtable[chindex]=temp
			else:
				temp += ch
	except:
		hashtable = {}
	if(command=="G0" or command=="G1"):
		output("Linear Movement Command is not supported on this device!")
	elif(command == "G2"):	
		#sleep(float(hashtable['T'])/1000.0)
		if(motor_control_sched != None):
			try:
				scheduler.cancel(motor_control_sched)
				motor_control_sched = None
			except:
				output("No BackTrack, OK!")
		mcu.digital_write(12,1)
		sleep(0.1)
		mcu.analog_write(3,int(float(hashtable['P'])*2.25))
		output("Rotating Clockwise with the speed of "+hashtable['P']+" for "+hashtable['T']+" milliseconds.")
		motor_control_sched = scheduler.enter(float(hashtable['T'])/1000.0,SCH_PIR,motor_shutdown)
	elif(command == "G3"):
		if(motor_control_sched != None):
			try:
				scheduler.cancel(motor_control_sched)
				motor_control_sched = None
			except:
				output("No BackTrack, OK!")
		mcu.digital_write(12,0)
		sleep(0.1)
		mcu.analog_write(3,int(float(hashtable['P'])*2.25))
		output("Rotating Counter-Clockwise with the speed of "+hashtable['P']+" for "+hashtable['T']+" milliseconds.")
		#sleep(float(hashtable['T'])/1000.0)
		motor_control_sched = scheduler.enter(float(hashtable['T'])/1000.0,SCH_PIR,motor_shutdown)
	elif(command == "G28"):
		output("Running Automated Homing Sequence.")

	elif(command == "M3"):
		output("Tasering the player with a duty cycle of "+hashtable['P']+" percent.")
		mcu.analog_write(11,int(float(hashtable['P'])/2))
	elif(command == "M4"):
		output("Tasering the player with a duty cycle of "+hashtable['P']+" percent for "+hashtable['T']+"milliseconds.")
		mcu.analog_write(11,int(float(hashtable['P'])/2))
		if(taser_control_sched != None):
			try:
				scheduler.cancel(taser_control_sched)
				taser_control_sched = None
			except:
				output("No BackTrack, OK!")
		taser_control_sched = scheduler.enter(float(hashtable['T'])/1000.0,SCH_PIR,taser_shutdown)
	elif(command == "M5"):
		mcu.analog_write(11,0)
		try:
			scheduler.cancel(taser_control_sched)
			output("Taser Disabled")
		except ValueError:
			output("Tazer Schduler is not running!")
	elif(command == "M17"):
		output("Enabling Holding Torque.")
		mcu.analog_write(9,255)	
	elif(command == "M18"):
		output("Disabling Holding Torque.")
		mcu.analog_write(9,0)
	elif(command == "M112"):
		output("EMERGENCY STOP!")
	elif(command == "M119"):
		output("Polling Endstops.")
		output("Impaler: A"+ str(mcu.analog_read(0)))
		output("BTN: A"+ str(mcu.analog_read(2)))
		output("RTABLE: "+str(mcu.get_digital_response_table()))
		output("Button: "+str(mcu.digital_read(5)))
	elif(command == "M220"):
		shock_user()
		output("Running Tazer Routine")
	else:
		output("I don't know what the hell this is.")
def on_connect(client, userdata, flags, rc):
    output("Connected with result code "+str(rc))
    client.subscribe("/osutazer/lab")
def on_message(client, userdata, msg):
    output("Recieved G-Code: "+str(msg.payload).lstrip('b').lstrip("'").rstrip("'"))
    gmcodeinterputer(str(msg.payload).lstrip('b').lstrip("'").rstrip("'"))

def motor_shutdown():
	mcu.analog_write(3,0);
	motor_control_sched = None
	output("Shutting Motor Down")
def taser_shutdown():
	mcu.analog_write(11,0)
	taser_control_sched = None
	output("Shutting Taser Down")
def output(message: str):
	print(message)
	client.publish("/osutazer/lab/response",payload=message,qos=0,retain=False)
def shock_user():
	global shock_active
	if (shock_active == 0):
		shock_active = 1
		sleep(0.1)
		mcu.digital_write(12,1) #Set DIR CW
		sleep(0.1)
		mcu.analog_write(3,200) #Set Speed 120
		sleep(0.25)
		mcu.analog_write(3,0) #Set Speed 0
		sleep(0.1)
		mcu.analog_write(11,25) #Enable Taser
		sleep(0.1)
		mcu.analog_write(11,0) #Disable Taser
		sleep(0.05)
		mcu.digital_write(12,0) #Set DIR CCW
		sleep(0.05)
		mcu.analog_write(3,1023) #Set Speed 200
		sleep(0.5)
		mcu.analog_write(3,0) #Set Speed 0
		shock_active = 0
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.username_pw_set(mqttauth.username,password=mqttauth.password)
client.connect(mqttauth.server, 1883, 60)
mcu.set_sampling_interval(500)
mcu.set_pin_mode(3,mcu.PWM,mcu.DIGITAL) 
mcu.set_pin_mode(5,mcu.PULLUP,mcu.DIGITAL) #BUTTON
mcu.set_pin_mode(6,mcu.PWM,mcu.DIGITAL) #BUZZER
mcu.set_pin_mode(7,mcu.OUTPUT,mcu.DIGITAL) #LED
mcu.set_pin_mode(8,mcu.OUTPUT,mcu.DIGITAL)
mcu.set_pin_mode(9,mcu.PWM,mcu.DIGITAL)
mcu.set_pin_mode(11,mcu.PWM,mcu.DIGITAL)
mcu.set_pin_mode(12,mcu.OUTPUT,mcu.DIGITAL)
mcu.set_pin_mode(13,mcu.OUTPUT,mcu.DIGITAL)
mcu.set_pin_mode(0,mcu.INPUT,mcu.ANALOG)
mcu.set_pin_mode(1,mcu.INPUT,mcu.ANALOG)
mcu.set_pin_mode(2,mcu.INPUT,mcu.ANALOG)
mcu.enable_analog_reporting(0)
mcu.enable_analog_reporting(1)
#mcu.enable_analog_reporting(2)
mcu.enable_digital_reporting(5)
mcu.digital_write(13,1)

#For 12V 1.5A DC SPS
#Voltage Drop for PWM Duty Cycle 150/255: 12V -> 7.6V
#Voltage Drop for PWM Duty Cycle 100/255: 12V -> 8.9V
#Voltage Drop for PWM Duty Cycle 50/255: 12V -> 11.3V
#mcu.analog_write(11,10)
mcu.analog_write(6,255)
time.sleep(1)
mcu.analog_write(6,0)
scheduler = sched.scheduler(time.time,time.sleep)
print("Impaler: A"+ str(mcu.analog_read(0)))
print("Initialization Completed!")

while True:
	scheduler.run(blocking=False)
	print("BTN: "+str(mcu.digital_read(5)))
	while(shock_active!=1):
		client.loop();