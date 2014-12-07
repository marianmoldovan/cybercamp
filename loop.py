import blescan
import sys
import bluetooth._bluetooth as bluez
import time
import pickledb
import commands
import requests

from sensor import ZWay
from flask.ext.sqlalchemy import SQLAlchemy
from flask import Flask, jsonify
from server import Beacon


#Sqlite acces
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///temp/test.db'
db = SQLAlchemy(app)

#Beacon init
dev_id = 0
try:
	sock = bluez.hci_open_dev(dev_id)
	print "ble thread started"
except:
	print "error accessing bluetooth device..."
	sys.exit(1)
blescan.hci_le_set_scan_parameters(sock)
blescan.hci_enable_le_scan(sock)

#ZWave sensors init
pdb = pickledb.load('movements.db', False)
z = ZWay()

#sys.path.append('/home/pi/workspace/cybercamp')
#import sensor

def sensor():
    return z.getMovement()


def mashup(items):
	item = {}
	if(len(items) > 0):
		item['uuid'] = items[0]['uuid']
		item['major'] = items[0]['major']
		item['minor'] = items[0]['minor']
		item['distance'] = items[0]['distance']
		for x in items:
			item['distance'] = item['distance'] + x['distance']
		item['distance'] = item['distance']/float(len(items))
		return item
	else:
		return None


def beacon_close():
	returnedList = blescan.parse_events(sock, 10)
	newlist = sorted(returnedList, key=lambda k: k['distance']) 
	beacon = mashup(newlist)

	if(beacon is not None):
		things = Beacon.query.all()
		for x in things:
			if x.uuid.replace('-','') == beacon['uuid'] and x.major == beacon['major'] and x.major == beacon['major']:
				return True
	return False

def device_in_net():
	ret = commands.getoutput("arp -an | grep cc:fa:00:f6:9b:d6")
	return not (ret == '')

beacon = False
movimiento = False
device = False

while True:
	# activate the sensor
	# check the database for recent movements

	newbeacon = beacon_close()
	newmovimiento = sensor()
	newdevice = device_in_net()

	if((beacon != newbeacon) or (movimiento != newmovimiento) or (device != newdevice)):
		print "Event"
		payload = {'movimiento': newmovimiento, 'beacon': newbeacon, 'device': newdevice}
		r = requests.get("http://localhost:8080/event", params=payload)
		movimiento = newmovimiento
		beacon = newbeacon
		device = newdevice




	

