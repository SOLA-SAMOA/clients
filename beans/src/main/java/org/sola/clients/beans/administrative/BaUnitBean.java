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
package org.sola.clients.beans.administrative;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.validation.Valid;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.referencedata.TypeActionBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.utils.RrrComparatorByRegistrationDate;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;
import org.sola.webservices.transferobjects.search.SpatialSearchResultTO;

/**
 * Contains properties and methods to manage <b>BA Unit</b> object of the domain
 * model. Could be populated from the {@link BaUnitTO} object.
 */
public class BaUnitBean extends BaUnitSummaryBean {

    private class RrrListListener implements ObservableListListener, Serializable {

        @Override
        public void listElementsAdded(ObservableList ol, int i, int i1) {
            setEstateType();
            RrrBean rrrBean = (RrrBean) ol.get(i);
            for (RrrShareBean shareBean : getShares(rrrBean)) {
                rrrSharesList.add(createShareWithStatus(shareBean, rrrBean));
            }
        }

        @Override
        public void listElementsRemoved(ObservableList ol, int i, List list) {
            setEstateType();
            for (Object bean : list) {
                RrrBean rrrBean = (RrrBean) bean;
                for (RrrShareBean shareBean : getShares(rrrBean)) {
                    for (RrrShareWithStatus shareWithStatusBean : rrrSharesList) {
                        if (shareWithStatusBean.getRrrShare().equals(shareBean)) {
                            rrrSharesList.remove(shareWithStatusBean);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void listElementReplaced(ObservableList ol, int i, Object o) {
            setEstateType();
            refreshSharesList();
        }

        @Override
        public void listElementPropertyChanged(ObservableList ol, int i) {
            setEstateType();
        }

        private void refreshSharesList() {
            rrrSharesList.clear();
            for (RrrBean rrrBean : rrrList.getFilteredList()) {
                for (RrrShareBean shareBean : getShares(rrrBean)) {
                    rrrSharesList.add(createShareWithStatus(shareBean, rrrBean));
                }
            }
        }

        private RrrShareWithStatus createShareWithStatus(RrrShareBean shareBean, RrrBean rrrBean) {
            RrrShareWithStatus shareWithStatusBean = new RrrShareWithStatus();
            shareWithStatusBean.setRrrShare(shareBean);
            if (rrrBean.getStatus() != null) {
                shareWithStatusBean.setStatus(rrrBean.getStatus().getDisplayValue());
            }
            return shareWithStatusBean;
        }

        private List<RrrShareBean> getShares(RrrBean rrrBean) {
            List<RrrShareBean> result = new LinkedList<RrrShareBean>();
//            if (rrrBean.getTypeCode().toLowerCase().contains("ownership")
//                    || rrrBean.getTypeCode().toLowerCase().contains("apartment")) {
//                result = rrrBean.getFilteredRrrShareList();
//            }
            // Samoa Customization all primary rrrs are ownership rrrs
            if (rrrBean.isPrimary()) {
                result = rrrBean.getFilteredRrrShareList();
            }
            return result;
        }
    }

    private class AllBaUnitNotationsListUpdater implements ObservableListListener, Serializable {

        @Override
        public void listElementsAdded(ObservableList ol, int i, int i1) {
            BaUnitNotationBean notationBean = getNotation(ol.get(i));
            if (notationBean != null) {
                allBaUnitNotationList.add(notationBean);
            }
        }

        @Override
        public void listElementsRemoved(ObservableList ol, int i, List list) {
            for (Object bean : list) {
                allBaUnitNotationList.remove(getNotation(bean));
            }
        }

        @Override
        public void listElementReplaced(ObservableList ol, int i, Object o) {
            int index = allBaUnitNotationList.indexOf(getNotation(o));
            // Samoa #75 Error on Computer Folio Certificate
            if (index > -1 && getNotation(ol.get(i)) != null) {
                allBaUnitNotationList.set(index, getNotation(ol.get(i)));
            }
        }

        @Override
        public void listElementPropertyChanged(ObservableList ol, int i) {
        }

        private BaUnitNotationBean getNotation(Object bean) {
            BaUnitNotationBean notationBean = null;

            if (bean instanceof RrrBean) {
                notationBean = (BaUnitNotationBean) ((RrrBean) bean).getNotation();
            } else if (bean instanceof BaUnitNotationBean) {
                notationBean = (BaUnitNotationBean) bean;
            }

            return notationBean;
        }
    }
    private static final String BAUNIT_ID_SEARCH = "system_search.cadastre_object_by_baunit_id";
    public static final String SELECTED_HISTORIC_RIGHT_PROPERTY = "selectedHistoricRight";
    public static final String SELECTED_PARCEL_PROPERTY = "selectedParcel";
    public static final String SELECTED_RIGHT_PROPERTY = "selectedRight";
    public static final String SELECTED_BA_UNIT_NOTATION_PROPERTY = "selectedBaUnitNotation";
    public static final String SELECTED_PARENT_BA_UNIT_PROPERTY = "selectedParentBaUnit";
    public static final String SELECTED_CHILD_BA_UNIT_PROPERTY = "selectedChildBaUnit";
    public static final String ESTATE_TYPE_PROPERTY = "estateType";
    public static final String PENDING_ACTION_CODE_PROPERTY = "pendingActionCode";
    public static final String PENDING_ACTION_PROPERTY = "pendingTypeAction";
    public static final String SELECTED_BA_UNIT_AREA_PROPERTY = "selectedBaUnitArea";
    public static final String NIL_REGISTERED_DEALINGS_TEXT = "Registered Dealings - Nil";
    public static final String NIL_UNREGISTERED_DEALINGS_TEXT = "Unregistered Dealings - Nil";
    private SolaList<RrrBean> rrrList;
    private SolaList<BaUnitNotationBean> baUnitNotationList;
    private SolaList<CadastreObjectBean> cadastreObjectList;
    private SolaList<CadastreObjectBean> newCadastreObjectList;
    private SolaObservableList<BaUnitNotationBean> allBaUnitNotationList;
    private SolaList<SourceBean> sourceList;
    private SolaObservableList<RrrShareWithStatus> rrrSharesList;
    @Valid
    private SolaList<RelatedBaUnitInfoBean> childBaUnits;
    @Valid
    private SolaList<RelatedBaUnitInfoBean> parentBaUnits;
    private SolaList<BaUnitAreaBean> baUnitAreaList;
    private ObservableList<BaUnitNotationBean> baUnitCurrentNotationList;
    private ObservableList<BaUnitNotationBean> baUnitPendingNotationList;
    private transient ObservableList<BaUnitNotationBean> easementList;
    private List<PartySummaryBean> currentOwnersList;
    private List<UnregisteredDealingBean> unregisteredDealingList;
    private SolaList<CertificatePrintBean> certificatePrintList;
    private transient CadastreObjectBean selectedParcel;
    private transient SolaList<RrrBean> rrrHistoricList;
    private transient RrrBean selectedRight;
    private transient RrrBean selectedHistoricRight;
    private transient BaUnitNotationBean selectedBaUnitNotation;
    private transient RelatedBaUnitInfoBean selectedParentBaUnit;
    private transient RelatedBaUnitInfoBean selectedChildBaUnit;
    private transient BaUnitAreaBean selectedBaUnitArea;
    private String estateType;
    private TypeActionBean pendingTypeAction;
    private BigDecimal calculatedAreaSize;
    private String village;
    private String district;
    private String priorTitle;
    private Date folioRegDate;

    public BigDecimal getCalculatedAreaSize() {
        return calculatedAreaSize;
    }

    public void setCalculatedAreaSize(BigDecimal calculatedAreaSize) {
        this.calculatedAreaSize = calculatedAreaSize;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPriorTitle() {
        return priorTitle;
    }

    public void setPriorTitle(String priorTitle) {
        this.priorTitle = priorTitle;
    }

    public Date getFolioRegDate() {
        return folioRegDate;
    }

    public void setFolioRegDate(Date folioRegDate) {
        this.folioRegDate = folioRegDate;
    }

    public BaUnitBean() {
        super();
        rrrList = new SolaList();
        rrrHistoricList = new SolaList<RrrBean>();
        baUnitNotationList = new SolaList();
        cadastreObjectList = new SolaList();
        baUnitAreaList = new SolaList();
        certificatePrintList = new SolaList();
        childBaUnits = new SolaList();
        parentBaUnits = new SolaList();
        sourceList = new SolaList();
        allBaUnitNotationList = new SolaObservableList<BaUnitNotationBean>();
        rrrSharesList = new SolaObservableList<RrrShareWithStatus>();
        rrrList.getFilteredList().addObservableListListener(new RrrListListener());

        sourceList.setExcludedStatuses(new String[]{StatusConstants.HISTORIC});
        rrrList.setExcludedStatuses(new String[]{StatusConstants.HISTORIC, StatusConstants.PREVIOUS});
        rrrHistoricList.setExcludedStatuses(new String[]{StatusConstants.CURRENT, StatusConstants.PENDING});

        AllBaUnitNotationsListUpdater allBaUnitNotationsListener = new AllBaUnitNotationsListUpdater();
//        rrrList.getFilteredList().addObservableListListener(allBaUnitNotationsListener);
//        baUnitNotationList.getFilteredList().addObservableListListener(allBaUnitNotationsListener);

        rrrList.addObservableListListener(allBaUnitNotationsListener);
        baUnitNotationList.addObservableListListener(allBaUnitNotationsListener);

        rrrList.addObservableListListener(new ObservableListListener() {
            RrrComparatorByRegistrationDate sorter = new RrrComparatorByRegistrationDate();

            @Override
            public void listElementsAdded(ObservableList list, int index, int length) {
                for (int i = index; i < length + index; i++) {
                    rrrHistoricList.add((RrrBean) list.get(i));
                }
                Collections.sort(rrrHistoricList.getFilteredList(), sorter);
            }

            @Override
            public void listElementsRemoved(ObservableList list, int index, List oldElements) {
                rrrHistoricList.removeAll(oldElements);
                Collections.sort(rrrHistoricList.getFilteredList(), sorter);
            }

            @Override
            public void listElementReplaced(ObservableList list, int index, Object oldElement) {
                rrrHistoricList.set(rrrHistoricList.indexOf(oldElement), (RrrBean) oldElement);
                Collections.sort(rrrHistoricList.getFilteredList(), sorter);
            }

            @Override
            public void listElementPropertyChanged(ObservableList list, int index) {
            }
        });

    }

    public void createPaperTitle(SourceBean source) {
        if (source != null) {
            for (SourceBean sourceBean : sourceList) {
                sourceBean.setEntityAction(EntityAction.DISASSOCIATE);
            }
            sourceList.addAsNew(source);
            sourceList.filter();
        }
    }

    // Checks for pending RRRs by RRR type and transaction.
    public boolean isPendingRrrExists(String rrrTypeCode) {
        if (getRrrFilteredList() != null && rrrTypeCode != null) {
            for (RrrBean bean : getRrrFilteredList()) {
                if (bean.getTypeCode() != null && bean.getTypeCode().equals(rrrTypeCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Checks for pending RRRs by provided RRR object.
    public boolean isPendingRrrExists(RrrBean rrrBean) {
        if (getRrrFilteredList() != null && rrrBean != null) {
            for (RrrBean bean : getRrrFilteredList()) {
                if (bean.getNr() != null && rrrBean.getNr() != null
                        && bean.getNr().equals(rrrBean.getNr())
                        && !bean.getId().equals(rrrBean.getId())
                        && (bean.getStatusCode() == null || bean.getStatusCode().equalsIgnoreCase(StatusConstants.PENDING))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValid() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
//        if(!this.getName().isEmpty()&& this.getName()!= null && this.getName()!= "" ){
        if (this.getName() != null) {
            if (this.getName().length() > 255) {
                MessageUtility.displayMessage(ClientMessage.CHECK_FIELD_INVALID_LENGTH_PAR, new Object[]{bundle.getString("PropertyPanel.jLabel5.text")});
                return false;
            }
        }
        return true;
    }

    public void removeSelectedParcel() {
        if (selectedParcel != null && cadastreObjectList != null) {
            if (selectedParcel.getStatusCode().equalsIgnoreCase(CadastreObjectBean.PENDING_STATUS)) {
                cadastreObjectList.safeRemove(selectedParcel, EntityAction.DELETE);
            } else {
                cadastreObjectList.safeRemove(selectedParcel, EntityAction.DISASSOCIATE);
            }
        }

    }

    public void removeSelectedRight() {
        if (selectedRight != null && rrrList != null) {
            rrrList.safeRemove(selectedRight, EntityAction.DELETE);
        }
    }

    public boolean addBaUnitNotation(String notationRef, String notationText) {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
        if (notationText.length() > 1000) {
            MessageUtility.displayMessage(ClientMessage.CHECK_FIELD_INVALID_LENGTH_PAR, new Object[]{bundle.getString("PropertyPanel.jLabel15.text")});
            return false;
        }

        BaUnitNotationBean notation = new BaUnitNotationBean();
        notation.setBaUnitId(this.getId());
        notation.setNotationText(notationText);
        notation.setStatusCode(StatusConstants.PENDING);
        notation.setReferenceNr(notationRef);

        if (notation.validate(true).size() < 1) {
            baUnitNotationList.addAsNew(notation);
            return true;
        }
        return false;
    }

    public void removeSelectedBaUnitNotation() {
        if (selectedBaUnitNotation != null && baUnitNotationList.size() > 0
                && selectedBaUnitNotation.getStatusCode().equalsIgnoreCase(StatusConstants.PENDING)
                && selectedBaUnitNotation.getBaUnitId().equals(this.getId())) {
            baUnitNotationList.safeRemove(selectedBaUnitNotation, EntityAction.DELETE);
        }
    }

    public ObservableList<BaUnitNotationBean> getAllBaUnitNotationList() {
        return sortNotations(allBaUnitNotationList);
    }

    public BaUnitNotationBean getSelectedBaUnitNotation() {
        return selectedBaUnitNotation;
    }

    public void setSelectedBaUnitNotation(BaUnitNotationBean selectedBaUnitNotation) {
        this.selectedBaUnitNotation = selectedBaUnitNotation;
        propertySupport.firePropertyChange(SELECTED_BA_UNIT_NOTATION_PROPERTY,
                null, selectedBaUnitNotation);
    }

    public RelatedBaUnitInfoBean getSelectedChildBaUnit() {
        return selectedChildBaUnit;
    }

    public void setSelectedChildBaUnit(RelatedBaUnitInfoBean selectedChildBaUnit) {
        this.selectedChildBaUnit = selectedChildBaUnit;
        propertySupport.firePropertyChange(SELECTED_CHILD_BA_UNIT_PROPERTY,
                null, this.selectedChildBaUnit);
    }

    public RelatedBaUnitInfoBean getSelectedParentBaUnit() {
        return selectedParentBaUnit;
    }

    public void setSelectedParentBaUnit(RelatedBaUnitInfoBean selectedParentBaUnit) {
        this.selectedParentBaUnit = selectedParentBaUnit;
        propertySupport.firePropertyChange(SELECTED_PARENT_BA_UNIT_PROPERTY,
                null, this.selectedParentBaUnit);
    }

    public CadastreObjectBean getSelectedParcel() {
        return selectedParcel;
    }

    public void setSelectedParcel(CadastreObjectBean selectedParcel) {
        this.selectedParcel = selectedParcel;
        propertySupport.firePropertyChange(SELECTED_PARCEL_PROPERTY,
                null, selectedParcel);
    }

    public RrrBean getSelectedRight() {
        return selectedRight;
    }

    public void setSelectedRight(RrrBean selectedRight) {
        this.selectedRight = selectedRight;
        propertySupport.firePropertyChange(SELECTED_RIGHT_PROPERTY,
                null, selectedRight);
    }

    public RrrBean getSelectedHistoricRight() {
        return selectedHistoricRight;
    }

    public void setSelectedHistoricRight(RrrBean selectedHistoricRight) {
        this.selectedHistoricRight = selectedHistoricRight;
        propertySupport.firePropertyChange(SELECTED_HISTORIC_RIGHT_PROPERTY,
                null, selectedHistoricRight);
    }

    public SolaList<BaUnitNotationBean> getBaUnitNotationList() {
        return baUnitNotationList;
    }

    public ObservableList<BaUnitNotationBean> getBaUnitFilteredNotationList() {
        return sortNotations(baUnitNotationList.getFilteredList());
    }

    public SolaList<CadastreObjectBean> getNewCadastreObjectList() {
        if (newCadastreObjectList == null) {
            loadNewParcels();
        }
        return newCadastreObjectList;
    }

    public SolaList<RelatedBaUnitInfoBean> getChildBaUnits() {
        return childBaUnits;
    }

    public SolaList<RelatedBaUnitInfoBean> getParentBaUnits() {
        return parentBaUnits;
    }

    public ObservableList<RelatedBaUnitInfoBean> getFilteredChildBaUnits() {
        return childBaUnits.getFilteredList();
    }

    public ObservableList<RelatedBaUnitInfoBean> getFilteredParentBaUnits() {
        return parentBaUnits.getFilteredList();
    }

    public String getPendingActionCode() {
        return getPendingTypeAction().getCode();
    }

    public void setPendingActionCode(String pendingActionCode) {
        String oldValue = null;
        if (getPendingTypeAction() != null) {
            oldValue = getPendingTypeAction().getCode();
        }
        setPendingTypeAction(CacheManager.getBeanByCode(
                CacheManager.getTypeActions(), pendingActionCode));
        propertySupport.firePropertyChange(PENDING_ACTION_CODE_PROPERTY, oldValue, pendingActionCode);
    }

    public TypeActionBean getPendingTypeAction() {
        if (this.pendingTypeAction == null) {
            this.pendingTypeAction = new TypeActionBean();
        }
        return pendingTypeAction;
    }

    public void setPendingTypeAction(TypeActionBean pendingTypeAction) {
        this.pendingTypeAction = pendingTypeAction;
        if (this.pendingTypeAction == null) {
            this.pendingTypeAction = new TypeActionBean();
        }
        this.setJointRefDataBean(this.pendingTypeAction, pendingTypeAction, PENDING_ACTION_PROPERTY);
    }

    public ObservableList<CadastreObjectBean> getSelectedNewCadastreObjects() {
        ObservableList<CadastreObjectBean> selectedCadastreObjects
                = ObservableCollections.observableList(new ArrayList<CadastreObjectBean>());
        for (CadastreObjectBean cadastreObject : getNewCadastreObjectList()) {
            if (cadastreObject.isSelected()) {
                selectedCadastreObjects.add(cadastreObject);
            }
        }
        return selectedCadastreObjects;
    }

    public ObservableList<CadastreObjectBean> getSelectedCadastreObjects() {
        ObservableList<CadastreObjectBean> selectedCadastreObjects
                = ObservableCollections.observableList(new ArrayList<CadastreObjectBean>());
        for (CadastreObjectBean cadastreObject : getCadastreObjectFilteredList()) {
            if (cadastreObject.isSelected()) {
                selectedCadastreObjects.add(cadastreObject);
            }
        }
        return selectedCadastreObjects;
    }

    /**
     * Returns the list of selected rights.
     *
     * @param regenerateIds If true, will generate new IDs for all parent and
     * child objects.
     */
    public ObservableList<RrrBean> getSelectedRrrs(boolean regenerateIds) {
        ObservableList<RrrBean> selectedRrrs
                = ObservableCollections.observableList(new ArrayList<RrrBean>());
        for (RrrBean rrr : getRrrFilteredList()) {
            if (rrr.isSelected()) {
                if (regenerateIds) {
                    rrr.resetIdAndVerion(true, true);
                }
                selectedRrrs.add(rrr);
            }
        }
        return selectedRrrs;
    }

    public ObservableList<CadastreObjectBean> getFilteredNewCadastreObjectList() {
        return getNewCadastreObjectList().getFilteredList();
    }

    public SolaList<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    public ObservableList<CadastreObjectBean> getCadastreObjectFilteredList() {
        return cadastreObjectList.getFilteredList();
    }

    public SolaList<RrrBean> getRrrList() {
        return rrrList;
    }

    public ObservableList<RrrBean> getRrrFilteredList() {
        return sortRrrs(rrrList.getFilteredList());
    }

    public ObservableList<RrrBean> getRrrHistoricList() {
        return sortRrrs(rrrHistoricList.getFilteredList());
    }

    public void addRrr(RrrBean rrrBean) {
        if (!this.updateListItem(rrrBean, rrrList, false)) {
            int i = 0;
            // Search by number
            i = 0;
            for (RrrBean bean : rrrList.getFilteredList()) {
                if (bean.getNr() != null && rrrBean.getNr() != null
                        && bean.getNr().equals(rrrBean.getNr())) {
                    rrrList.getFilteredList().add(i + 1, rrrBean);
                    return;
                }
                i += 1;
            }

            // If RRR is new
            rrrList.add(rrrBean);
        }
    }

    public ObservableList<RrrShareWithStatus> getRrrSharesList() {
        return rrrSharesList;
    }

    public String getEstateType() {
        return estateType;
    }

    public void setEstateType() {
        String oldValue = estateType;
        estateType = "";

        for (RrrBean rrrBean : rrrList.getFilteredList()) {
            if (rrrBean.isPrimary()) {
                estateType = rrrBean.getRrrType().getDisplayValue();
                break;
            }
        }
        propertySupport.firePropertyChange(ESTATE_TYPE_PROPERTY, oldValue, estateType);

    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void removeSelectedParentBaUnit() {
        if (getSelectedParentBaUnit() != null) {
            getParentBaUnits().safeRemove(getSelectedParentBaUnit(), EntityAction.DELETE);
        }
    }

    public boolean createBaUnit(String serviceId) {
        BaUnitTO baUnit = TypeConverters.BeanToTrasferObject(this, BaUnitTO.class);
        baUnit = WSManager.getInstance().getAdministrative().createBaUnit(serviceId, baUnit);
        TypeConverters.TransferObjectToBean(baUnit, BaUnitBean.class, this);
        return true;
    }

    public boolean saveBaUnit(String serviceId) {
        BaUnitTO baUnit = TypeConverters.BeanToTrasferObject(this, BaUnitTO.class);
        baUnit = WSManager.getInstance().getAdministrative().saveBaUnit(serviceId, baUnit);
        TypeConverters.TransferObjectToBean(baUnit, BaUnitBean.class, this);
        return true;
    }

    /**
     * Loads list of new parcels, created on the base of current BA unit parcels
     * (e.g. result of subdivision).
     */
    private void loadNewParcels() {
        if (newCadastreObjectList == null) {
            newCadastreObjectList = new SolaList<CadastreObjectBean>();
        }
        newCadastreObjectList.clear();
        if (getId() != null) {
            List<SpatialSearchResultTO> searchResults
                    = WSManager.getInstance().getSearchService().searchSpatialObjects(BAUNIT_ID_SEARCH, getId());
            if (searchResults != null && searchResults.size() > 0) {
                List<String> ids = new ArrayList<String>();
                for (SpatialSearchResultTO result : searchResults) {
                    ids.add(result.getId());
                }
                TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getCadastreService().getCadastreObjects(ids),
                        CadastreObjectBean.class, (List) newCadastreObjectList);
            }
        }
    }

    /**
     * Filters all child lists to keep only records with current status.
     */
    public void filterCurrentRecords() {
        sourceList.setIncludedStatuses(new String[]{StatusConstants.CURRENT});
        rrrList.setIncludedStatuses(new String[]{StatusConstants.CURRENT});
        baUnitNotationList.setIncludedStatuses(new String[]{StatusConstants.CURRENT});
        cadastreObjectList.setIncludedStatuses(new String[]{StatusConstants.CURRENT});
    }

    /**
     * Returns BA Unit by ID.
     *
     * @param baUnitId The ID of BA Unit to return.
     */
    public static BaUnitBean getBaUnitsById(String baUnitId) {
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getAdministrative().getBaUnitById(baUnitId),
                BaUnitBean.class, null);
    }

    /**
     * Returns list of BA Units, created by the given service.
     *
     * @param serviceId The ID of service, used pick up BA Units.
     */
    public static List<BaUnitBean> getBaUnitsByServiceId(String serviceId) {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getAdministrative().getBaUnitsByServiceId(serviceId),
                BaUnitBean.class, null);
    }

    /**
     * Returns o BA Unit Areas, for the Ba Unit Id.
     *
     * @param baUnitId The ID of service, used pick up BA Units.
     */
    public static BaUnitAreaBean getBaUnitArea(String baUnitId) {
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getAdministrative().getBaUnitAreas(baUnitId),
                BaUnitAreaBean.class, null);
    }

    /**
     * Terminates/Cancel BaUnit. Creates pending record for further action.
     *
     * @param serviceId ID of the service, which terminates BaUnit.
     */
    public void terminateBaUnit(String serviceId) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().terminateBaUnit(this.getId(), serviceId);
        if (baUnitTO != null) {
            TypeConverters.TransferObjectToBean(
                    baUnitTO, BaUnitBean.class, this);
        }
    }

    /**
     * Rolls back BaUnit termination/cancellation.
     */
    public void cancelBaUnitTermination() {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().cancelBaUnitTermination(this.getId());
        if (baUnitTO != null) {
            TypeConverters.TransferObjectToBean(
                    baUnitTO, BaUnitBean.class, this);
        }
    }

    /**
     * Returns collection of {@link BaUnitBean} objects. This method is used by
     * Jasper report designer to extract properties of BA Unit bean to help
     * design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        BaUnitBean bean = new BaUnitBean();
        collection.add(bean);
        return collection;
    }

    /**
     * Returns the list of current notations for the property. Displayed in the
     * Second Schedule section of the Computer Folio certificate.
     *
     * @return
     */
    public ObservableList<BaUnitNotationBean> getBaUnitCurrentNotationList() {
        // ObservableList<BaUnitNotationBean> result = new SolaList(getAllBaUnitNotationList(), null, new String[]{StatusConstants.CURRENT}).getFilteredList();
        ObservableList<BaUnitNotationBean> result = new SolaObservableList<BaUnitNotationBean>();

        // Get all of the current notations linked to the ba unit. Notations linked to an rrr
        // will be added next depending on the rrr type and status. 
        for (BaUnitNotationBean bean : allBaUnitNotationList) {
            if (bean.getBaUnitId() != null && StatusConstants.CURRENT.equals(bean.getStatusCode())) {
                bean.setNotationText(formatNotationText(bean.getNotationText(), bean.getReferenceNr()));
                result.add(bean);
            }
        }
        // Get all current rrrs and remove the primary Rrrs as these are displayed in the First Schedule
        // Also remove easements as these are displayed in the Land Description
        for (RrrBean bean : rrrList.getFilteredList()) {
            if (!bean.isPrimary() && !bean.isEasement() && bean.getNotation() != null
                    && StatusConstants.CURRENT.equals(bean.getStatusCode())) {
                BaUnitNotationBean notation = bean.getNotation();
                notation.setNotationText(formatNotationText(notation.getNotationText(),
                        notation.getReferenceNr()));
                result.add(notation);
            }
        }

        if (result.size() == 0) {
            BaUnitNotationBean dummy = new BaUnitNotationBean();
            dummy.setNotationText(NIL_REGISTERED_DEALINGS_TEXT);
            result.add(dummy);
        } else {
            result = sortNotations(result);
        }

        return result;
    }

    /**
     * Returns the list of pending notations for the property. Displayed in the
     * Notations section of the Computer Folio certificate.
     *
     * @return
     */
    public ObservableList<BaUnitNotationBean> getBaUnitPendingNotationList() {
        ObservableList<BaUnitNotationBean> result = new SolaList(getAllBaUnitNotationList(), null, new String[]{StatusConstants.PENDING}).getFilteredList();
        if (result.size() > 0) {
            for (BaUnitNotationBean bean : result) {
                // Format the Notation text for display by including the 
                bean.setNotationText(formatNotationText(bean.getNotationText(), bean.getReferenceNr()));
            }
            result = sortNotations(result);
        } else {
            BaUnitNotationBean dummy = new BaUnitNotationBean();
            dummy.setNotationText(NIL_UNREGISTERED_DEALINGS_TEXT);
            result.add(dummy);
        }
        return result;
    }

    /**
     * Used to help format the notation text for display on the Computer Folio
     * Certificate report.
     *
     * @param notationText
     * @param refNr
     * @return
     */
    private String formatNotationText(String notationText, String refNr) {
        String result = notationText == null ? "" : notationText;
        if (refNr != null && !result.startsWith(refNr.trim())) {
            result = refNr + " " + notationText;
        }
        return result;
    }

    /**
     * Sorts the list of notations by notation date if provided or otherwise by
     * the notation reference number. Removes any non digit characters before
     * performing the sort.
     *
     * @param notationsList
     * @return
     */
    public ObservableList<BaUnitNotationBean> sortNotations(ObservableList<BaUnitNotationBean> notationsList) {
        // Sort the list of notations by the reference Nr
        Collections.sort(notationsList, new Comparator<BaUnitNotationBean>() {
            @Override
            public int compare(BaUnitNotationBean note1, BaUnitNotationBean note2) {
                // Remove any non digit characters from the string using reg expression.
                if (note1 != null && note2 != null && note1.getChangeTime() != null && note2.getChangeTime() != null
                        && !note1.getChangeTime().equals(note2.getChangeTime())) {
                    return note1.getChangeTime().compareTo(note2.getChangeTime());
                } else {
                    // AM 17 Aug 2017 Added extra check for reference number that is all characters
                    BigDecimal ref1 = note1 == null || note1.getReferenceNr() == null
                            || "".equals(note1.getReferenceNr().replaceAll("[^0-9\\.]", "")) ? BigDecimal.ZERO
                            : new BigDecimal(note1.getReferenceNr().replaceAll("[^0-9\\.]", ""));
                    BigDecimal ref2 = note2 == null || note2.getReferenceNr() == null
                            || "".equals(note2.getReferenceNr().replaceAll("[^0-9\\.]", "")) ? BigDecimal.ZERO
                            : new BigDecimal(note2.getReferenceNr().replaceAll("[^0-9\\.]", ""));
                    return ref1.compareTo(ref2);
                }
            }
        });
        return notationsList;
    }

    /**
     * Retrieves the list of current owners for the ba unit based on the owner
     * details for the primary Rrrs.
     *
     * @return
     */
    public List<PartySummaryBean> getCurrentOwnersList() {
        List<PartySummaryBean> result = new ArrayList<PartySummaryBean>();
        boolean commonTenant;
        boolean lifeEstate = false;

        //Get all the current shares
        List<RrrShareBean> shares = new ArrayList<RrrShareBean>();
        for (RrrBean rrr : rrrList.getFilteredList()) {
            if (rrr.isPrimary() && StatusConstants.CURRENT.equals(rrr.getStatusCode())) {
                shares.addAll(rrr.getRrrShareList());
            }
            if (RrrTypeBean.RRR_TYPE_CODE_LIFE_ESTATE.equals(rrr.getTypeCode())
                    && StatusConstants.CURRENT.equals(rrr.getStatusCode())) {
                // This property has a life estate
                lifeEstate = true;
            }
        }

        if (shares.size() == 1 && shares.get(0).getFilteredRightHolderList().size() == 1) {
            // Only 1 owner so use the simple name formatting
            PartySummaryBean rightHolder = new PartySummaryBean();
            String fullName = formatOwnerName(shares.get(0).getFilteredRightHolderList().get(0).getFullName(),
                    "1/1", false, false, lifeEstate);
            rightHolder.setName(fullName);
            result.add(rightHolder);
        } else {
            commonTenant = shares.size() > 1;
            for (RrrShareBean share : shares) {
                int numOwners = share.getFilteredRightHolderList().size();
                int tmpCount = 1;
                String ownerNames = "";
                for (PartySummaryBean bean : share.getFilteredRightHolderList()) {
                    if (tmpCount == 1) {
                        ownerNames = bean.getFullName();
                    } else if (tmpCount > 1 && numOwners != tmpCount) {
                        ownerNames = ownerNames + ", " + bean.getFullName();
                    } else {
                        ownerNames = ownerNames + " and " + bean.getFullName();
                    }
                    tmpCount++;
                }
                PartySummaryBean rightHolder = new PartySummaryBean();
                // Format the text to display for the owner based on their share holding
                rightHolder.setName(formatOwnerName(ownerNames,
                        share.getShare(), numOwners > 1, commonTenant, lifeEstate));
                result.add(rightHolder);
            }

        }
        return result;
    }

    /**
     * Determines the description for the owner on the Computer Folio
     * Certificate
     *
     * @param name The owner name
     * @param share The share for the owner
     * @param jointTenant Indicates if the owner is a joint tenant
     * @param commonTenant Indicates if the owner is a tenant in common
     */
    protected String formatOwnerName(String name, String share, boolean jointTenant,
            boolean commonTenant, boolean lifeEstate) {
        String result = name;
        if (jointTenant && !commonTenant) {
            result = result + " as joint tenants";
        }
        if (!jointTenant && commonTenant) {
            result = result + " as to a " + share + " share as a tenant in common";
        }
        if (jointTenant && commonTenant) {
            result = result + " as joint tenants as to a " + share + " share as a tenant in common";
        }
        if (lifeEstate) {
            result = result + " of an estate in remainder";
        }
        return result;
    }

    /**
     * Sorts the list of Rrrs by registration date if provided or otherwise by
     * the Rrr reference number. Removes any non digit characters before
     * performing the sort.
     *
     * @param rrrList
     * @return
     */
    public ObservableList<RrrBean> sortRrrs(ObservableList<RrrBean> rrrList) {
        // Sort the list of RRR's by registered date if available otherwise by the reference nr
        Collections.sort(rrrList, new Comparator<RrrBean>() {
            @Override
            public int compare(RrrBean rrr1, RrrBean rrr2) {
                // Remove any non digit characters from the string using reg expression.
                if (rrr1 != null && rrr2 != null && rrr1.getRegistrationExpirationDate() != null
                        && rrr2.getRegistrationExpirationDate() != null
                        && !rrr1.getRegistrationExpirationDate().equals(rrr2.getRegistrationExpirationDate())) {
                    return rrr1.getRegistrationExpirationDate().compareTo(rrr2.getRegistrationExpirationDate());
                } else {
                    // 17 Aug 2017. Check that the reference is not all characters. 
                    String ref1Str = rrr1 == null || rrr1.getNotation() == null
                            || rrr1.getNotation().getReferenceNr() == null
                            || "".equals(rrr1.getNotation().getReferenceNr().replaceAll("[^0-9\\.]", "")) ? "0"
                            : rrr1.getNotation().getReferenceNr();
                    String ref2Str = rrr2 == null || rrr2.getNotation() == null
                            || rrr2.getNotation().getReferenceNr() == null
                            || "".equals(rrr2.getNotation().getReferenceNr().replaceAll("[^0-9\\.]", "")) ? "0"
                            : rrr2.getNotation().getReferenceNr();
                    BigDecimal ref1 = new BigDecimal(ref1Str.replaceAll("[^0-9\\.]", ""));
                    BigDecimal ref2 = new BigDecimal(ref2Str.replaceAll("[^0-9\\.]", ""));
                    return ref1.compareTo(ref2);
                }
            }
        });
        return rrrList;
    }

    /**
     * Returns true if this property is part of a Unit Development (i.e. it is a
     * strata unit or linked to a common property)
     */
    public boolean isStrataProperty() {
        boolean result = BaUnitTypeBean.TYPE_CODE_STRATA_UNIT.equals(getTypeCode());
        if (!result) {
            if (getFilteredChildBaUnits() != null && getFilteredChildBaUnits().size() > 0) {
                for (RelatedBaUnitInfoBean bean : getFilteredChildBaUnits()) {
                    if (BaUnitTypeBean.TYPE_CODE_STRATA_UNIT.equals(bean.getRelatedBaUnit().getTypeCode())) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Samoa Customization - Retrieve the list of unregistered Dealings for the
     * property
     */
    public List<UnregisteredDealingBean> getUnregisteredDealings() {

        if (unregisteredDealingList == null) {
            unregisteredDealingList = TypeConverters.TransferObjectListToBeanList(
                    WSManager.getInstance().getSearchService().getUnregisteredDealings(getId()),
                    UnregisteredDealingBean.class, null);
        }
        if (unregisteredDealingList == null) {
            unregisteredDealingList = new ArrayList<UnregisteredDealingBean>();
        }
        if (unregisteredDealingList.isEmpty()) {
            UnregisteredDealingBean bean = new UnregisteredDealingBean();
            bean.setPendingServices(UnregisteredDealingBean.NIL_DEALING);
            unregisteredDealingList.add(bean);
        }
        return unregisteredDealingList;
    }

    public SolaList<CertificatePrintBean> getCertificatePrintList() {
        return certificatePrintList;
    }

    /**
     * Returns the list of current easements for the property. Displayed in the
     * Land Description section of the Computer Folio certificate.
     *
     * @return
     */
    public ObservableList<BaUnitNotationBean> getEasementList() {
        easementList = new SolaObservableList<BaUnitNotationBean>();

        // Get all current Easements for display in the Land Description
        for (RrrBean bean : rrrList.getFilteredList()) {
            if (bean.isEasement() && StatusConstants.CURRENT.equals(bean.getStatusCode())) {
                BaUnitNotationBean notation;
                if (bean.getNotation() == null) {
                    notation = new BaUnitNotationBean();
                    notation.setNotationText("Subject to easement " + bean.getNr());
                } else {
                    notation = bean.getNotation();
                    notation.setNotationText(formatNotationText(notation.getNotationText(), null));
                }
                easementList.add(notation);
            }
        }

        if (easementList.size() > 0) {
            easementList = sortNotations(easementList);
        }

        return easementList;
    }

    public boolean isDeed() {
        boolean result = false;
        if (getNameFirstpart() != null) {
            result = getNameFirstpart().trim().startsWith("V");
        }
        return result;
    }
    
    /**
     * Updates the status of the Bean from historic to current and refreshes
     * the bean
     */
    public void makePropertyCurrent() {
        WSManager.getInstance().getAdministrative().makePropertyCurrent(getId());
        BaUnitTO baUnit = WSManager.getInstance().getAdministrative().getBaUnitById(getId());
        TypeConverters.TransferObjectToBean(baUnit, BaUnitBean.class, this);
    }

}
