import requests, json, sys
from bos.dr.clients import restservice

target_subsystem=sys.argv[1]
response = restservice.run(target_subsystem, "wiremock_source", "sources", {})
response.raise_for_status()
print(response.text)