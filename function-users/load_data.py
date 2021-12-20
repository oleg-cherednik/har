import argparse
import json
from decimal import Decimal

import boto3


def load_data(items, tableName):
    print("tableName: " + tableName)
    print("totalItems: " + str(len(items)))

    dynamodb = boto3.resource('dynamodb')
    devices_table = dynamodb.Table(tableName)

    for item in items:
        devices_table.put_item(Item=item)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--project', default='har')
    parser.add_argument('--env', default='dev')
    args = parser.parse_args()

    tableName = 'Users'

    with open("src/test/resources/users.json") as json_file:
        items = json.load(json_file, parse_float=Decimal)

    load_data(items, args.project + '-' + args.env + '-' + tableName)
