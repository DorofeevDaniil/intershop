#!/bin/sh
# wait-for-keycloak.sh

set -e

host="$1"
shift
cmd="$@"

CURL=$(which curl)

echo "Waiting for Keycloak at $host..."

until $CURL -s -f http://$host/realms/master/.well-known/openid-configuration > /dev/null 2>&1; do
  echo "Keycloak not ready yet, retrying in 3 seconds..."
  sleep 3
done

echo "Keycloak is up! Starting application..."
exec "$@"