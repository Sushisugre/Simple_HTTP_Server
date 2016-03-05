#!/bin/bash
for i in `seq 1 10`;
do
    httperf --server localhost --port 65530 --num-conns 200 --http-version 1.0 —uri=/image/server_attention_span.png &
done
