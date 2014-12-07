#!/bin/bash

function usage(){
echo "<script name> [o|f]"
echo	"-o  starts the openDNS Server"
echo    "-f  stops the openDNS server"		
}

function startOpenDNS(){
echo "Cambiando a OpenDNS"
sudo mv /etc/dhcp/dhcpd-opendns.conf /etc/dhcp/dhcpd.conf
sudo service isc-dhcp-server restart
sudo service hostapd restart
echo "Cambio a OpenDNS realizado"
}

function stopOpenDNS(){
echo "Quitando el OpenDNS"
sudo mv /etc/dhcp/dhcpd-normal.conf /etc/dhcp/dhcpd.conf
sudo service isc-dhcp-server restart
sudo service hostapd restart
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
