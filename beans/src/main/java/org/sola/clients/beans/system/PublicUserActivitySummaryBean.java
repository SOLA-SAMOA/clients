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
package org.sola.clients.beans.system;

import org.sola.clients.beans.application.*;
import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.common.StringUtility;

/**
 * Bean containing work summary information for the Lodgement Report
 *
 */
public class PublicUserActivitySummaryBean extends AbstractBindingBean {

    private String publicUserId;
    private String publicUserName;
    private Date activityDay;
    private String receiptNumber;
    private int loginNumber;
    private int mapPrintNumber;
    private String mapPrintComments;
    private int docViewNumber;
    private String docViewComments;
    private int docPrintNumber;
    private String docPrintComments;
    private Date maxActivityTime;
    private Date minActivityTime;
    private String docsList; 

    public PublicUserActivitySummaryBean() {
        super();
    }

    public String getPublicUserId() {
        return publicUserId;
    }

    public void setPublicUserId(String publicUserId) {
        this.publicUserId = publicUserId;
    }

    public String getPublicUserName() {
        return publicUserName;
    }

    public void setPublicUserName(String publicUserName) {
        this.publicUserName = publicUserName;
    }

    public Date getActivityDay() {
        return activityDay;
    }

    public void setActivityDay(Date activityDay) {
        this.activityDay = activityDay;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public int getLoginNumber() {
        return loginNumber;
    }

    public void setLoginNumber(int loginNumber) {
        this.loginNumber = loginNumber;
    }

    public int getMapPrintNumber() {
        return mapPrintNumber;
    }

    public void setMapPrintNumber(int mapPrintNumber) {
        this.mapPrintNumber = mapPrintNumber;
    }

    public String getMapPrintComments() {
        return mapPrintComments;
    }

    public void setMapPrintComments(String mapPrintComments) {
        this.mapPrintComments = mapPrintComments;
    }

    public int getDocViewNumber() {
        return docViewNumber;
    }

    public void setDocViewNumber(int docViewNumber) {
        this.docViewNumber = docViewNumber;
    }

    public String getDocViewComments() {
        return docViewComments;
    }

    public void setDocViewComments(String docViewComments) {
        this.docViewComments = docViewComments;
    }

    public int getDocPrintNumber() {
        return docPrintNumber;
    }

    public void setDocPrintNumber(int docPrintNumber) {
        this.docPrintNumber = docPrintNumber;
    }

    public String getDocPrintComments() {
        return docPrintComments;
    }

    public void setDocPrintComments(String docPrintComments) {
        this.docPrintComments = docPrintComments;
    }

    public Date getMaxActivityTime() {
        return maxActivityTime;
    }

    public void setMaxActivityTime(Date maxActivityTime) {
        this.maxActivityTime = maxActivityTime;
    }

    public Date getMinActivityTime() {
        return minActivityTime;
    }

    public void setMinActivityTime(Date minActivityTime) {
        this.minActivityTime = minActivityTime;
    }

    public String getDocsList() {
        docsList = (StringUtility.isEmpty(docViewComments) ? "" : "Docs Viewed: " + docViewComments + "; ")
                + (StringUtility.isEmpty(docPrintComments) ? "" : "Docs Printed: " + docPrintComments); 
        return docsList;
    }

    public void setDocsList(String docsList) {
        this.docsList = docsList;
    }
    
    
  
    /**
     * Returns collection of {@link WorkSummaryBean} objects. This method is used by Jasper report
     * designer to extract properties of application bean to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        PublicUserActivitySummaryBean bean = new PublicUserActivitySummaryBean();
        collection.add(bean);
        return collection;
    }
}
