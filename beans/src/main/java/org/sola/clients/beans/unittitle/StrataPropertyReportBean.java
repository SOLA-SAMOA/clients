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

import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Used to provide details about Strata Properties for reporting purposes.
 */
public class StrataPropertyReportBean extends AbstractBindingListBean {

    private List<StrataPropertyBean> strataPropertyList;
    private BaUnitBean commonProperty;
    private String name;

    public StrataPropertyReportBean() {
        super();
        strataPropertyList = new ArrayList<StrataPropertyBean>();
    }

    /**
     * Overloaded constructor for the report bean that load the strata property
     * and common property parcel
     *
     * @param unitParcelGroupName
     * @param baUnitIds
     */
    public StrataPropertyReportBean(String unitParcelGroupName, List<String> baUnitIds) {
        this();
        name = unitParcelGroupName;
        getStrataProperties(unitParcelGroupName, baUnitIds);
        loadCommonProperty();
    }

    /**
     * Retrieves a summary of the properties that are part of the specified unit
     * parcel group. Also retrieves a summary of the properties noted in the
     * baUnitIds list. Typically this list will represent the underlying
     * properties of the unit development and is included as a parameter in case
     * the underlying property is not linked to a cadastre_object.
     *
     * @param unitParcelGroupName The name of the parcel group (if known)
     * @param baUnitIds The identifier for any of the BA Units linked to the
     * unit parcel group or the identifier for any BA Units that should be part
     * of the unit development.
     */
    private void getStrataProperties(String unitParcelGroupName, List<String> baUnitIds) {
        strataPropertyList.clear();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().getStrataProperties(unitParcelGroupName, baUnitIds),
                StrataPropertyBean.class, (List) strataPropertyList);
    }

    /**
     * Loads the common property BaUnit based on the StrataProperty Common
     * Property record.
     */
    private void loadCommonProperty() {
        if (strataPropertyList.size() > 0) {
            for (StrataPropertyBean bean : strataPropertyList) {
                if (CadastreObjectTypeBean.CODE_COMMON_PROPERTY.equals(bean.getUnitParcelTypeCode())) {
                    commonProperty = BaUnitBean.getBaUnitsById(bean.getId());
                    break;
                }
            }
        }
    }

    /**
     * Used to simplify access to the methods on this bean from the Jasper
     * report
     *
     * @return this
     */
    public StrataPropertyReportBean getReportBean() {
        return this;
    }

    public BaUnitBean getCommonProperty() {
        return commonProperty;
    }

    public void setCommonProperty(BaUnitBean commonProperty) {
        this.commonProperty = commonProperty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Only returns the Current Strata Properties - filters out any underlying
     * properties.
     *
     * @return
     */
    public List<StrataPropertyBean> getCurrentStrataPropertyList() {
        List<StrataPropertyBean> result = new ArrayList<StrataPropertyBean>();
        for (StrataPropertyBean bean : strataPropertyList) {
            if (bean.isStrataProperty() && StatusConstants.CURRENT.equals(bean.getStatusCode())) {
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * Obtains the list of prior titles for the Unit Development.
     */
    public String getPriorTitles() {
        String result = "";
        if (strataPropertyList.size() > 0) {
            for (StrataPropertyBean bean : strataPropertyList) {
                if (!bean.isStrataProperty()
                        && !StatusConstants.HISTORIC.equals(bean.getStatusCode())) {
                    result = ", " + bean.getName();
                }
            }
            if (result.length() > 2) {
                result = result.substring(2).trim();
            }
        }
        return result;
    }

    /**
     * Determines the total of all unit entitlements for the Strata Properties.
     */
    public Integer getEntitlementTotal() {
        Integer result = 0;
        for (StrataPropertyBean bean : getCurrentStrataPropertyList()) {
            if (bean.getUnitEntitlement() != null && bean.getUnitEntitlement() > 0) {
                result += bean.getUnitEntitlement();
            }
        }
        return result;
    }

    /**
     * Obtains the total area for the unit properties to report as the area of
     * the Unit Development.
     */
    public Integer getAreaTotal() {
        Integer result = 0;
        for (StrataPropertyBean bean : getCurrentStrataPropertyList()) {
            if (bean.getOfficialArea() != null && bean.getOfficialArea() > 0) {
                result += bean.getOfficialArea();
            }
        }
        return result;
    }
}
