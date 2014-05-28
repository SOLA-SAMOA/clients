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
package org.sola.clients.beans.unittitle;

import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Represents summary information for properties associated with Unit Plan
 * developments. This bean cannot be used to save changes to the property - use
 * the BaUnitBean to perform save operations.
 *
 * @author soladev
 */
public class StrataPropertyBean extends AbstractBindingBean {

    public static final String ID_PROPERTY = "id";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String BA_UNIT_TYPE_PROPERTY = "baUnitType";
    public static final String NAME_PROPERTY = "name";
    public static final String NAME_FIRST_PART_PROPERTY = "nameFirstpart";
    public static final String NAME_LAST_PART_PROPERTY = "nameLastpart";
    public static final String STATUS_PROPERTY = "status";
    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String TRANSACTION_ID_PROPERTY = "transactionId";
    public static final String REGISTRATION_DATE_PROPERTY = "registrationDate";
    public static final String OFFICIAL_AREA_PROPERTY = "officialArea";
    public static final String UNIT_ENTITLEMENT_PROPERTY = "unitEntitlement";
    public static final String UNIT_PARCEL_TYPE_CODE_PROPERTY = "unitParcelTypeCode";
    public static final String UNIT_PARCEL_TYPE_PROPERTY = "unitParcelType";
    public static final String UNIT_PARCELS_PROPERTY = "unitParcels";
    public static final String PENDING_ACTION_CODE_PROPERTY = "pendingActionCode";
    private String id;
    private BaUnitTypeBean baUnitType;
    private String name;
    private String nameFirstpart;
    private String nameLastpart;
    private RegistrationStatusTypeBean status;
    private String transactionId;
    private Date registrationDate;
    private Integer officialArea;
    private Integer unitEntitlement;
    private CadastreObjectTypeBean unitParcelType;
    // Ticket #68 Flag to indicate when property will be canceled/terminated. 
    private String pendingActionCode;
    // Ticket #67 List of Unit Parcels for the Unit Title
    private String unitParcels;

    public StrataPropertyBean() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        String oldValue = this.id;
        this.id = value;
        propertySupport.firePropertyChange(ID_PROPERTY, oldValue, this.id);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        String oldValue = this.name;
        this.name = value;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, this.name);
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String value) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = value;
        propertySupport.firePropertyChange(NAME_FIRST_PART_PROPERTY, oldValue, this.nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String value) {
        String oldValue = this.nameLastpart;
        this.nameLastpart = value;
        propertySupport.firePropertyChange(NAME_LAST_PART_PROPERTY, oldValue, this.nameLastpart);
    }

    public Integer getOfficialArea() {
        return officialArea;
    }

    public void setOfficialArea(Integer value) {
        Integer oldValue = this.officialArea;
        this.officialArea = value;
        propertySupport.firePropertyChange(OFFICIAL_AREA_PROPERTY, oldValue, this.officialArea);
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date value) {
        Date oldValue = this.registrationDate;
        this.registrationDate = value;
        propertySupport.firePropertyChange(REGISTRATION_DATE_PROPERTY, oldValue, this.registrationDate);
    }

    public String getStatusCode() {
        if (status != null) {
            return status.getCode();
        } else {
            return null;
        }
    }

    public void setStatusCode(String value) {
        String oldValue = null;
        if (status != null) {
            oldValue = status.getCode();
        }
        if (WSManager.getInstance().getReferenceDataService() != null) {
            setStatus(CacheManager.getBeanByCode(
                    CacheManager.getRegistrationStatusTypes(), value));
            propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, value);
        }
    }

    public RegistrationStatusTypeBean getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatusTypeBean value) {
        if (this.status == null) {
            this.status = new RegistrationStatusTypeBean();
        }
        this.setJointRefDataBean(this.status, value, STATUS_PROPERTY);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String value) {
        String oldValue = this.transactionId;
        this.transactionId = value;
        propertySupport.firePropertyChange(TRANSACTION_ID_PROPERTY, oldValue, this.transactionId);
    }

    public String getTypeCode() {
        if (baUnitType != null) {
            return baUnitType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (baUnitType != null) {
            oldValue = baUnitType.getCode();
        }
        setBaUnitType(CacheManager.getBeanByCode(
                CacheManager.getBaUnitTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public BaUnitTypeBean getBaUnitType() {
        return baUnitType;
    }

    public void setBaUnitType(BaUnitTypeBean baUnitType) {
        if (this.baUnitType == null) {
            this.baUnitType = new BaUnitTypeBean();
        }
        this.setJointRefDataBean(this.baUnitType, baUnitType, BA_UNIT_TYPE_PROPERTY);
    }

    public Integer getUnitEntitlement() {
        return unitEntitlement;
    }

    public void setUnitEntitlement(Integer value) {
        Integer oldValue = this.unitEntitlement;
        this.unitEntitlement = value;
        propertySupport.firePropertyChange(UNIT_ENTITLEMENT_PROPERTY, oldValue, this.unitEntitlement);
    }

    public String getUnitParcelTypeCode() {
        if (unitParcelType != null) {
            return unitParcelType.getCode();
        } else {
            return null;
        }
    }

    public void setUnitParcelTypeCode(String value) {
        String oldValue = null;
        if (unitParcelType != null) {
            oldValue = unitParcelType.getCode();
        }
        setUnitParcelType(CacheManager.getBeanByCode(CacheManager.getCadastreObjectTypes(), value));
        propertySupport.firePropertyChange(UNIT_PARCEL_TYPE_CODE_PROPERTY, oldValue, value);
    }

    public CadastreObjectTypeBean getUnitParcelType() {
        return unitParcelType;
    }

    public void setUnitParcelType(CadastreObjectTypeBean value) {
        if (this.unitParcelType == null) {
            this.unitParcelType = new CadastreObjectTypeBean();
        }
        this.setJointRefDataBean(this.unitParcelType, value, UNIT_PARCEL_TYPE_PROPERTY);
    }

    public String getPendingActionCode() {
        return pendingActionCode;
    }

    public void setPendingActionCode(String value) {
        String oldValue = this.pendingActionCode;
        this.pendingActionCode = value;
        propertySupport.firePropertyChange(PENDING_ACTION_CODE_PROPERTY, oldValue, this.pendingActionCode);
    }

    public String getUnitParcels() {
        return unitParcels;
    }

    public void setUnitParcels(String value) {
        String oldValue = this.unitParcels;
        this.unitParcels = value;
        propertySupport.firePropertyChange(UNIT_PARCELS_PROPERTY, oldValue, this.unitParcels);
    }

    // Identifies if the property is a Strata Property or not. Note that underlying
    // properties are also included in the Strata Property List. 
    public boolean isStrataProperty() {
        return BaUnitTypeBean.TYPE_CODE_STRATA_UNIT.equals(this.getTypeCode());
    }
}
