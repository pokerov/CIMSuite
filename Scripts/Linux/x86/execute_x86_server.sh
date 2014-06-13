#!/bin/bash
echo Running Server x86
java -cp .:swt-3.7.2-lin-x86.jar -Djavax.net.ssl.keyStore=mySrvKey -Djavax.net.ssl.keyStorePassword=123456 Program
echo Application terminated
