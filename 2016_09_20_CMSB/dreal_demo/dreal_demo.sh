#!/bin/bash

echo \#\# 01.dr
cat 01.dr

echo
echo \#\# Enter to execute: dReal 01.dr
read
dReal 01.dr

echo 
echo \#\# Enter to show: 01.smt2
read
cat 01.smt2

echo
echo \#\# Enter to execute: dReal 01.smt2
read
dReal 01.smt2

echo
echo \#\# Enter to execute: dReal 01.smt2 --model
read
dReal 01.smt2 --model

echo
echo \#\# Enter to plot
read
gnuplot plot1.gnuplot

echo
echo \#\# Enter to show: 02.dr
read
cat 02.dr

echo
echo \#\# Enter to execute: dReal 02.dr
read
dReal 02.dr

echo 
echo \#\# Enter to plot
read
gnuplot plot2.gnuplot

echo 
echo \#\# Enter to show: 03.dr
read
cat 03.dr

echo
echo \#\# Enter to execute: dReal 03.dr
read
dReal 03.dr

echo
echo \#\# Enter to execute: dReal 03.dr --precision 0.0001
read
dReal 03.dr --precision 0.0001
echo
