#!/bin/bash
set -e

# dataset to ingest to the database
OSM_DOWNLOAD_URL="http://download.geofabrik.de/europe/germany/nordrhein-westfalen/muenster-regbez-latest.osm.pbf"

# database related parameters
MAPSERVER_DOCKER_INSTANCE="docker_mapserver_1"
MAPSERVER_DBNAME="postgres"
MAPSERVER_DBUSER="postgres"
MAPSERVER_DBPASS="postgres"
MAPSERVER_MODE="slim"

# ingestion into docker-container
docker exec -it $MAPSERVER_DOCKER_INSTANCE /bin/bash --rcfile /root/.bashrc -ci \
    "wget -O /osm-data.osm.pbf $OSM_DOWNLOAD_URL && /mnt/map/osm/import.sh /osm-data.osm.pbf $MAPSERVER_DBNAME $MAPSERVER_DBUSER $MAPSERVER_DBPASS /mnt/map/tools/road-types.json $MAPSERVER_MODE"