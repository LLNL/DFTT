#!/bin/sh
rm DetectionFrameworkOpenSource*jar
rm Builder_2.0.sh
rm ConfigCreator_2.0.sh
rm DetSegExtractor_2.0.sh
rm FrameworkRunner_2.0.sh

cp ../../scripts/launch/* .
cp ../../target/DetectionFrameworkOpenSource*.jar .
docker build . -t dftt:latest