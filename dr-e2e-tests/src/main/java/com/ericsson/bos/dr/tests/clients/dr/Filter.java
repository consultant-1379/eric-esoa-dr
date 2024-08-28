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
package com.ericsson.bos.dr.tests.clients.dr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Filter {

    private String name;
    private ReconcileAction reconcileAction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReconcileAction getReconcileAction() {
        return reconcileAction;
    }

    public void setReconcileAction(ReconcileAction reconcileAction) {
        this.reconcileAction = reconcileAction;
    }

    public static class ReconcileAction {
        private String name;
        private String status;
        private String command;
        private String commandOutput;
        private String errorMessage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getCommandOutput() {
            return commandOutput;
        }

        public void setCommandOutput(String commandOutput) {
            this.commandOutput = commandOutput;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
