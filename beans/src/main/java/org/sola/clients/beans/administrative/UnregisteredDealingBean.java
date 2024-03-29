/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative;

import org.sola.clients.beans.AbstractBindingBean;

/**
 * Entity representing the unregistered dealing details. Used by the Computer Folio report to list
 * the unregistered dealings on a property
 */
public class UnregisteredDealingBean extends AbstractBindingBean {

    public static final String NIL_DEALING = "Nil";
    public static final String UNREGISTERED_DEALING_TEXT = "Unregistered Dealings";
     public static final String UNREGISTERED_SERVICE_TEXT = "Unregistered Service";
    private String baUnitId;
    private String appNr;
    private String appId;
    private String pendingServices;

    public UnregisteredDealingBean() {
        super();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppNr() {
        return appNr;
    }

    public void setAppNr(String appNr) {
        this.appNr = appNr;
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getPendingServices() {
        return pendingServices;
    }

    public void setPendingServices(String pendingServices) {
        this.pendingServices = pendingServices;
    }

    /**
     * Retrieve the pending notations
     * @return 
     */
    public String getNotation() {
        String result;
        if (NIL_DEALING.equals(pendingServices)) {
            result = String.format("%s - %s", UNREGISTERED_DEALING_TEXT,
                    NIL_DEALING);
        } else {
            result = String.format("%s : %s %s", UNREGISTERED_SERVICE_TEXT,
                    pendingServices == null ? "" : pendingServices,
                    appNr == null ? "" : appNr).trim();
        }
        return result;
    }
}
