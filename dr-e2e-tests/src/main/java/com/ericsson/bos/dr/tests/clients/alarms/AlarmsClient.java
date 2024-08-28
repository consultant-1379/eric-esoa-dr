package com.ericsson.bos.dr.tests.clients.alarms;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Environment;

public class AlarmsClient {

    private static final String ALARMS_URL = "/ah/api/v0/alarms";

    public HttpResponse getAlarms() {
        return HttpOperations.get(Environment.ALARM_HANDLER_HOST.concat(ALARMS_URL));
    }
}
