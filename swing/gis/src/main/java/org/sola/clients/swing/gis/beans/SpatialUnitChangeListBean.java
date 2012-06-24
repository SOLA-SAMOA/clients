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
package org.sola.clients.swing.gis.beans;

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * Manages a list of {@linkplain SpatialUnitChangeBean} to simplify binding the list of beans to a
 * form. 
 */
public class SpatialUnitChangeListBean extends AbstractBindingBean {

    public static final String SELECTED_SPATIAL_UNIT_CHANGE_PROPERTY = "selectedSpatialUnitChange";
    private SolaObservableList<SpatialUnitChangeBean> spatialUnitChanges;
    private SpatialUnitChangeBean selectedSpatialUnitChange;

    public SpatialUnitChangeListBean() {
        super();
        spatialUnitChanges = new SolaObservableList<SpatialUnitChangeBean>();
    }

    public SolaObservableList<SpatialUnitChangeBean> getSpatialUnitChanges() {
        return spatialUnitChanges;
    }

    public SpatialUnitChangeBean getSelectedSpatialUnitChange() {
        return selectedSpatialUnitChange;
    }

    public void setSelectedSpatialUnitChange(SpatialUnitChangeBean newValue) {
        SpatialUnitChangeBean oldValue = this.selectedSpatialUnitChange;
        this.selectedSpatialUnitChange = newValue;
        propertySupport.firePropertyChange(SELECTED_SPATIAL_UNIT_CHANGE_PROPERTY, oldValue, newValue);
    }
}
