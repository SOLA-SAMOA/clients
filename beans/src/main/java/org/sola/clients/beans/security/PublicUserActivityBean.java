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
package org.sola.clients.beans.security;

import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.admin.PublicUserActivityTO;

/**
 * Represents the <b>public user activity</b> such as a login document print or
 * map print activity.
 */
public class PublicUserActivityBean extends AbstractIdBean {

    public final static String ACTIVITY_TYPE_PROPERTY = "activityType";
    public final static String ACTIVITY_TIME_PROPERTY = "activityTime";
    public final static String RECEIPT_NUMBER_PROPERTY = "receiptNumber";
    public final static String COMMENT_PROPERTY = "comment";
    public final static String PUBLIC_USER_PROPERTY = "publicUser";
    
    public final static String LOGIN_ACTIVITY_TYPE = "login";
    public final static String VIEW_DOCUMENT_ACTIVITY_TYPE = "docView";
    public final static String PRINT_DOCUMENT_ACTIVITY_TYPE = "docPrint";
    public final static String MAP_PRINT_ACTIVITY_TYPE = "mapPrint";

    private Date activityTime;
    private String activityType;
    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_RECEIPT_NUMBER, payload = Localized.class)
    private String receiptNumber;
    private String comment;
    private String publicUser;

    public PublicUserActivityBean() {
        super();
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date value) {
        Date oldValue = activityTime;
        activityTime = value;
        propertySupport.firePropertyChange(ACTIVITY_TIME_PROPERTY, oldValue, value);
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String value) {
        String oldValue = activityType;
        activityType = value;
        propertySupport.firePropertyChange(ACTIVITY_TYPE_PROPERTY, oldValue, value);
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String value) {
        String oldValue = receiptNumber;
        receiptNumber = value;
        propertySupport.firePropertyChange(RECEIPT_NUMBER_PROPERTY, oldValue, value);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        String oldValue = comment;
        comment = value;
        propertySupport.firePropertyChange(COMMENT_PROPERTY, oldValue, value);
    }

    public String getPublicUser() {
        return publicUser;
    }

    public void setPublicUser(String value) {
        String oldValue = publicUser;
        publicUser = value;
        propertySupport.firePropertyChange(PUBLIC_USER_PROPERTY, oldValue, value);
    }

    public void save() {
        PublicUserActivityTO puaTO = TypeConverters.BeanToTrasferObject(this, PublicUserActivityTO.class);
        puaTO = WSManager.getInstance().getAdminService().savePublicUserActivity(puaTO);
        TypeConverters.TransferObjectToBean(puaTO, PublicUserActivityBean.class, this);
    }

}
