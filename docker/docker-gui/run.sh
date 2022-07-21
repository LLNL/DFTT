#!/bin/bash
#Remove any stale X11 mounts to docker start works cleanly on a docker stop
rm -rf /tmp/.X*

HOME="/opt/apps" USER="apps" LANG="en_US.UTF-8" /usr/bin/vncserver :1 -geometry 800x600 -depth 16 -SecurityTypes None &
HOME="/opt/apps" USER="apps" LANG="en_US.UTF-8" /usr/sbin/xrdp-sesman --nodaemon &
HOME="/opt/apps" USER="apps" LANG="en_US.UTF-8" /usr/sbin/xrdp --nodaemon &
export DISPLAY=:1.0

/usr/bin/websockify --web=/usr/share/novnc/ 8080 localhost:5901 2> /dev/null 