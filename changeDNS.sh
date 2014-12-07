#!/bin/bash

function usage(){
	echo "<script name> [o|f]"
		echo	"-o  starts the openDNS Server"
		echo    "-f  stops the openDNS server"		
}

function startOpenDNS(){
	echo "Cambiando a OpenDNS"
		sudo service hostapd stop
		sudo service isc-dhcp-server stop
		sudo cp /etc/dhcp/dhcpd-opendns.conf /etc/dhcp/dhcpd.conf
		sudo ifconfig wlan0 192.168.42.1
		sudo service isc-dhcp-server start
		sudo service hostapd start
		echo "Cambio a OpenDNS realizado"
}

function stopOpenDNS(){
	echo "Quitando el OpenDNS"
		sudo service hostapd stop
		sudo service isc-dhcp-server stop
		sudo cp /etc/dhcp/dhcpd-normal.conf /etc/dhcp/dhcpd.conf
		sudo ifconfig wlan0 192.168.42.1 
		sudo service isc-dhcp-server start
		sudo service hostapd start
		echo "OpenDNS apagado"
}

while getopts ":of" opts; do
case "${opts}" in
o)
startOpenDNS
;;
f)
stopOpenDNS
;;
*)
usage
exit -1
;;
esac
done


exit 0
