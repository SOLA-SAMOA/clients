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
package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.ReadGeometryException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.sola.clients.beans.AbstractIdBean;

/**
 * Bean used for storage of SpatialUnitChange details.
 */
public class SpatialUnitChangeBean extends AbstractIdBean {

    public static final String GEOM_PROPERTY = "geom";
    public static final String LABEL_PROPERTY = "label";
    public static final String LEVEL_NAME_PROPERTY = "levelName";
    public static final String DELETE_ON_APPROVAL_PROPERTY = "deleteOnApproval";
    public static final String SPATIAL_UNIT_ID_PROPERTY = "spatialUnitId";
    public static final String NEW_FEATURE_PROPERTY = "newFeature";
    public static final String SELECTED_PROPERTY = "selected";
    public static final String NORTHING_PROPERTY = "northing";
    public static final String EASTING_PROPERTY = "easting";
    private byte[] geom = null;
    private String label;
    private String levelName;
    private boolean deleteOnApproval;
    private String spatialUnitId;
    private boolean newFeature;
    private boolean selected;
    private Geometry mergedGeom;
    private Double northing = null;
    private Double easting = null;
    private Boolean isPoint = null;
    private Geometry geometry = null;

    public SpatialUnitChangeBean() {
        super();
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] newValue) {
        byte[] oldValue = this.geom;
        this.geom = newValue;
        propertySupport.firePropertyChange(GEOM_PROPERTY, oldValue, newValue);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String newValue) {
        String oldValue = this.label;
        this.label = newValue;
        propertySupport.firePropertyChange(LABEL_PROPERTY, oldValue, newValue);
    }

    public String getSpatialUnitId() {
        return spatialUnitId;
    }

    public void setSpatialUnitId(String newValue) {
        String oldValue = this.spatialUnitId;
        this.spatialUnitId = newValue;
        propertySupport.firePropertyChange(SPATIAL_UNIT_ID_PROPERTY, oldValue, newValue);
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String newValue) {
        String oldValue = this.levelName;
        this.levelName = newValue;
        propertySupport.firePropertyChange(LEVEL_NAME_PROPERTY, oldValue, newValue);
    }

    public boolean isDeleteOnApproval() {
        return deleteOnApproval;
    }

    public void setDeleteOnApproval(boolean newValue) {
        boolean oldValue = this.deleteOnApproval;
        this.deleteOnApproval = newValue;
        propertySupport.firePropertyChange(DELETE_ON_APPROVAL_PROPERTY, oldValue, newValue);
    }

    public boolean isNewFeature() {
        newFeature = getSpatialUnitId() == null;
        return newFeature;
    }

    public void setNewFeature(boolean newValue) {
        boolean oldValue = this.newFeature;
        this.newFeature = newValue;
        propertySupport.firePropertyChange(NEW_FEATURE_PROPERTY, oldValue, newValue);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean newValue) {
        boolean oldValue = this.selected;
        this.selected = newValue;
        propertySupport.firePropertyChange(SELECTED_PROPERTY, oldValue, newValue);
    }

    public Geometry getMergedGeom() {
        return mergedGeom;
    }

    public void setMergedGeom(Geometry mergedGeom) {
        this.mergedGeom = mergedGeom;
    }

    public Geometry getGeometry() {
        if (geometry == null && geom != null) {
            geometry = GeometryUtility.getGeometryFromWkb(geom);
        }
        return geometry;
    }

    public boolean isPoint() {
        if (isPoint == null) {
            try {
                isPoint = geom == null ? false : GeometryUtility.isGeomType(geom, Geometries.POINT);
            } catch (ReadGeometryException ex) {
                org.sola.common.logging.LogUtility.log(
                        "SpatialUnitChangeBean.isPoint - Failed to read geometry", ex);
                isPoint = false;
            }
        }
        return isPoint;
    }

    public Double getNorthing() {
        if (this.isPoint() && northing == null) {
            northing = getGeometry().getCoordinate().y;

        }
        return northing;
    }

    public void setNorthing(Double newValue) {
        if (this.isPoint()) {
            Double oldValue = this.northing;
            this.northing = newValue;
            if (newValue != null) {
                geometry = GeometryUtility.getGeometryFactory().createPoint(
                        new Coordinate(getEasting(), newValue));
                setGeom(GeometryUtility.getWkbFromGeometry(geometry));
                propertySupport.firePropertyChange(NORTHING_PROPERTY, oldValue, newValue);
            }
        }
    }

    public Double getEasting() {
        if (this.isPoint() && easting == null) {
            easting = getGeometry().getCoordinate().x;
        }
        return easting;
    }

    public void setEasting(Double newValue) {
        if (this.isPoint()) {
            Double oldValue = this.easting;
            this.easting = newValue;
            if (newValue != null) {
                geometry = GeometryUtility.getGeometryFactory().createPoint(
                        new Coordinate(newValue, getNorthing()));
                setGeom(GeometryUtility.getWkbFromGeometry(geometry));
                propertySupport.firePropertyChange(EASTING_PROPERTY, oldValue, newValue);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SpatialUnitChangeBean other = (SpatialUnitChangeBean) obj;
        if (this.spatialUnitId != null && other.spatialUnitId != null) {
            return this.spatialUnitId.equals(other.spatialUnitId);
        }
        if (this.spatialUnitId != null) {
            return this.spatialUnitId.equals(other.getId());
        }
        if (this.getId() != null && other.spatialUnitId != null) {
            return this.getId().equals(other.spatialUnitId);
        }
        if (this.getId() != null) {
            return this.getId().equals(other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        hash = 89 * hash + (this.spatialUnitId != null ? this.spatialUnitId.hashCode() : 0);
        return hash;
    }
}
