#!/bin/bash

testng_file=$1

echo "Checking testng results"

# read the number of passed and failed tests from testng results, <testng-results ignored="1" total="4" passed="2" failed="0" skipped="3">
# based on above testng_results value would be "2 0"
testng_results=$(python3 -c "import xml.etree.ElementTree as ET;tree = ET.parse('$testng_file');root = tree.getroot();print(root.attrib['passed'],root.attrib['failed'])")
read -ra passed_and_failed <<< "$testng_results"
passed=$(echo ${passed_and_failed[0]})
failed=$(echo ${passed_and_failed[1]})

if [ $passed == 0 ]; then
 echo "No tests executed"
 exit 1
elif [ $failed -gt 0 ]; then
 echo "One or more test failures"
 exit 2
fi
echo "All tests passed"
