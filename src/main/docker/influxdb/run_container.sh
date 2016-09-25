#!/bin/bash
docker stop tbk-influxdb1
docker rm tbk-influxdb1
sudo docker run \
  --volume=/opt/docker-data/influxdb:/var/lib/influxdb:z \
  --restart=always  \
  -p 8083:8083  \
  -p 8086:8086  \
  --name tbk-influxdb1 \
  tbk-influxdb