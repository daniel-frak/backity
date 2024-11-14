#!/bin/bash
set -e

echo "Initiating..."

SONAR_HEALTH_URL="$SONAR_URL/api/system/health"
SONAR_RESTORE_PROFILE_URL="$SONAR_URL/api/qualityprofiles/restore"
SONAR_DEFAULT_PROFILE_URL="$SONAR_URL/api/qualityprofiles/set_default"
SONAR_ADD_PERMISSION_URL="$SONAR_URL/api/permissions/add_group"
SONAR_SECURITY_SETTINGS_URL="$SONAR_URL/api/settings/set"
SONAR_QUALITY_GATE_URL="$SONAR_URL/api/qualitygates"
SONAR_ADD_PLUGINS_URL="$SONAR_URL/api/plugins/install"
SONAR_RESTART_SERVER_URL="$SONAR_URL/api/system/restart"
#NOTE: Profile name must be the same as in the file
JAVA_PROFILE_NAME="Sonar%20way%20(extended)%20%2B%20Mutation"
JAVA_PROFILE_FILE_PATH="/java_profile.xml"
QUALITY_GATE_NAME="Backend%20way"

function createQualityGateConditionsOnOverallCode() {
    createQualityGateCondition coverage LT 100
    createQualityGateCondition code_smells GT 0
    createQualityGateCondition duplicated_lines_density GT 5
    createQualityGateCondition maintainability_rating GT 1
    createQualityGateCondition reliability_rating GT 1
    createQualityGateCondition security_hotspots_reviewed LT 100
    createQualityGateCondition security_rating GT 1
    createQualityGateCondition violations GT 0
}

function createQualityGateConditionsOnNewCode() {
    createQualityGateCondition new_coverage LT 100
    createQualityGateCondition new_code_smells GT 0
    createQualityGateCondition new_duplicated_lines_density GT 5
    createQualityGateCondition new_maintainability_rating GT 1
    createQualityGateCondition new_reliability_rating GT 1
    createQualityGateCondition new_security_hotspots_reviewed LT 100
    createQualityGateCondition new_security_rating GT 1
    createQualityGateCondition new_violations GT 0
}

function createQualityGateCondition() {
  local metric_name="${1}"
  local operation_name="${2}"
  local treshold="${3}"

  curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "metric=$metric_name" -d "op=$operation_name" \
  -d "gateName=$QUALITY_GATE_NAME" -d "error=$treshold" -X POST "$SONAR_QUALITY_GATE_URL/create_condition" ||
                   (echo "Could not create condition metric=$metric_name, operation_name=$operation_name, \
                   treshold=$treshold . Aborting configuration." && exit 1)
}

function waitUntilSonarQubeIsUp() {
    retry_counter=0
    while true; do
      echo "Checking if SonarQube is up... Retries so far: $retry_counter"
      STATUS=$(curl -f -s -o /dev/null -I -w "%{http_code}" -u "$SONAR_USER":"$SONAR_PASSWORD" \
      -X GET "$SONAR_HEALTH_URL") || true
      echo "Received HTTP status: $STATUS."

      if [ "${STATUS:0:1}" -eq 2 ]; then
        echo "SonarQube is up."
        break
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
        sleep 5
      fi

    done
}

# INSTALL CURL
apk --no-cache add curl

# INSTALL jq
apk --no-cache add jq

echo "Initiated."

waitUntilSonarQubeIsUp;

echo "Disabling 'Force user authentication'"

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "key=sonar.forceAuthentication&value=false" \
    -X POST "$SONAR_SECURITY_SETTINGS_URL" ||
      (echo "Could not disable 'Force user authentication'. Aborting configuration." && exit 1)

echo "Disabling 'Force user authentication' done"

