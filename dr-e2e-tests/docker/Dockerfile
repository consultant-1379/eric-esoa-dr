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

FROM armdocker.rnd.ericsson.se/proj-orchestration-so/so-base-openjdk17:1.0.45-1
COPY target/eric-esoa-dr-e2e-tests.jar /dr-e2e-tests/
COPY docker/entryPoint.sh /dr-e2e-tests/entryPoint.sh
RUN chmod +x /dr-e2e-tests/*.sh
RUN curl -o /aspectjweaver.jar https://arm1s11-eiffel052.eiffel.gic.ericsson.se:8443/nexus/content/groups/eo-repositories/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.jar
ENV ASPECTJ_JAR="/aspectjweaver.jar"
ENTRYPOINT ["/dr-e2e-tests/entryPoint.sh"]