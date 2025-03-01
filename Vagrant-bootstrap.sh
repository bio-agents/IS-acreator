#!/usr/bin/env bash

sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install -y openjdk-7-jdk maven zip python xvfb
sudo echo "export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64" >> /home/ubuntu/.bashrc

# Workaround for not finding xalan dependency, fix pinched from https://github.com/ISA-agents/linkedISA/pull/13
mvn dependency:get -Dartifact=xalan:xalan:2.4.0
mvn install:install-file -Dfile="$HOME/.m2/repository/xalan/xalan/2.4.0/xalan-2.4.0.jar" -DgroupId=xalan -DartifactId=xalan -Dversion=2.4 -Dpackaging=jar