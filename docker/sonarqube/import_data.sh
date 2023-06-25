#!/bin/bash
set -e

echo "Initiating..."

SONAR_HEALTH_URL="$SONAR_URL/api/system/health"
SONAR_RESTORE_PROFILE_URL="$SONAR_URL/api/qualityprofiles/restore"
SONAR_DEFAULT_PROFILE_URL="$SONAR_URL/api/qualityprofiles/set_default"

CUSTOM_PROFILE_JAVA="Sonar%20way%20(extended)"
FILE_JAVA="/java_profile.xml"

# INSTALL CURL
apk --no-cache add curl

echo "Initiated."

# WAIT UNTIL SONARQUBE IS UP
retry_counter=0

while true; do
  echo "Checking if SonarQube is up... Retries so far: $retry_counter"
  STATUS=$(curl -f -s -o /dev/null -I -w "%{http_code}" -u "$SONAR_USER":"$SONAR_PASSWORD" -X GET "$SONAR_HEALTH_URL") || true
  echo "Received HTTP status: $STATUS."

  if [ "${STATUS:0:1}" -eq 2 ]; then
    echo "SonarQube is up."
    break
# Detecting 401 doesn't seem to work reliably. It seems that sometimes SonarQube just gives a 401...
#  elif [ "${STATUS:0:3}" -eq 401 ]; then
#    echo "Aborting configuration. Are the credentials correct?"
#    exit 1
  elif [ "${STATUS:0:1}" -eq 5 ]; then
    echo "Aborting configuration. Something went wrong inside SonarQube."
    exit 1
  else
    retry_counter=$((retry_counter + 1))
    if [ $retry_counter -eq 100 ]
    then
      echo "Retried $retry_counter times, giving up."
      exit 1
    fi
    echo "Waiting to retry..."
    sleep 1
  fi

done

# PROFILE - JAVA
echo "Importing custom profile ($FILE_JAVA)..."

curl -s -o /dev/null -X POST -u "$SONAR_USER":"$SONAR_PASSWORD" "$SONAR_RESTORE_PROFILE_URL" --form backup=@$FILE_JAVA ||
  (echo "Could not import custom profile. Aborting configuration." && exit 1)

echo "Setting the profile as default..."
curl -s -o /dev/null -u admin:admin -d "language=java&qualityProfile=$CUSTOM_PROFILE_JAVA" -X POST "$SONAR_DEFAULT_PROFILE_URL" ||
  (echo "Could set custom profile as default. Aborting configuration." && exit 1)

echo "Data import done."