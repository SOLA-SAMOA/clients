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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.services.boundary.wsclients.WSManager;

/**
 *
 * @author McDowell
 */
public class UnitParcelGroupBean extends AbstractIdBean {

    public static final String NAME_PROPERTY = "name";
    public static final String SELECTED_UNIT_PARCEL_PROPERTY = "selectedUnitParcel";
    public static final String SELECTED_PARCEL_PROPERTY = "selectedParcel";
    private String name;
    private SolaList<UnitParcelBean> unitParcelList;
    private SolaList<UnitParcelBean> parcelList;
    private transient UnitParcelBean selectedUnitParcel;
    private transient UnitParcelBean selectedParcel;

    public UnitParcelGroupBean() {
        super();
        unitParcelList = new SolaList();
        parcelList = new SolaList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        propertySupport.firePropertyChange(NAME_PROPERTY, oldValue, this.name);
    }

    public SolaList<UnitParcelBean> getParcelList() {
        return parcelList;
    }

    public void setParcelList(SolaList<UnitParcelBean> parcelList) {
        this.parcelList = parcelList;
    }

    public ObservableList<UnitParcelBean> getFilteredParcelList() {
        return parcelList.getFilteredList();
    }

    public UnitParcelBean getSelectedParcel() {
        return selectedParcel;
    }

    public void setSelectedParcel(UnitParcelBean value) {
        this.selectedParcel = value;
        propertySupport.firePropertyChange(SELECTED_PARCEL_PROPERTY, null, value);
    }

    public SolaList<UnitParcelBean> getUnitParcelList() {
        return unitParcelList;
    }

    public void setUnitParcelList(SolaList<UnitParcelBean> unitParcelList) {
        this.unitParcelList = unitParcelList;
    }

    public ObservableList<UnitParcelBean> getFilteredUnitParcelList() {
        return (ObservableList<UnitParcelBean>) sortUnitParcels(unitParcelList.getFilteredList());
    }

    public UnitParcelBean getSelectedUnitParcel() {
        return selectedUnitParcel;
    }

    public void setSelectedUnitParcel(UnitParcelBean value) {
        this.selectedUnitParcel = value;
        propertySupport.firePropertyChange(SELECTED_UNIT_PARCEL_PROPERTY, null, value);
    }

    /**
     * Retrieves a Unit Parcel Group (if one exists) based on the id for one of the parcels in the
     * Unit Parcel Group. Any parcel id can be used (e.g. underling parcel, accessory unit principal
     * unit or common property parcel.
     *
     * @return The unit parcel group or null if the parcel is not linked to a unit parcel group.
     */
    public static UnitParcelGroupBean getUnitParcelGroupByParcelId(String parcelId) {
        UnitParcelGroupBean result;
        result = TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getCadastreService().getUnitParcelGroupByParcelId(parcelId),
                UnitParcelGroupBean.class, null);
        return result;
    }
    
        /**
     * Retrieves a Unit Parcel Group (if one exists) based on the name of the group.
     *
     * @return The unit parcel group or null if the name does not match any unit parcel group.
     */
    public static UnitParcelGroupBean getUnitParcelGroupByName(String groupName) {
        UnitParcelGroupBean result;
        result = TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getCadastreService().getUnitParcelGroupByName(groupName),
                UnitParcelGroupBean.class, null);
        return result;
    }


    /**
     * Sorts the unit parcel list by the parcel type and lot number.
     *
     * @param unitParcelList the list of unit parcels to sort.
     */
    public List<UnitParcelBean> sortUnitParcels(List<UnitParcelBean> unitParcelList) {
        // Sort the parcels based on their type code and parcel number. 
        Collections.sort(unitParcelList, new Comparator<UnitParcelBean>() {

            @Override
            public int compare(UnitParcelBean unit1, UnitParcelBean unit2) {
                if (unit1.getTypeCode().equals(unit2.getTypeCode())) {
                    // Compare the Lot Number. Make sure the strings are of the same length for
                    // the comparison. 
                    String tmpStr1 = unit1.getNameFirstpart().substring(2).trim();
                    String tmpStr2 = unit2.getNameFirstpart().substring(2).trim();
                    while (tmpStr1.length() < tmpStr2.length()) {
                        tmpStr1 = "0" + tmpStr1;
                    }
                    while (tmpStr2.length() < tmpStr1.length()) {
                        tmpStr2 = "0" + tmpStr2;
                    }
                    return tmpStr1.compareTo(tmpStr2);
                }
                if (unit1.getTypeCode().equals(CadastreObjectTypeBean.CODE_COMMON_PROPERTY)
                        || unit2.getTypeCode().equals(CadastreObjectTypeBean.CODE_ACCESSORY_UNIT)) {
                    return -1;
                }
                return 1;
            }
        });
        return unitParcelList;
    }
}
