package com.ericsson.bos.dr.tests.clients.alarms;

public record Alarm(String alarmName, String serviceName, String eventTime, String faultyResource, String description,
                    String severity, Integer expires) {
}
