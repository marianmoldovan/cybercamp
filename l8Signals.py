#!/usr/bin/env python

import time
import requests
import l8
import json


#vars
color_verde=l8.Colour(0,255,0)
color_rojo=l8.Colour(255,0,0)
color_blanco=l8.Colour(255,255,255)


#l8 device
l = l8.L8Bt("00:17:EC:4C:62:EE")

#symbol
list_d=[]

list_d.append((0,3))
list_d.append((2,3))
list_d.append((4,3))
list_d.append((1,3))
list_d.append((3,3))
list_d.append((5,3))
list_d.append((7,3))
list_d.append((0,4))
list_d.append((2,4))
list_d.append((4,4))
list_d.append((1,4))
list_d.append((3,4))
list_d.append((5,4))
list_d.append((7,4))




list_ok=[]

list_ok.append((3,3))
list_ok.append((3,4))
list_ok.append((4,2))
list_ok.append((4,3))
list_ok.append((2,0))
list_ok.append((3,0))
list_ok.append((3,1))
list_ok.append((4,1))
list_ok.append((0,6))
list_ok.append((0,7))
list_ok.append((1,5))
list_ok.append((1,6))
list_ok.append((2,4))
list_ok.append((2,5))
list_ok.append((4,2))
list_ok.append((5,1))
list_ok.append((5,2))



list_x=[]

list_x.append((0,0))
list_x.append((1,0))
list_x.append((1,1))
list_x.append((2,1))
list_x.append((2,2))
list_x.append((3,2))
list_x.append((3,3))
list_x.append((3,4))
list_x.append((4,3))
list_x.append((4,4))
list_x.append((5,4))
list_x.append((5,5))
list_x.append((6,5))
list_x.append((6,6))
list_x.append((7,6))
list_x.append((7,7))
list_x.append((0,6))
list_x.append((0,7))
list_x.append((1,5))
list_x.append((1,6))
list_x.append((2,4))
list_x.append((2,5))
list_x.append((4,2))
list_x.append((5,1))
list_x.append((5,2))
list_x.append((6,0))
list_x.append((6,1))
list_x.append((7,0))
list_x.append((4,4))


def initial():
	l.send_clear()
	l.back_light(color_verde)

	print ("ENCENDER")
	
def finalize():
	l.send_clear()
	l.back_light(color_blanco)

def paint_x():
	l.send_clear()
	l.back_light(color_rojo)
	for i in list_x:		
		l.set_light(i[0],i[1],color_rojo)
	
def paint_ok():
	l.send_clear()
	l.back_light(color_verde)
	for i in list_ok:		
		l.set_light(i[0],i[1],color_verde)

def paint_d():
	l.send_clear()
	l.back_light(color_rojo)
	for i in list_d:		
		l.set_light(i[0],i[1],color_rojo)

def post_result():
	data={"user":2,"gameType":"Basic","att1":list_mov[0],"att1_time":0.8,"att2":list_mov[1],"att2_time":0.3,"att3":list_mov[2],"att3_time":1.8,"att4":list_mov[3],"att4_time":0.5,"att5":list_mov[4],"att5_time":2.8,"att6":list_mov[5],"att6_time":0.2,"att7":list_mov[6],"att7_time":0.9,"att8":list_mov[7],"att8_time":3.8,"att9":list_mov[8],"att9_time":0.8,"att10":list_mov[9],"att10_time":0.7}
	print data
	url="http://192.168.1.12:8000/api/game/"
	h={'Content-type': 'application/json', 'Accept': 'application/json'}
	r=requests.post(url, data=json.dumps(data), headers=h)
	print r


#initial()
#paint_x()
#time.sleep(2)

#paint_ok()
#time.sleep(2)

#paint_d()
#time.sleep(2)


#finalize()
