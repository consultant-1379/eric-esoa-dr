#!/usr/bin/env bash
#
# COPYRIGHT Ericsson 2023
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

echo "Starting DR Slicing Feature Test Execution"

command="exec java -javaagent:${ASPECTJ_JAR} -jar /dr-e2e-tests/eric-esoa-dr-e2e-tests.jar $1"

echo "Running command: ${command}"
${command}