#!/bin/bash
echo Running Chat x86
java -cp .:swt-3.7.2-mac-x86.jar -Djavax.net.ssl.trustStore=mySrvKey -Djavax.net.ssl.trustStorePassword=123456 Program
echo Application terminated
