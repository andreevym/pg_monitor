#!/bin/bash
docker stop tbk-telegraf1
docker rm tbk-telegraf1
docker run --net=container:twyn-influxdb1 --restart=always --name tbk-telegraf1 tbk-telegraf