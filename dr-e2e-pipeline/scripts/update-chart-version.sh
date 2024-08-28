#!/bin/bash
set -x

chart=$(find charts/ -name Chart.yaml -print)
chart_version=$(cat ${chart} | yq -r .version)
echo "Current chart version is $chart_version"

sed -i "s/$chart_version/$VERSION/g" $chart
echo "Chart version updated to $VERSION"

git add $chart