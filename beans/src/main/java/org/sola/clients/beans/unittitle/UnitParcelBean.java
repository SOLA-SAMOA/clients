/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.unittitle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.cadastre.SpatialValueAreaBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Used to represent a unit parcel from a Unit Plan Development
 */
public class UnitParcelBean extends CadastreObjectBean {

    public static final String DELETE_ON_APPROVAL_PROPERTY = "deleteOnApproval";
    public static final String UNIT_PARCEL_STATUS_CODE_PROPERTY = "unitParcelStatusCode";
    public static final String UNIT_PARCEL_STATUS_PROPERTY = "unitParcelStatus";
    public static final String OFFICIAL_AREA_PROPERTY = "officialArea";
    private boolean deleteOnApproval;
    private RegistrationStatusTypeBean unitParcelStatus;
    private List<SpatialValueAreaBean> spatialValueAreaList;
    private transient String officialArea;

    public UnitParcelBean() {
        super();
    }

    /**
     * Retrieves the official area for the unit parcel with the option to add a new official area if
     * necessary.
     *
     * @param addBean if true, add an official area bean to the unit parcel if it does not already
     * exist.
     * @return The official area bean or null if it doesn't exist and is not created.
     */
    private SpatialValueAreaBean getOfficialAreaBean(boolean addBean) {
        SpatialValueAreaBean result = null;
        if (spatialValueAreaList != null) {
            for (SpatialValueAreaBean bean : spatialValueAreaList) {
                if (SpatialValueAreaBean.TYPE_OFFICIAL.equals(bean.getTypeCode())) {
                    result = bean;
                    break;
                }
            }
        }

        // Create the official area bean for the unit parcel
        if (result == null && addBean) {
            result = new SpatialValueAreaBean();
            result.setTypeCode(SpatialValueAreaBean.TYPE_OFFICIAL);
            if (spatialValueAreaList == null) {
                spatialValueAreaList = new ArrayList<SpatialValueAreaBean>();
            }
            spatialValueAreaList.add(result);
        }
        return result;
    }

    public boolean isDeleteOnApproval() {
        return deleteOnApproval;
    }

    public void setDeleteOnApproval(boolean deleteOnApproval) {
        boolean oldValue = this.deleteOnApproval;
        this.deleteOnApproval = deleteOnApproval;
        propertySupport.firePropertyChange(DELETE_ON_APPROVAL_PROPERTY, oldValue, this.deleteOnApproval);
    }

    public List<SpatialValueAreaBean> getSpatialValueAreaList() {
        return spatialValueAreaList;
    }

    public void setSpatialValueAreaList(List<SpatialValueAreaBean> spatialValueAreaList) {
        this.spatialValueAreaList = spatialValueAreaList;
    }

    public String getOfficialArea() {
        if (officialArea == null) {
            SpatialValueAreaBean bean = getOfficialAreaBean(false);
            if (bean != null && bean.getSize() != null) {
                officialArea = bean.getSize().toBigInteger().toString();
            }
        }
        return officialArea;
    }

    /**
     * Sets the official area with the specified value. If the new value is null or empty, the
     * setter attempts to remove the official area from the unit parcel.
     *
     * @param value
     */
    public void setOfficialArea(String value) {
        String oldValue = this.officialArea;
        if (value == null || value.trim().isEmpty()) {
            this.officialArea = "";
            SpatialValueAreaBean bean = getOfficialAreaBean(false);
            if (bean != null) {
                if (bean.isNew()) {
                    this.spatialValueAreaList.remove(bean);
                } else {
                    bean.setEntityAction(EntityAction.DELETE);
                }
            }
        } else {
            // Check if this is a valid area value and set the official area bean accordingly. If the
            // area value is not valid, reject the value. 
            oldValue = null;
            try {
                Integer.parseInt(value);
                oldValue = this.officialArea;
                this.officialArea = value;
                SpatialValueAreaBean bean = getOfficialAreaBean(true);
                bean.setSize(new BigDecimal(value));
                bean.setEntityAction(null);
            } catch (NumberFormatException nfe) {
            }
        }
        propertySupport.firePropertyChange(OFFICIAL_AREA_PROPERTY, oldValue, this.officialArea);
    }

    public String getUnitParcelStatusCode() {
        if (unitParcelStatus != null) {
            return unitParcelStatus.getCode();
        } else {
            return null;
        }
    }

    public void setUnitParcelStatusCode(String statusCode) {
        String oldValue = null;
        if (unitParcelStatus != null) {
            oldValue = unitParcelStatus.getCode();
        }
        if (WSManager.getInstance().getReferenceDataService() != null) {
            setUnitParcelStatus(CacheManager.getBeanByCode(
                    CacheManager.getRegistrationStatusTypes(), statusCode));
            propertySupport.firePropertyChange(UNIT_PARCEL_STATUS_CODE_PROPERTY, oldValue, statusCode);
        }
    }

    public RegistrationStatusTypeBean getUnitParcelStatus() {
        return unitParcelStatus;
    }

    public void setUnitParcelStatus(RegistrationStatusTypeBean unitParcelStatus) {
        if (this.unitParcelStatus == null) {
            this.unitParcelStatus = new RegistrationStatusTypeBean();
        }
        this.setJointRefDataBean(this.unitParcelStatus, unitParcelStatus, UNIT_PARCEL_STATUS_PROPERTY);
    }
}
