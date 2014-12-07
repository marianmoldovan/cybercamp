#!flask/bin/python
from flask import Flask, jsonify
from flask.ext.sqlalchemy import SQLAlchemy
from flask import request
from gcm import GCM
import flask.ext.restless
import json

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

@app.route('/beaconX/api/', methods=['GET'])
def get_beacons():
    b = Beacon("aaaaaa", dd1, 1)
    return json.dumps(Beacon.query.all().__dict__)

@app.route('/event', methods=['GET'])
def get_events():
   beacon = request.args.get('beacon',0,type=int)
   device = request.args.get('device',0,type=int)
   movement = request.args.get('movement', 0, type=int)
   send_intrusion()
   return 'Yeah'

@app.route('/open', methods=['GET'])
def get_open():
   return 'Yeah'

manager = flask.ext.restless.APIManager(app, flask_sqlalchemy_db=db)

manager.create_api(Beacon, methods=['GET', 'POST', 'DELETE'])


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8080)


