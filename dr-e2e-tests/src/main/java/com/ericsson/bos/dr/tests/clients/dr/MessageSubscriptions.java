package com.ericsson.bos.dr.tests.clients.dr;

import java.util.List;

public record MessageSubscriptions(int totalCount, List<MessageSubscription> items) {
}
