import requests, json, sys
from bos.dr.clients import restservice

target_subsystem=sys.argv[1]
source_id=sys.argv[2]
data={"inputs": {"id": source_id}}
response = restservice.run(target_subsystem, "wiremock_source", "source", data)
response.raise_for_status()
print(response.text)