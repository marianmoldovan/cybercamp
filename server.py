#!flask/bin/python
from flask import Flask, jsonify
from flask.ext.sqlalchemy import SQLAlchemy
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

@app.route('/beaconX/api/', methods=['GET'])
def get_beacons():
    b = Beacon("aaaaaa", dd1, 1)
    return json.dumps(Beacon.query.all().__dict__)

manager = flask.ext.restless.APIManager(app, flask_sqlalchemy_db=db)

manager.create_api(Beacon, methods=['GET', 'POST', 'DELETE'])

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8080)


