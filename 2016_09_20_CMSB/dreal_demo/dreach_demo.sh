#!/bin/bash

echo \#\# bouncing_ball.drh
cat bouncing_ball.drh

echo
echo \#\# Enter to execute: dReach -l 8 -u 10  bouncing_ball.drh --precision 0.001 --visualize
read
dReach -l 8 -u 10  bouncing_ball.drh --precision 0.001 --visualize

echo
echo \#\# Enter to visualize the result \(bouncing_ball_9_0.smt2.json\)
read

echo Enter the path to ODE_visualization \(e.g. \$HOME/dreal/tools/ODE_visualization\):
read visual
#visual=$HOME/work/projects/dreal3_dz/tools/ODE_visualization

cp bouncing_ball_9_0.smt2.json $visual/data.json
cd $visual
./run_websvr.sh &
firefox "http://localhost:8000/"
cd -
sleep 5
kill -TERM 0
