/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.bos.dr.tests.clients.subsystem;

import java.util.List;
import java.util.Map;

import com.ericsson.bos.dr.tests.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subsystem {

    private long id;
    private String name;
    private String url;
    private SubsystemType subsystemType;
    private List<Map<String, Object>> connectionProperties;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SubsystemType getSubsystemType() {
        return subsystemType;
    }

    public void setSubsystemType(SubsystemType subsystemType) {
        this.subsystemType = subsystemType;
    }

    public List<Map<String, Object>> getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(List<Map<String, Object>> connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public KafkaConnectionProperties getKafkaConnectionProperties() {
        return JsonUtils.convert(connectionProperties.get(0), KafkaConnectionProperties.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubsystemType {

        private long id;
        private String type;
        private String alias;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }
}