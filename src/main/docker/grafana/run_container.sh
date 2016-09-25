#!/bin/bash
sudo docker stop tbk-grafana1
sudo docker rm tbk-grafana1
sudo docker run -d -p 3000:3000 \
   --restart=always \
   --link tbk-influxdb1:influxdb \
   --volume=/opt/docker-data/grafana:/var/lib/grafana:z \
   --volume=/opt/docker-data/grafana/log:/var/log/grafana \
   --name tbk-grafana1 \
   tbk-grafana