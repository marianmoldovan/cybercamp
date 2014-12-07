import blescan
import sys
import bluetooth._bluetooth as bluez
import time
import pickledb


from flask.ext.sqlalchemy import SQLAlchemy
from flask import Flask, jsonify
from server import Beacon
app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///temp/test.db'
db = SQLAlchemy(app)

#sys.path.append('/home/pi/workspace/cybercamp')
#import sensor

#indicar movimiento 
movimiento=False

dev_id = 0
try:
	sock = bluez.hci_open_dev(dev_id)
	print "ble thread started"
except:
	print "error accessing bluetooth device..."
	sys.exit(1)
blescan.hci_le_set_scan_parameters(sock)
blescan.hci_enable_le_scan(sock)

pdb = pickledb.load('movements.db', False)

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

while True:
	# activate the sensor
        	
	# check the database for recent movements

	movimiento = pdb.get('valor')
	beacon = beacon_close()
	
	