echo "Adding permissions: create projects, execute analysis, configure profiles and gates to 'anyone' group"

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "groupName=Anyone&permission=gateadmin" \
    -X POST "$SONAR_ADD_PERMISSION_URL" ||
      (echo "Could not add 'configure gates' permission to 'anyone' group. Aborting configuration." && exit 1)

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "groupName=Anyone&permission=profileadmin" \
    -X POST "$SONAR_ADD_PERMISSION_URL" ||
      (echo "Could not add 'configure profiles' permission to 'anyone' group. Aborting configuration." && exit 1)

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "groupName=Anyone&permission=scan" \
    -X POST "$SONAR_ADD_PERMISSION_URL" ||
      (echo "Could not add 'execute analysis' permission to 'anyone' group. Aborting configuration." && exit 1)

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "groupName=Anyone&permission=provisioning" \
    -X POST "$SONAR_ADD_PERMISSION_URL" ||
      (echo "Could not add 'create projects'  permission to 'anyone' group. Aborting configuration." && exit 1)

echo "Adding permissions done"

echo "Installing 'Mutation Analysis' plugin"

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "key=sonar.plugins.risk.consent&value=ACCEPTED" \
    -X POST "$SONAR_SECURITY_SETTINGS_URL" ||
      (echo "Could not accept plugins consent. Aborting configuration." && exit 1)

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "key=mutationanalysis" -X POST "$SONAR_ADD_PLUGINS_URL" ||
      (echo "Could not add 'Mutation Analysis' plugin. Aborting configuration." && exit 1)

    echo "Restarting server to complete plugin installation"

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -X POST "$SONAR_RESTART_SERVER_URL" ||
      (echo "Could not restart server. Aborting configuration." && exit 1)

    # Wait for the restart request to be processed.
    sleep 10

    waitUntilSonarQubeIsUp;

echo "Installing 'Mutation Analysis' plugin done"

# PROFILE - Java + Mutation
echo "Importing custom profile ($JAVA_PROFILE_FILE_PATH)..."

    curl -s -o /dev/null -X POST -u "$SONAR_USER":"$SONAR_PASSWORD" "$SONAR_RESTORE_PROFILE_URL" \
    --form backup=@$JAVA_PROFILE_FILE_PATH ||
      (echo "Could not import $JAVA_PROFILE_FILE_PATH profile. Aborting configuration." && exit 1)

echo "Setting the profile as default..."

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "language=java&qualityProfile=$JAVA_PROFILE_NAME" \
    -X POST "$SONAR_DEFAULT_PROFILE_URL" ||
      (echo "Could not set $JAVA_PROFILE_NAME profile as default. Aborting configuration." && exit 1)


echo "Creating quality gate and setting it as a default"

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "name=$QUALITY_GATE_NAME" \
    -X POST "$SONAR_QUALITY_GATE_URL/create" ||
      (echo "Could not create quality gate. Aborting configuration." && exit 1)

    curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "name=$QUALITY_GATE_NAME" \
    -X POST "$SONAR_QUALITY_GATE_URL/set_as_default" ||
      (echo "Could not set quality gate as a default. Aborting configuration." && exit 1)

echo "Creating quality gate done"

echo "Setting '$QUALITY_GATE_NAME' quality gate"

    echo "Deleting existing conditions"

    CONDITIONS_JSON_RAW="$(curl -s -u $SONAR_USER:$SONAR_PASSWORD \
    -X GET "$SONAR_QUALITY_GATE_URL/show?name=$QUALITY_GATE_NAME")" ||
      (echo "Could not get quality gate conditions. Aborting configuration." && exit 1)

    CONDITIONS_IDS_RAW=$(echo "$CONDITIONS_JSON_RAW" | jq '.conditions[].id')

    for CONDITION_ID_RAW in $CONDITIONS_IDS_RAW ; do
         CONDITION_ID=$(echo $CONDITION_ID_RAW | tr -d '"')
         echo "Deleting condition id: $CONDITION_ID"
         curl -s -o /dev/null -u $SONAR_USER:$SONAR_PASSWORD -d "id=$CONDITION_ID" \
         -X POST "$SONAR_QUALITY_GATE_URL/delete_condition" ||
          (echo "Could not delete condition id=$CONDITION_ID. Aborting configuration." && exit 1)
    done

    echo "Deleting existing conditions done"

    echo "Setting conditions - Overall Code"

    createQualityGateConditionsOnOverallCode;

    echo "Setting conditions - Overall Code done"

    echo "Setting conditions - New Code"

    createQualityGateConditionsOnNewCode;

    echo "Setting conditions - New Code done"

echo "Setting '$QUALITY_GATE_NAME' quality gate done"

echo "Data import done."