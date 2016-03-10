#!/usr/bin/env python
import os
import sys

import pymongo
import datetime

#Start
my_settings = {
    'MONGO_HOST': 'momenta.herokuapp.com',
   # 'MONGO_PORT': 27017,
    #'MONGO_DBNAME': 'the_db_name',
    'DOMAIN': {'contacts': {}}
}
   
from eve import Eve

if 'PORT' in os.environ:
    port = int(os.environ.get('PORT'))
    # use '0.0.0.0' to ensure your REST API is reachable from all your
    # network (and not only your computer).
    host = '0.0.0.0'
else:
    port = 80
    host = 'momenta.herokuapp.com'

app = Eve(settings=my_settings)


#End

if __name__ == "__main__":
  #  os.environ.setdefault("DJANGO_SETTINGS_MODULE", "gettingstarted.settings")

  #  from django.core.management import execute_from_command_line

  #  execute_from_command_line(sys.argv)
    app.run(host=host, port=port)


from pymongo import MongoClient
client = MongoClient()

client = MongoClient('mongodb://Vedha:test@ds055485.mongolab.com:55485/momenta-sandbox')

db = client['momenta-sandbox']

from datetime import datetime
result = db.Momenta.insert_one(
    {
        "address": {
            "street": "Janac is the best",
            "zipcode": "Only on drugs",
            "building": "Sometimes when hes not",
            "coord": [-73.9557413, 40.7720266]
        },
        "borough": "Upmyass",
        "cuisine": "Inmynose",
        "grades": [
            {
                "date": datetime.strptime("2014-10-01", "%Y-%m-%d"),
                "grade": "A",
                "score": 11
            },
            {
                "date": datetime.strptime("2014-01-16", "%Y-%m-%d"),
                "grade": "B",
                "score": 17
            }
        ],
        "name": "Vella",
        "restaurant_id": "41704620"
    }
)
print result.inserted_id