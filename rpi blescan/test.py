import blescan
import sys
import bluetooth._bluetooth as bluez
import time

dev_id = 0
try:
	sock = bluez.hci_open_dev(dev_id)
	print "ble thread started"
except:
	print "error accessing bluetooth device..."
	sys.exit(1)
blescan.hci_le_set_scan_parameters(sock)
blescan.hci_enable_le_scan(sock)
while True:
	returnedList = blescan.parse_events(sock, 10)
	newlist = sorted(returnedList, key=lambda k: k['distance']) 
	for beacon in newlist:
		print beacon
	time.sleep(1)

