# Manually running the project

Install Docker or compatible runtime:

### Windows
https://docs.microsoft.com/en-us/windows/wsl/install

https://docs.microsoft.com/en-us/windows/wsl/tutorials/wsl-containers

### Linux/Mac/Etc

https://docs.docker.com/engine/


Pull the official Oracle container repository Dockerfile and build the EE image at oracle/docker-images/tree/main/OracleDatabase/SingleInstance/dockerfiles/, download the LINUX.X64_213000_db_home.zip installation file for Oracle EE 21.3, place it in the 21.3.0 folder, and run:

./buildContainerImage.sh -v 21.3.0 -t oracle:21.3.0-0-ee -e

Pull or build the dftt and dftt-db images.

Create a host-only bridge network for DFTT, this will enable the service discovery mechanism and insure that DFTT doesn't conflict with any existing network resources:

docker network create --driver bridge dftt-net

Start the database and set the necessary password:

mkdir -p \$(pwd)/database

docker run -e ORACLE_PDB=ORCL -e ORACLE_PWD=changeit -v $(pwd)/database/:/opt/oracle/oradata/ --network dftt-net --name dftt-db -d -p 127.0.0.1:1521:1521 dftt-db

Start the gui:

mkdir -p \$(pwd)/config

mkdir -p \$(pwd)/data

docker run -p 127.0.0.1:8080:8080 -v $(pwd)/config/:/opt/apps/config/ -v \$(pwd)/data/:/opt/apps/data/ -e DB_URL=dftt-db --name dftt-gui --network dftt-net -d dftt

Log into the gui by opening a browser and visiting:
http://127.0.0.1:8080/

You can now connect to the database from the Builder to start working on DFTT projects.

### FDSN example

cd /opt/apps/config/ && ../ConfigCreator_2.0.sh dftt@dftt-db -c TJN -m 2022040 -M 2022046 -s TJN -I IRISDMC -type FDSN

### Continuous Waveform table example

./ConfigCreator_2.0.sh dftt@dftt-db -c JOKA_Z -D 1 -d 1 -e 10 -f 2 -F 20 -i 5 -m 2018119 -M 2018124 -n 80 -s JOKA -S 2 -t 0.5

Note that for the continuous waveform usage you will need to have waveforms available and mounted in volumes into the docker container that match the paths entered into the CONTINUOUS_WAVEFORM table.