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

import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.CertificatePrintTO;

/**
 *
 * @author soladev
 */
public class CertificatePrintBean extends AbstractBindingBean {

    public static final String CERT_TYPE_PROPERTY = "certificateType";
    public static final String PRINT_TIME_PROPERTY = "printTime";
    public static final String PRINT_USER_PROPERTY = "printUser";
    public static final String COMMENT_PROPERTY = "comment";
    private String id;
    private String baUnitId;
    private String certificateType;
    private Date printTime;
    private String printUser;
    private String comment;

    public CertificatePrintBean() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String val) {
        String old = certificateType;
        certificateType = val;
        propertySupport.firePropertyChange(CERT_TYPE_PROPERTY, old, val);
    }

    public Date getPrintTime() {
        return printTime;
    }

    public void setPrintTime(Date val) {
        Date old = printTime;
        printTime = val;
        propertySupport.firePropertyChange(PRINT_TIME_PROPERTY, old, val);
    }

    public String getPrintUser() {
        return printUser;
    }

    public void setPrintUser(String val) {
        String old = printUser;
        printUser = val;
        propertySupport.firePropertyChange(PRINT_USER_PROPERTY, old, val);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String val) {
        String old = comment;
        comment = val;
        propertySupport.firePropertyChange(COMMENT_PROPERTY, old, val);
    }

    public void saveCertificatePrint() {
        CertificatePrintTO to = TypeConverters.BeanToTrasferObject(this, CertificatePrintTO.class);
        to = WSManager.getInstance().getAdministrative().saveCertificatePrint(to);
        TypeConverters.TransferObjectToBean(to, CertificatePrintBean.class, this);
    }
}
