#!flask/bin/python
from l8Signals import ELE8
from flask import Flask, jsonify
from flask.ext.sqlalchemy import SQLAlchemy
from flask import request
from gcm import GCM
import flask.ext.restless
import json
import time
import commands
import pickledb

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///temp/test.db'
db = SQLAlchemy(app)

class Beacon(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(120))
    major = db.Column(db.Integer)
    minor = db.Column(db.Integer)

    def __init__(self, uuid, major, minor):
        self.uuid = uuid
        self.major = major
        self.minor = minor

    def __repr__(self):
        return '<Beacon %r>' % self.uuid

def send_push(type, description):
  gcm = GCM('AIzaSyCxXaknhqHcNAxxSKGseYQrpgHB5COLF00')
  data = {'type': type, 'message': description}
  try:
    gcm.plaintext_request(registration_id='APA91bGxJ3Y2n4pExPc8kX1PAyuARTuA9p8a3LxwTj9d6LbAQ3aE4TYeaJ13nxqDohOtBW0ief8VdQl_H4Zz31gm5Csa61eT68RyeudpJH7lwJrzy-5yl3VmvZzYU3uIOnYMTfSGMozmhkV6DfEGblz6xUGmLrYBxQ', data=data, retries=10 )
  except:
    print 'fail'

def send_intrusion():
  send_push('Intrusion', 'Alguien no autorizada ha entrado en el hogar')

def send_entrada_salida():
  send_push('Acceso', 'Alguien conocido ha entrado en casa')


@app.route('/event', methods=['GET'])
def get_events():
   pdb = pickledb.load('movements.db', False)
   status = pdb.get('status')
   beacon = request.args.get('beacon',False,type=bool)
   device = request.args.get('device',False,type=bool)
   movement = request.args.get('movement', False, type=bool)
   if(not beacon and not device and movement and status != 0):
      send_intrusion()
      x8 = ELE8()
      x8.initial()
      x8.paint_d()
      time.sleep(5)
      x8.finalize()
      status = 0
   elif(beacon or device):
      if(not device and status != 2):
        x = commands.getoutput("sudo bash changeDNS.sh -f")
        status = 2
      elif(status != 3):
        x = commands.getoutput("sudo bash changeDNS.sh -o")
        status = 3
      else:
        status = 1
      send_entrada_salida()
      x8 = ELE8()
      x8.initial()
      x8.paint_ok()
      time.sleep(5)
      x8.finalize()
   else:
      status = 4
   pdb.set('status',status)
   pdb.dump()
   return "Yeah"

@app.route('/open', methods=['GET'])
def get_open():
   send_entrada_salida()
   x8 = ELE8()
   x8.initial()
   x8.paint_ok()
   time.sleep(5)
   x8.finalize()
   return 'Yeah'

manager = flask.ext.restless.APIManager(app, flask_sqlalchemy_db=db)

manager.create_api(Beacon, methods=['GET', 'POST', 'DELETE'])


if __name__ == '__main__':

    app.run(debug=True, host='0.0.0.0', port=8080)

