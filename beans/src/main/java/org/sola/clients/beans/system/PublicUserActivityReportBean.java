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

import java.util.Date;
import org.sola.clients.beans.application.*;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.admin.PublicUserActivitySummaryParamsTO;
import org.sola.webservices.admin.PublicUserActivitySummaryTO;
import org.sola.webservices.transferobjects.casemanagement.LodgementViewTO;

/**
 * Contains summary properties of the LodgementView object. Could be populated
 * from the {@link LodgementViewTO} object.<br /> For more information see UC
 * <b>Lodgement Report</b> schema.
 */
public class PublicUserActivityReportBean extends AbstractBindingBean {

    private ObservableList<PublicUserActivitySummaryBean> activitySummaryList;

    public PublicUserActivityReportBean() {
        super();
        activitySummaryList = ObservableCollections.observableList(new LinkedList<PublicUserActivitySummaryBean>());
    }

    public ObservableList<PublicUserActivitySummaryBean> getActivitySummaryList() {
        return activitySummaryList;
    }

    public void setActivitySummaryList(ObservableList<PublicUserActivitySummaryBean> activitySummaryList) {
        this.activitySummaryList = activitySummaryList;
    }

    /**
     * Returns collection of {@link ApplicationBean} objects. This method is
     * used by Jasper report designer to extract properties of application bean
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        PublicUserActivityReportBean bean = new PublicUserActivityReportBean();
        collection.add(bean);
        return collection;
    }

    /**
     *
     *
     * @param params
     */
    public void loadActivitySummary(Date fromDate, Date toDate, String publicUser) {
        PublicUserActivitySummaryParamsTO paramsTO = new PublicUserActivitySummaryParamsTO();
        paramsTO.setFromDate(TypeConverters.DateToXMLDate(fromDate));
        paramsTO.setToDate(TypeConverters.DateToXMLDate(toDate));
        paramsTO.setPublicUser(publicUser);

        List<PublicUserActivitySummaryTO> activitySummaryTOList
                = WSManager.getInstance().getAdminService().getPublicUserActivitySummary(paramsTO);

        TypeConverters.TransferObjectListToBeanList(activitySummaryTOList,
                PublicUserActivitySummaryBean.class, (List) activitySummaryList);

    }
}
