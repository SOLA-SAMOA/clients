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
package org.sola.clients.beans.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.unittitle.UnitParcelGroupBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.transaction.TransactionUnitParcelsTO;

/**
 *
 * @author soladev
 */
public class TransactionUnitParcelsBean extends AbstractIdBean {

    public static final String FROM_SERVICE_ID_PROPERTY = "fromServiceId";
    public static final String APPROVAL_DATETIME_PROPERTY = "approvalDatetime";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String UNIT_PARCEL_GROUP_PROPERTY = "unitParcelGroup";
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    private String fromServiceId;
    private Date approvalDatetime;
    private String statusCode;
    private UnitParcelGroupBean unitParcelGroup;
    private TransactionSourceBean selectedSource;
    private SolaObservableList<TransactionSourceBean> transactionSourceList;

    public TransactionUnitParcelsBean() {
        super();
        transactionSourceList = new SolaObservableList<TransactionSourceBean>();
        unitParcelGroup = new UnitParcelGroupBean();
    }

    public Date getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(Date approvalDatetime) {
        Date oldValue = this.approvalDatetime;
        this.approvalDatetime = approvalDatetime;
        propertySupport.firePropertyChange(APPROVAL_DATETIME_PROPERTY, oldValue, this.approvalDatetime);
    }

    public String getFromServiceId() {
        return fromServiceId;
    }

    public void setFromServiceId(String fromServiceId) {
        String oldValue = this.fromServiceId;
        this.fromServiceId = fromServiceId;
        propertySupport.firePropertyChange(FROM_SERVICE_ID_PROPERTY, oldValue, this.fromServiceId);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        String oldValue = this.statusCode;
        this.statusCode = statusCode;
        propertySupport.firePropertyChange(FROM_SERVICE_ID_PROPERTY, oldValue, this.statusCode);
    }

    public SolaObservableList<TransactionSourceBean> getTransactionSourceList() {
        return transactionSourceList;
    }

    public void setTransactionSourceList(SolaObservableList<TransactionSourceBean> transactionSourceList) {
        this.transactionSourceList = transactionSourceList;
    }

    public TransactionSourceBean getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(TransactionSourceBean selectedSource) {
        this.selectedSource = selectedSource;
        propertySupport.firePropertyChange(UNIT_PARCEL_GROUP_PROPERTY, null, this.selectedSource);
    }

    public UnitParcelGroupBean getUnitParcelGroup() {
        return unitParcelGroup;
    }

    public void setUnitParcelGroup(UnitParcelGroupBean unitParcelGroup) {
        UnitParcelGroupBean oldValue = this.unitParcelGroup;
        this.unitParcelGroup = unitParcelGroup;
        propertySupport.firePropertyChange(UNIT_PARCEL_GROUP_PROPERTY, oldValue, this.unitParcelGroup);
    }

    /**
     * Saves the transaction into the database.
     *
     * @throws Exception
     */
    public List<ValidationResultBean> saveTransaction() {
        List<ValidationResultBean> result;
        TransactionUnitParcelsTO to = TypeConverters.BeanToTrasferObject(this, TransactionUnitParcelsTO.class);
        result = TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCadastreService().saveUnitParcels(to),
                ValidationResultBean.class, null);
        return result;
    }

    /**
     * Reloads transaction from the database.
     */
    public void reload() {
        TransactionUnitParcelsTO transactionTO = WSManager.getInstance().getCadastreService().getTransactionUnitParcels(this.getFromServiceId());
        TypeConverters.TransferObjectToBean(transactionTO, TransactionUnitParcelsBean.class, this);
        if (this.getUnitParcelGroup() == null) {
            this.setUnitParcelGroup(new UnitParcelGroupBean());
        } else {
            this.getUnitParcelGroup().getFilteredUnitParcelList();
        }

    }
}
