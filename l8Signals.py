#!/usr/bin/env python

import RPi.GPIO as GPIO
import time
import requests
import l8
import json

GPIO.setmode(GPIO.BOARD)

#leds pins

led_off=26
led_on=8

#buttons pins
#butt=12
button0=3
button1=5
#button2=7
#button3=13
#button4=15 

#vars
en_juego=True;
color0=l8.Colour(0,0,255)
color1=l8.Colour(255,51,0)

l = l8.L8Bt("00:17:EC:4C:62:EE")

list_mov=[]
def encender_luz(led):
	GPIO.setup(led, GPIO.OUT)
	GPIO.output(led,1)

def apagar_luz(led):
	GPIO.setup(led, GPIO.OUT)
	GPIO.output(led,0)

def init_buttons():
	GPIO.setup(button0, GPIO.IN)
	GPIO.setup(button1, GPIO.IN)

def init_juego():
	time.sleep(1)
	init_buttons()
	apagar_luz(led_off)
	encender_luz(led_on)
	l.send_clear()
	l.back_light(l8.Colour(0,255,0))
	print ("ENCENDER")
	
def apagar_juego():
	GPIO.cleanup()
	encender_luz(led_off)
	l.send_clear()

def pintar(inicio, fin, color):
	l.set_light(inicio, fin, color)

def movimiento(inicio , final , boton, color):
	print("amos a probar:", inicio, final, boton)
	pintar(inicio,final,color)
	while GPIO.input(button0) and GPIO.input(button1) :
                pass
        if (GPIO.input(boton) == False) :
                list_mov.append(True)
                l.send_clear()
		l.back_light(l8.Colour(0,0,0))
		time.sleep(1)
		l.back_light(l8.Colour(0,255,0))
	        print ("OK")
	else:
		list_mov.append(False)
		print("NOT OK")
		l.back_light(l8.Colour(255,0,0))
                time.sleep(1)
                l.back_light(l8.Colour(0,255,0))

	apagar_juego()
	init_juego()


def jugar():
	movimiento(2,3,button1,color1)
	movimiento(6,2,button0,color0)
	movimiento(6,7,button1,color1)
	movimiento(0,3,button1,color1)
	movimiento(5,5,button0,color0)
	movimiento(7,0,button0,color0)
	movimiento(3,0,button1,color1)
	movimiento(3,4,button1,color1)
	movimiento(1,7,button0,color0)
	movimiento(0,4,button1,color1)
	print list_mov

def post_result():
	data={"user":2,"gameType":"Basic","att1":list_mov[0],"att1_time":0.8,"att2":list_mov[1],"att2_time":0.3,"att3":list_mov[2],"att3_time":1.8,"att4":list_mov[3],"att4_time":0.5,"att5":list_mov[4],"att5_time":2.8,"att6":list_mov[5],"att6_time":0.2,"att7":list_mov[6],"att7_time":0.9,"att8":list_mov[7],"att8_time":3.8,"att9":list_mov[8],"att9_time":0.8,"att10":list_mov[9],"att10_time":0.7}
	print data
	url="http://192.168.1.12:8000/api/game/"
	h={'Content-type': 'application/json', 'Accept': 'application/json'}
	r=requests.post(url, data=json.dumps(data), headers=h)
	print r
encender_luz(led_off)

init_juego()

jugar()
post_result()
apagar_juego()


#testing:



#encender_luz(led_on)
#time.sleep(5)
#apagar_luz(led_on)



#limpieza final :)



