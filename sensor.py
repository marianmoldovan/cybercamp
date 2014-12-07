#!/usr/bin/Python2.7
# -*- coding: utf-8 -*-
import time
import pprint
import requests
import json
import sys
import pickle
import pickledb 
import l8Signals

#class made to recover from z-way-server the sensor data
#gets the ip addres
#returns the data
class ZWay:
    def __init__(self):
        #server ip & port:
        self.address = "http://192.168.1.147:8083/"

        #constants values of the ZWaveAPI
        self.getAllDataURL = 'ZWaveAPI/Data/0'
        self.getDeviceDataURL_1 = 'ZWaveAPI/Run/devices['
        self.getInquireURL = '].instances[0].commandClasses[0x30].Get()'
        self.getDeviceDataURL_2 = '].instances[0].commandClasses[0x30].data'


    def performGetRequest(self):

        #search for connected devices
        deviceN_=self.getDevices()
        data=Render()
        #TODO:test the devices that have no relevant data
        
        #for each one of the devices try to pull data
        for deviceNum in deviceN_ :        
            #obtains last updateTime from the sensor
            
	    #timeStam = data.getSensorUpdateTime(self.getRequest(self.getDeviceDataURL_1+str(deviceNum)+self.getDeviceDataURL_2,'OK'))
            
            #inquires the devices for new data
            #self.getRequest(self.getDeviceDataURL_1+str(deviceNum)+self.getInquireURL,None)
            
            #obtains again the updateTime from sensor
            #timeStam2 = data.getSensorUpdateTime(self.getRequest(self.getDeviceDataURL_1+str(deviceNum)+self.getDeviceDataURL_2,'OK'))
            #print 'inital time stamp: ',timeStam, ' actual time: ',time.time(), ' update time:',timeStam2
            #while the data has not been updated wait for 1 minute
            #info = None
	    #print ("timeStam ", timeStam, ">= timeStam2", timeStam2)
	    
            #while (timeStam >= timeStam2):
            #if (timeStam != timeStam2):
                #enquires again for UpdateTime
       #         print "waiting for sensor response..."

             #   time.sleep(10)
            info= self.getRequest(self.getDeviceDataURL_1+str(deviceNum)+self.getDeviceDataURL_2,'OK')
             #timeStam2 = data.getSensorUpdateTime(info)
             #new instances of the render clas that will be entitled to feed the Spring api server with the proper data
	    if (not info == None):
#	print data
	    	data.initiate(info)
	    	print "Movimiento detectado"
	print "sin movimiento"
#else:
#     raise MyException(str(e)), None, sys.exc_info()[2]


    def getSensorUpdateTime(self,jsonData):
        resp=None
        try:
            for id_, item in jsonData['1'].iteritems():
                if id_ == 'updateTime':
                    resp = item
		    print item
        except Exception as e:
            print 'ERROR during updating the Sensor Time:'
            raise MyException(str(e)), None, sys.exc_info()[2]
        return resp


    def getDevices(self):
        
        devicesList = []
        jsonDev=self.getRequest(self.getAllDataURL,'OK')
        try:
            for id_, item in jsonDev['devices'].iteritems():
                if id_ != '1' and item!="NoneType":
                    devicesList.append(id_)
            #print (id_ , item)   

        except Exception as e:
            print 'ERROR during searching for devices'
            raise MyException(str(e)), None, sys.exc_info()[2]
            return None

        return devicesList


    def getRequest(self,direction,err):
        resp = None
        try:
            print 'Sending a get request to: ',self.address+direction
            response = requests.get(self.address+direction)
            result=response.status_code
            if (result == 200 and err != None):
                resp = response.json()
            elif (result == 200 and err == None):
                resp = 'OK'
        except Exception as e:
            print 'ERROR sending a get request to this direction:',direction
            raise MyException(str(e)), None, sys.exc_info()[2]
        return resp


#render class
class Render:
    def __init__(self):
        self.MAXFIELDS=10
        self.MINFIELDS=0    
        self.sensorList=[]
        self.db = pickledb.load('movements.db', False) 
        
        self.setSTime( 0)
        print 'Processing the data'

    def setSTime(self,  value):
        self.db.set('valor', value)
        self.db.dump()

    def getSTime(self):
	algo=self.db.get('valor')
        return algo    

    def initiate(self, sensorData):
        print 'initiate sensorData'
        try:
            for i in range (self.MINFIELDS,self.MAXFIELDS):
                if str(i) in sensorData:
                    self.processFields(self.recoverFields(sensorData,str(i)))
            #jsons=self.mountJSON()
            #self.sendDataSpring(jsons)
        except BaseException as e:
            raise MyException(str(e)), None, sys.exc_info()[2]

    def getSensorUpdateTime(self,jsonData):
        resp=None
        try:
            for id_, item in jsonData['1'].iteritems():
                if id_ == 'updateTime':
                    resp = item
        except Exception as e:
            print 'ERROR during updating the Sensor Time:'
            raise MyException(str(e)), None, sys.exc_info()[2]
        return resp


#******************
#Aquí se recuperan los valores 
#******************
    def recoverFields(self,data,position):
        try:
	    tiempo=0;
	    valor="false";
            for id_, item in data[position].iteritems():
                if id_ == 'sensorTypeString':
                    print self.getValue(item)                  
                elif id_ == 'level':
                    valor= self.getValue(item)
                    tiempo= self.getTime(item)
		    print tiempo
                elif id_ == 'scaleString':
                    print self.getValue(item)
           # if (self.getValue is "true") and (tiempo >= self.getSTime()):
	    print ("valor: ", valor)
            self.setSTime(valor)
            print ("zegundoz", self.getSTime())
        except Exception as e:
            raise MyException(str(e)), None, sys.exc_info()[2]
            return None
    

    def getValue(self, diction):
        try:
            for id_, item in diction.iteritems():
                if id_ == 'value' :
                    return item
        except Exception as e:
            raise MyException(str(e)), None, sys.exc_info()[2]
            return None

    def getTime(self, diction):
        try:
            for id_, item in diction.iteritems():
                if id_ == 'updateTime' :
                    return item
        except Exception as e:
            raise MyException(str(e)), None, sys.exc_info()[2]
            return None

#        raise MyException('Not Implemented yet')

    def processFields(self,model):
        try:
            if not model == None:
                self.sensorList.append(model)
        except Exception as e:
            raise MyException(str(e)), None, sys.exc_info()[2]


#own type of exception
class MyException(Exception): pass        


    
    

print '**********  Spring newtwork ***********\n'

#Test class instances

#inI=Initial()
#inI.initiate()

#reads the server ip from config file
#z = ZWay(FileManager().readFileKeys("ip"))
#z = ZWay("http://192.168.1.147:8083/")
#z = ZWay()
#while True:
#    z.performGetRequest()
#    time.sleep(2)
	
#request to server
#resp = z.performGetRequest()
print '\n **********  Spring newtwork ***********\n'
