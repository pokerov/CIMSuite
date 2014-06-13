#!/bin/bash
echo Running Chat x86_64
java -cp .:swt-3.7.2-lin-x86_64.jar -Djavax.net.ssl.trustStore=mySrvKey -Djavax.net.ssl.trustStorePassword=123456 Program
echo Application terminated
