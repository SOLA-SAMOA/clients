/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.desktop.unittitle;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.CadastreObjectTypeListBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.transaction.TransactionUnitParcelsBean;
import org.sola.clients.beans.unittitle.StrataPropertyListBean;
import org.sola.clients.beans.unittitle.UnitParcelBean;
import org.sola.clients.beans.unittitle.UnitParcelGroupBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.administrative.PropertyPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForUnitParcels;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.StringUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Panel used to manage information for Unit Title Developments in Samoa.
 */
public class UnitParcelsPanel extends ContentPanel {

    /**
     * Constants defining the prefixes used for the unit parcel types.
     */
    private static final String PRINCIPAL_UNIT_LOT_NR_PREFIX = "PU";
    private static final String ACCESSORY_UNIT_LOT_NR_PREFIX = "AU";
    private static final String COMMON_PROPERTY_LOT_NR = "CP";
    private static final int LOT_NR_PREFIX_LEN = 2;
    /**
     * Instance variables for the panel
     */
    private ControlsBundleForUnitParcels mapControl = null;
    private boolean readOnly = true;
    private ApplicationServiceBean applicationService;
    private ApplicationBean applicationBean;
    private String unitDevelopmentNr;
    private boolean isRecordUnitPlan = false;
    private boolean isCreateStrataTitles = false;
    private boolean isCancelStrataTitles = false;
    private List<String> baUnitIds = new ArrayList<String>();

    /**
     * Constructor - Used for Record Unit Plan services where the application
     * number is used as the basis of the unit development number.
     *
     * @param applicationBean The application used to create or update the Unit
     * Plan Development. Can be null if the panel is opened independently from
     * an application/transaction. If null, the panel will default to readOnly.
     * @param appService The application service being processed or null if the
     * panel is opened independently from an application/transaction.
     * @param readOnly Indicates if the form is read only or not.
     */
    public UnitParcelsPanel(ApplicationBean applicationBean, ApplicationServiceBean appService,
            boolean readOnly) {
        // The Unit Development number should exclude any suffix assigned to make the application
        // number unique. 
        this(applicationBean, appService, (applicationBean == null || applicationBean.getNr() == null ? null
                : applicationBean.getNr().split("/", 2)[0]), readOnly);

    }

    /**
     * Constructor
     *
     * @param applicationBean The application used to create or update the Unit
     * Plan Development. Can be null if the panel is opened independently from
     * an application/transaction. If null, the panel will default to readOnly.
     * @param appService The application service being processed or null if the
     * panel is opened independently from an application/transaction.
     * @param unitDevelopmentNr The unit development number for the unit parcel
     * group
     * @param readOnly Indicates if the form is read only or not.
     */
    public UnitParcelsPanel(ApplicationBean applicationBean, ApplicationServiceBean appService,
            String unitDevelopmentNr, boolean readOnly) {

        this.applicationBean = applicationBean;
        this.readOnly = readOnly || applicationBean == null;
        this.applicationService = appService;
        isRecordUnitPlan = appService == null ? false
                : RequestTypeBean.CODE_RECORD_UNIT_PLAN.equals(appService.getRequestType().getCode());
        isCreateStrataTitles = appService == null ? false
                : RequestTypeBean.CODE_RECORD_STRATA_TITLES.equals(appService.getRequestType().getCode());
        isCancelStrataTitles = appService == null ? false
                : RequestTypeBean.CODE_CANCEL_STRATA_TITLES.equals(appService.getRequestType().getCode());
        if (applicationBean != null && applicationBean.getFilteredPropertyList().size() > 0) {
            for (ApplicationPropertyBean appProp : applicationBean.getFilteredPropertyList()) {
                if (appProp.getBaUnitId() != null && !baUnitIds.contains(appProp.getBaUnitId())) {
                    baUnitIds.add(appProp.getBaUnitId());
                }
            }
        }
        // Determine the Unit Parcel Development Number
        if (unitDevelopmentNr == null) {
            String serviceId = appService == null ? null : appService.getId();
            this.unitDevelopmentNr = UnitParcelGroupBean.getUnitDevelopmentNr(serviceId, baUnitIds);
        } else {
            this.unitDevelopmentNr = unitDevelopmentNr;
        }

        if (StringUtility.isEmpty(this.unitDevelopmentNr)) {
            // No Unit Development Number. The user may not have selected the correct
            // underlying property. 
            MessageUtility.displayMessage(ClientMessage.CHECK_UNIT_DEVELOPMENT_NR);
            this.readOnly = true;
        }

        createTransactionBean();
        createPropertyListBean();
        initComponents();
        postInit();

    }

    /**
     * Can be used to open the UnitParcelsPanel using the Unit Parcel
     * development number or the Identifier for one of the BA Units that is part
     * of the unit Development.
     *
     * @param unitDevelopmentNr
     * @param baUnitId
     * @param readOnly
     */
    public UnitParcelsPanel(String unitDevelopmentNr, String baUnitId, boolean readOnly) {
        if (baUnitId != null) {
            baUnitIds.add(baUnitId);
        }
        if (unitDevelopmentNr == null && baUnitId != null) {
            this.unitDevelopmentNr = UnitParcelGroupBean.getUnitDevelopmentNr(null, baUnitIds);
        } else {
            this.unitDevelopmentNr = unitDevelopmentNr;
        }

        if (StringUtility.isEmpty(this.unitDevelopmentNr)) {
            // No Unit Development Number. The user may not have selected the correct
            // underlying property. 
            MessageUtility.displayMessage(ClientMessage.CHECK_UNIT_DEVELOPMENT_NR);
            this.readOnly = true;
        }
        createTransactionBean();
        createPropertyListBean();
        initComponents();
        postInit();
    }

    /**
     * Default method used to initialize a transaction bean
     *
     * @return
     */
    private TransactionUnitParcelsBean createTransactionBean() {
        if (transactionBean == null) {
            transactionBean = new TransactionUnitParcelsBean();
        }
        return transactionBean;
    }

    private StrataPropertyListBean createPropertyListBean() {
        if (strataPropertyListBean == null) {
            strataPropertyListBean = new StrataPropertyListBean();
        }
        return strataPropertyListBean;

    }

    /**
     * Post Initialization method. Load the form with data and setup the buttons
     * and panel title.
     */
    private void postInit() {

        initializeMap();
        loadForm();

        // Set the title for the form
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".Bundle");
        if (applicationBean != null && applicationService != null) {
            headerPanel1.setTitleText(String.format(bundle.getString("UnitParcelsPanel.headerPanel1.titleText.Service"),
                    unitDevelopmentNr, applicationService.getRequestType().getDisplayValue()));
        } else {
            headerPanel1.setTitleText(String.format(bundle.getString("UnitParcelsPanel.headerPanel1.titleText.Property"),
                    unitDevelopmentNr));
        }

        // Only show Principal and Accessory unit parcel types in the dropdown list. Every Unit Plan
        // Development must include a Common Property parcel so this parcel is setup by default and
        // cannot be removed.
        cadastreObjectTypeListBean1.setIncludedCodes(CadastreObjectTypeBean.CODE_ACCESSORY_UNIT,
                CadastreObjectTypeBean.CODE_PRINCIPAL_UNIT);
        cadastreObjectTypeListBean1.setSelectedCadastreObjectTypeCode(CadastreObjectTypeBean.CODE_PRINCIPAL_UNIT);

        // Setup a listener for selection of a unit parcel from the list and customize the unit
        // parcel buttons based on the details of the selected unit. 
        transactionBean.getUnitParcelGroup().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(UnitParcelGroupBean.SELECTED_UNIT_PARCEL_PROPERTY)) {
                    customizeUnitParcelButtons((UnitParcelBean) evt.getNewValue());
                }
            }
        });

        // Setup a listener for selection of a unit parcel type and customize the unit parcel 
        // number accordingly. 
        cadastreObjectTypeListBean1.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CadastreObjectTypeListBean.SELECTED_CADASTRE_OBJECT_TYPE_PROPERTY)) {
                    customizeUnitParcelNr((CadastreObjectTypeBean) evt.getNewValue());
                }
            }
        });

        // Set the default state for the on screen buttons. 
        btnSave.setEnabled(!readOnly && isRecordUnitPlan);
        btnAddUnit.setEnabled(!readOnly && isRecordUnitPlan);
        btnRemoveUnit.setEnabled(false);
        btnReinstateUnit.setEnabled(false);

        txtUnitFirstPart.setEditable(!readOnly && isRecordUnitPlan);
        txtUnitFirstPart.setEnabled(!readOnly && isRecordUnitPlan);
        txtUnitArea.setEditable(!readOnly && isRecordUnitPlan);
        txtUnitArea.setEnabled(!readOnly && isRecordUnitPlan);
        cbxParcelType.setEditable(!readOnly && isRecordUnitPlan);
        cbxParcelType.setEnabled(!readOnly && isRecordUnitPlan);
        txtUnitLastPart.setEditable(false);
        txtUnitLastPart.setEnabled(false);
        if (!readOnly && isRecordUnitPlan) {
            txtUnitLastPart.setText(unitDevelopmentNr);
            txtUnitFirstPart.setText(calculateLotNumber(CadastreObjectTypeBean.CODE_PRINCIPAL_UNIT));
        }

        btnCreateProperties.setEnabled(!readOnly && isCreateStrataTitles);
        btnEditProperty.setEnabled(!readOnly && isCreateStrataTitles);
        btnRefreshProperties.setEnabled(!readOnly);

        customizeTerminateButton();

        // Load the map data
        loadMap();
    }

    /**
     * Ticket #68 - Implement Unit Development cancellation. Customize terminate
     * button depending on whether cancellation of the strata properties is
     * pending or not.
     */
    private void customizeTerminateButton() {
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".Bundle");
        btnTerminateProperties.setEnabled(!readOnly && isCancelStrataTitles);
        if (strataPropertyListBean.isPendingCancellation()) {
            // Show Cancel termination
            btnTerminateProperties.setIcon(new ImageIcon(getClass().getResource("/images/common/undo.png")));
            btnTerminateProperties.setText(bundle.getString("UnitParcelsPanel.btnTerminateProperties.text2"));
        } else {
            // Show Terminate
            btnTerminateProperties.setIcon(new ImageIcon(getClass().getResource("/images/common/stop.png")));
            btnTerminateProperties.setText(bundle.getString("UnitParcelsPanel.btnTerminateProperties.text"));
        }
    }

    /**
     * Creates the map bundle, initializes it and links it to the panel
     */
    private void initializeMap() {
        this.mapControl = new ControlsBundleForUnitParcels();
        this.mapControl.setup(null, readOnly);
        this.mapPanel.setLayout(new BorderLayout());
        this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
    }

    /**
     * Loads the transaction for the Unit Title Development from the database.
     * If there isn't a transaction, a default transaction object is configured.
     */
    public void loadForm() {
        if (applicationService != null) {
            // Get the transaction object using the service id
            transactionBean.setFromServiceId(applicationService.getId());
            transactionBean.reload();
        }
        // Create a Unit Parcel Group if there isn't one on the transaction
        if (transactionBean.getUnitParcelGroup().isNew() && unitDevelopmentNr != null) {
            // Retrieve the Unit Parcel Group for the given parcel from the database
            UnitParcelGroupBean group = UnitParcelGroupBean.getUnitParcelGroupByName(unitDevelopmentNr);
            if (group != null) {
                transactionBean.setUnitParcelGroup(group);
            }
        }

        if (transactionBean.getUnitParcelGroup().getName() == null) {
            // Set the name of the Unit Parcel Group of the application Bean has been provided. 
            transactionBean.getUnitParcelGroup().setName(unitDevelopmentNr);
        } else if (unitDevelopmentNr == null) {
            unitDevelopmentNr = transactionBean.getUnitParcelGroup().getName();
        }

        if (!readOnly && isRecordUnitPlan) {
            // Check if there is a common property parcel and if not, add one. 
            if (!hasCommonProperty()) {
                UnitParcelBean commonPropBean = new UnitParcelBean();
                commonPropBean.setTypeCode(CadastreObjectTypeBean.CODE_COMMON_PROPERTY);
                commonPropBean.setUnitParcelStatusCode(StatusConstants.PENDING);
                commonPropBean.setNameFirstpart(COMMON_PROPERTY_LOT_NR);
                commonPropBean.setNameLastpart(unitDevelopmentNr);
                transactionBean.getUnitParcelGroup().getUnitParcelList().addAsNew(commonPropBean);
            }
        }
        loadStrataProperties();
    }

    private void loadStrataProperties() {
        strataPropertyListBean.getStrataProperties(unitDevelopmentNr, baUnitIds);
    }

    /**
     * Checks if a common property is configured for the Unit Plan Development.
     *
     * @return
     */
    private boolean hasCommonProperty() {
        boolean result = false;
        if (transactionBean != null && transactionBean.getUnitParcelGroup() != null) {
            for (UnitParcelBean bean : transactionBean.getUnitParcelGroup().getFilteredUnitParcelList()) {
                if (CadastreObjectTypeBean.CODE_COMMON_PROPERTY.equals(bean.getTypeCode())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Customizes the Unit Parcel Buttons based on the selected UnitParcelBean.
     *
     * @param bean
     */
    private void customizeUnitParcelButtons(UnitParcelBean bean) {
        if (!isRecordUnitPlan) {
            return;
        }
        btnRemoveUnit.setEnabled(false);
        btnReinstateUnit.setEnabled(false);
        if (bean != null && !readOnly) {

            boolean officialAreaEdit = true;
            boolean nameFirstPartEdit = false;

            // Don't allow the Common Property to be removed from the Unit Plan. 
            if (!CadastreObjectTypeBean.CODE_COMMON_PROPERTY.equals(bean.getTypeCode())) {
                if (bean.isDeleteOnApproval()) {
                    // The parcel is flagged for removal. Do no let the user change the official area
                    btnReinstateUnit.setEnabled(true);
                    officialAreaEdit = false;
                } else {
                    btnRemoveUnit.setEnabled(true);
                }
            }

            // Pending parcels can have their Name First Part updated. 
            if (StatusConstants.PENDING.equals(bean.getUnitParcelStatusCode())) {
                nameFirstPartEdit = true;
            }

            // Control the columns that may be edited for the selected unit parcel. 
            JTableBinding tabBinding = (JTableBinding) bindingGroup.getBinding("bindTblUnits");
            if (tabBinding != null) {
                for (ColumnBinding colBinding : (List<ColumnBinding>) tabBinding.getColumnBindings()) {
                    if (colBinding.getColumnName().equals("Official Area")) {
                        colBinding.setEditable(officialAreaEdit);
                    } else if (colBinding.getColumnName().equals("Name Firstpart")) {
                        colBinding.setEditable(nameFirstPartEdit);
                    }
                }
            }
        }
    }

    /**
     * Sets the next available lot number for a unit parcel based on the type of
     * unit
     *
     * @param typeBean Bean indicating the type of unit to set the lot number
     * for.
     */
    private void customizeUnitParcelNr(CadastreObjectTypeBean typeBean) {
        txtUnitFirstPart.setText(calculateLotNumber(typeBean.getCode()));
    }

    /**
     * Loads the map with the underlying parcels and zooms to the area for the
     * application.
     */
    private void loadMap() {
        mapControl.setUnderlyingParcels(transactionBean.getUnitParcelGroup().getFilteredParcelList());
        byte[] applicationLocation = applicationBean == null ? null : applicationBean.getLocation();
        mapControl.zoomToTargetArea(applicationLocation);
    }

    /**
     * Calculates the next available Lot Number for a unit parcel based on the
     * unit parcel type code. Note that the lot number for unit parcels should
     * be of the form PU# for Principal Units and AU# for Accessory Units. The
     * user is able to change this name if it is not suitable.
     *
     * @param unitParcelTypeCode The type of unit to calculate the lot number
     * for.
     */
    private String calculateLotNumber(String unitParcelTypeCode) {
        String result = "";
        if (unitParcelTypeCode != null && !unitParcelTypeCode.trim().isEmpty()) {
            Integer lotNr = 0;
            for (UnitParcelBean bean : transactionBean.getUnitParcelGroup().getUnitParcelList()) {
                if (unitParcelTypeCode.equals(bean.getTypeCode())) {
                    // Assume the unit has a 2 character prefix (AU or PU)
                    String tmpStr = bean.getNameFirstpart().substring(LOT_NR_PREFIX_LEN).trim();
                    try {
                        Integer tmpInt = Integer.parseInt(tmpStr);
                        lotNr = lotNr < tmpInt ? tmpInt : lotNr;
                    } catch (NumberFormatException nfe) {
                        // Unable to convert the remainder of the lot number to a valid integer. 
                        // Ignore the error. 
                    }
                }
            }
            lotNr++;
            if (unitParcelTypeCode.equals(CadastreObjectTypeBean.CODE_PRINCIPAL_UNIT)) {
                result = PRINCIPAL_UNIT_LOT_NR_PREFIX + lotNr.toString();
            } else {
                result = ACCESSORY_UNIT_LOT_NR_PREFIX + lotNr.toString();
            }
        }
        return result;
    }

    /**
     * Checks the underlying parcels displayed in the map and synchronizes the
     * Unit Plan Parcel List with any changes. Returns true if the list of
     * underlying parcels is changed.
     */
    private boolean updateUnderlyingParcels() {
        boolean result = false;
        List<String> parcelIds = this.mapControl.getUnderlyingParcels();
        for (UnitParcelBean bean : transactionBean.getUnitParcelGroup().getParcelList()) {
            if (!parcelIds.contains(bean.getId())) {
                // The underlying parcel is being removed from the unit plan, so check if it
                // should be deleted immediately (if the link is only pending) or deleted on approval
                if (StatusConstants.PENDING.equals(bean.getUnitParcelStatusCode())) {
                    bean.setEntityAction(EntityAction.DISASSOCIATE);
                } else {
                    bean.setDeleteOnApproval(true);
                    bean.setEntityAction(EntityAction.UPDATE);
                }
                result = true;
            } else {
                // Make sure the parcel is not flagged for removal
                bean.setEntityAction(null);
                bean.setDeleteOnApproval(false);
            }
        }
        // Check for any new underlying parcels to add to the list. 
        for (String underlyingParcelId : parcelIds) {
            UnitParcelBean newBean = new UnitParcelBean();
            newBean.setId(underlyingParcelId);
            newBean.setUnitParcelStatusCode(StatusConstants.PENDING);
            if (!transactionBean.getUnitParcelGroup().getParcelList().contains(newBean)) {
                transactionBean.getUnitParcelGroup().getParcelList().addAsNew(newBean);
                result = true;
            }
        }
        return result;
    }

    /**
     * Save the Unit Plan transaction to the database. Triggered by btnSave
     */
    private void saveTransaction() {

        // Get the list of underlying parcels from the map adn update as required to track changes
        // to the underlying parcels. 
        final boolean updateUnderlying = updateUnderlyingParcels();

        final List result = new ArrayList<ValidationResultBean>();
        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));
                // Capture the validation messages from the save - if any. 
                result.addAll(transactionBean.saveTransaction());
                // Reload the transactionBean with the updated details. 
                transactionBean.reload();
                if (updateUnderlying) {
                    loadStrataProperties();
                }
                return null;
            }

            @Override
            public void taskDone() {
                String message = MessageUtility.getLocalizedMessageText(ClientMessage.GENERAL_SAVE_SUCCESSFUL);
                // Show the validation results form. if there was a critical validation failure, the
                // services would have thrown an exception, so assume the validation passed. 
                ValidationResultForm resultForm = new ValidationResultForm(
                        null, true, result, true, message);
                resultForm.setLocationRelativeTo(tabPane);
                resultForm.setVisible(true);
                // Update the lot number in case a lot was deleted during the save. 
                if (cadastreObjectTypeListBean1.getSelectedCadastreObjectType() != null) {
                    customizeUnitParcelNr(cadastreObjectTypeListBean1.getSelectedCadastreObjectType());
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Adds a new unit parcel to the list of unit parcels for the development.
     * Triggered by btnAddUnit.
     */
    private void addUnitParcel() {
        // Onlly create a new unit if the lot number and parcel type are set
        if (txtUnitFirstPart.getText() != null && !txtUnitFirstPart.getText().trim().isEmpty()
                && cadastreObjectTypeListBean1.getSelectedCadastreObjectType() != null) {
            UnitParcelBean bean = new UnitParcelBean();
            bean.setNameFirstpart(txtUnitFirstPart.getText());
            bean.setNameLastpart(unitDevelopmentNr);
            bean.setUnitParcelStatusCode(StatusConstants.PENDING);
            bean.setCadastreObjectType(cadastreObjectTypeListBean1.getSelectedCadastreObjectType());
            if (txtUnitArea.getText() != null && !txtUnitArea.getText().trim().isEmpty()) {
                bean.setOfficialArea(txtUnitArea.getText());
            }
            transactionBean.getUnitParcelGroup().getUnitParcelList().addAsNew(bean);
            transactionBean.getUnitParcelGroup().setSelectedUnitParcel(bean);
            // Reset the lot number to the next avaiable number. 
            customizeUnitParcelNr(bean.getCadastreObjectType());
        }
    }

    /**
     * Removes a unit parcel from the Unit Plan Development. Triggered by
     * btnRemoveUnit.
     */
    private void removeUnitParcel() {
        UnitParcelBean bean = transactionBean.getUnitParcelGroup().getSelectedUnitParcel();
        if (StatusConstants.PENDING.equals(bean.getUnitParcelStatusCode())) {
            // Mark the parcel for deletion and force the unit parcel list to be filtered so that
            // the deleted parcel is removed from the list. 
            bean.setEntityAction(EntityAction.DELETE);
            transactionBean.getUnitParcelGroup().getUnitParcelList().filter();
        } else {
            // The unit parcel cannot be deleted. Instead it must be disassociated from the
            // Unit Plan when the transaction is approved. 
            bean.setDeleteOnApproval(true);
        }
        transactionBean.getUnitParcelGroup().setSelectedUnitParcel(null);
    }

    /**
     * Reverses the delete on approval flag if set for a unit parcel. Triggered
     * by the btnReinstateUnit button
     */
    private void reinstateUnitParcel() {
        UnitParcelBean bean = transactionBean.getUnitParcelGroup().getSelectedUnitParcel();
        if (bean.isDeleteOnApproval()) {
            bean.setDeleteOnApproval(false);
        }
        transactionBean.getUnitParcelGroup().setSelectedUnitParcel(null);
    }

    /**
     * Creates the strata properties for the Unit Development
     */
    private void createStrataProperties() {
        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_CREATE_STRATA_PROPS));
                strataPropertyListBean.createStrataProperties(applicationService.getId(),
                        unitDevelopmentNr, baUnitIds);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Sets the cancellation state for the strata properties depending on
     * whether the properties are pending cancellation or not. If they are
     * pending cancellation, then the cancellation will be reversed.
     */
    private void setPropertyCancellationState() {
        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                if (strataPropertyListBean.isPendingCancellation()) {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_UNDO_CANCEL_STRATA_PROPS));
                    strataPropertyListBean.undoTerminateStrataProperties(applicationService.getId(), unitDevelopmentNr, baUnitIds);
                } else {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_CANCEL_STRATA_PROPS));
                    strataPropertyListBean.terminateStrataProperties(applicationService.getId(), unitDevelopmentNr, baUnitIds);
                }
                return null;
            }

            @Override
            public void taskDone() {
                customizeTerminateButton();
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens the property form for the selected property
     *
     * @param forEdit Indicates if the property form should be opened in edit
     * mode or not.
     */
    private void openPropertyForm(final boolean forEdit) {
        if (strataPropertyListBean.getSelectedStrataProperty() != null) {
            SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                    PropertyPanel propertyPnl = new PropertyPanel(applicationBean,
                            applicationService, strataPropertyListBean.getSelectedStrataProperty().getNameFirstpart(),
                            strataPropertyListBean.getSelectedStrataProperty().getNameLastpart(), !forEdit);
                    getMainContentPanel().addPanel(propertyPnl, MainContentPanel.CARD_PROPERTY_PANEL, true);
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel3 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        transactionBean = createTransactionBean();
        cadastreObjectTypeListBean1 = new org.sola.clients.beans.referencedata.CadastreObjectTypeListBean();
        strataPropertyListBean = createPropertyListBean();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        unitsTab = new javax.swing.JPanel();
        pnlUnits = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnAddUnit = new javax.swing.JButton();
        btnRemoveUnit = new javax.swing.JButton();
        btnReinstateUnit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUnits = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtUnitFirstPart = new javax.swing.JTextField();
        txtUnitLastPart = new javax.swing.JTextField();
        txtUnitArea = new javax.swing.JFormattedTextField();
        cbxParcelType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        propertyTab = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnOpenProperty = new javax.swing.JButton();
        btnCreateProperties = new javax.swing.JButton();
        btnEditProperty = new javax.swing.JButton();
        btnTerminateProperties = new javax.swing.JButton();
        btnRefreshProperties = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProperties = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        mapTab = new javax.swing.JPanel();
        mapPanel = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setHeaderPanel(headerPanel1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/unittitle/Bundle"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("UnitParcelsPanel.headerPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("UnitParcelsPanel.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnAddUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddUnit.setText(bundle.getString("UnitParcelsPanel.btnAddUnit.text")); // NOI18N
        btnAddUnit.setFocusable(false);
        btnAddUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUnitActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAddUnit);

        btnRemoveUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveUnit.setText(bundle.getString("UnitParcelsPanel.btnRemoveUnit.text")); // NOI18N
        btnRemoveUnit.setFocusable(false);
        btnRemoveUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveUnitActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveUnit);

        btnReinstateUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/undo.png"))); // NOI18N
        btnReinstateUnit.setText(bundle.getString("UnitParcelsPanel.btnReinstateUnit.text")); // NOI18N
        btnReinstateUnit.setFocusable(false);
        btnReinstateUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReinstateUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReinstateUnitActionPerformed(evt);
            }
        });
        jToolBar2.add(btnReinstateUnit);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${unitParcelGroup.filteredUnitParcelList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, transactionBean, eLProperty, tblUnits, "bindTblUnits");
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${officialArea}"));
        columnBinding.setColumnName("Official Area");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectType.displayValue}"));
        columnBinding.setColumnName("Cadastre Object Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${unitParcelStatus.displayValue}"));
        columnBinding.setColumnName("Unit Parcel Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${deleteOnApproval}"));
        columnBinding.setColumnName("Delete On Approval");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, transactionBean, org.jdesktop.beansbinding.ELProperty.create("${unitParcelGroup.selectedUnitParcel}"), tblUnits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tblUnits);
        if (tblUnits.getColumnModel().getColumnCount() > 0) {
            tblUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title0_1")); // NOI18N
            tblUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title1_1")); // NOI18N
            tblUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title5_1")); // NOI18N
            tblUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title2_1")); // NOI18N
            tblUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title3_1")); // NOI18N
            tblUnits.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("UnitParcelsPanel.tblUnits.columnModel.title4_1")); // NOI18N
        }

        txtUnitFirstPart.setText(bundle.getString("UnitParcelsPanel.txtUnitFirstPart.text")); // NOI18N

        txtUnitLastPart.setEditable(false);
        txtUnitLastPart.setText(bundle.getString("UnitParcelsPanel.txtUnitLastPart.text")); // NOI18N

        txtUnitArea.setText(bundle.getString("UnitParcelsPanel.txtUnitArea.text")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectTypeListBean1, eLProperty, cbxParcelType, "");
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectTypeListBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedCadastreObjectType}"), cbxParcelType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel1.setText(bundle.getString("UnitParcelsPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("UnitParcelsPanel.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("UnitParcelsPanel.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("UnitParcelsPanel.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUnitFirstPart)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUnitLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUnitArea))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(cbxParcelType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUnitFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUnitLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUnitArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxParcelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67))
        );

        javax.swing.GroupLayout pnlUnitsLayout = new javax.swing.GroupLayout(pnlUnits);
        pnlUnits.setLayout(pnlUnitsLayout);
        pnlUnitsLayout.setHorizontalGroup(
            pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlUnitsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlUnitsLayout.setVerticalGroup(
            pnlUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnitsLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout unitsTabLayout = new javax.swing.GroupLayout(unitsTab);
        unitsTab.setLayout(unitsTabLayout);
        unitsTabLayout.setHorizontalGroup(
            unitsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlUnits, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        unitsTabLayout.setVerticalGroup(
            unitsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlUnits, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabPane.addTab(bundle.getString("UnitParcelsPanel.unitsTab.TabConstraints.tabTitle"), unitsTab); // NOI18N

        propertyTab.setVerifyInputWhenFocusTarget(false);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnOpenProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenProperty.setText(bundle.getString("UnitParcelsPanel.btnOpenProperty.text")); // NOI18N
        btnOpenProperty.setFocusable(false);
        btnOpenProperty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenPropertyActionPerformed(evt);
            }
        });
        jToolBar3.add(btnOpenProperty);

        btnCreateProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/create.png"))); // NOI18N
        btnCreateProperties.setText(bundle.getString("UnitParcelsPanel.btnCreateProperties.text")); // NOI18N
        btnCreateProperties.setFocusable(false);
        btnCreateProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCreateProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreatePropertiesActionPerformed(evt);
            }
        });
        jToolBar3.add(btnCreateProperties);

        btnEditProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditProperty.setText(bundle.getString("UnitParcelsPanel.btnEditProperty.text")); // NOI18N
        btnEditProperty.setFocusable(false);
        btnEditProperty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditPropertyActionPerformed(evt);
            }
        });
        jToolBar3.add(btnEditProperty);

        btnTerminateProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/stop.png"))); // NOI18N
        btnTerminateProperties.setText(bundle.getString("UnitParcelsPanel.btnTerminateProperties.text")); // NOI18N
        btnTerminateProperties.setFocusable(false);
        btnTerminateProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTerminateProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminatePropertiesActionPerformed(evt);
            }
        });
        jToolBar3.add(btnTerminateProperties);

        btnRefreshProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnRefreshProperties.setText(bundle.getString("UnitParcelsPanel.btnRefreshProperties.text")); // NOI18N
        btnRefreshProperties.setFocusable(false);
        btnRefreshProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshPropertiesActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRefreshProperties);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${strataPropertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, strataPropertyListBean, eLProperty, tblProperties);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${officialArea}"));
        columnBinding.setColumnName("Official Area");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${unitEntitlement}"));
        columnBinding.setColumnName("Unit Entitlement");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${unitParcelType.displayValue}"));
        columnBinding.setColumnName("Unit Parcel Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, strataPropertyListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedStrataProperty}"), tblProperties, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(tblProperties);
        if (tblProperties.getColumnModel().getColumnCount() > 0) {
            tblProperties.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title0")); // NOI18N
            tblProperties.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title1")); // NOI18N
            tblProperties.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title2")); // NOI18N
            tblProperties.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title3")); // NOI18N
            tblProperties.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title4")); // NOI18N
            tblProperties.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title6")); // NOI18N
            tblProperties.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("UnitParcelsPanel.tblProperties.columnModel.title7")); // NOI18N
        }

        javax.swing.GroupLayout propertyTabLayout = new javax.swing.GroupLayout(propertyTab);
        propertyTab.setLayout(propertyTabLayout);
        propertyTabLayout.setHorizontalGroup(
            propertyTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertyTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE))
                .addContainerGap())
        );
        propertyTabLayout.setVerticalGroup(
            propertyTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPane.addTab(bundle.getString("UnitParcelsPanel.propertyTab.TabConstraints.tabTitle"), propertyTab); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 957, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mapTabLayout = new javax.swing.GroupLayout(mapTab);
        mapTab.setLayout(mapTabLayout);
        mapTabLayout.setHorizontalGroup(
            mapTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 957, Short.MAX_VALUE)
            .addGroup(mapTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mapPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mapTabLayout.setVerticalGroup(
            mapTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
            .addGroup(mapTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mapPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPane.addTab(bundle.getString("UnitParcelsPanel.mapTab.TabConstraints.tabTitle"), mapTab); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabPane))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveTransaction();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUnitActionPerformed
        addUnitParcel();
    }//GEN-LAST:event_btnAddUnitActionPerformed

    private void btnRemoveUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveUnitActionPerformed
        removeUnitParcel();
    }//GEN-LAST:event_btnRemoveUnitActionPerformed

    private void btnReinstateUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReinstateUnitActionPerformed
        reinstateUnitParcel();
    }//GEN-LAST:event_btnReinstateUnitActionPerformed

    private void btnCreatePropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreatePropertiesActionPerformed
        createStrataProperties();
    }//GEN-LAST:event_btnCreatePropertiesActionPerformed

    private void btnOpenPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenPropertyActionPerformed
        openPropertyForm(false);
    }//GEN-LAST:event_btnOpenPropertyActionPerformed

    private void btnEditPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditPropertyActionPerformed
        openPropertyForm(true);
    }//GEN-LAST:event_btnEditPropertyActionPerformed

    private void btnRefreshPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshPropertiesActionPerformed
        loadStrataProperties();
    }//GEN-LAST:event_btnRefreshPropertiesActionPerformed

    private void btnTerminatePropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminatePropertiesActionPerformed
        setPropertyCancellationState();
    }//GEN-LAST:event_btnTerminatePropertiesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUnit;
    private javax.swing.JButton btnCreateProperties;
    private javax.swing.JButton btnEditProperty;
    private javax.swing.JButton btnOpenProperty;
    private javax.swing.JButton btnRefreshProperties;
    private javax.swing.JButton btnReinstateUnit;
    private javax.swing.JButton btnRemoveUnit;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTerminateProperties;
    private org.sola.clients.beans.referencedata.CadastreObjectTypeListBean cadastreObjectTypeListBean1;
    private javax.swing.JComboBox cbxParcelType;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    public org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JPanel mapTab;
    private javax.swing.JPanel pnlUnits;
    private javax.swing.JPanel propertyTab;
    private org.sola.clients.beans.unittitle.StrataPropertyListBean strataPropertyListBean;
    private javax.swing.JTabbedPane tabPane;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblProperties;
    private javax.swing.JTable tblUnits;
    public org.sola.clients.beans.transaction.TransactionUnitParcelsBean transactionBean;
    private javax.swing.JFormattedTextField txtUnitArea;
    private javax.swing.JTextField txtUnitFirstPart;
    private javax.swing.JTextField txtUnitLastPart;
    private javax.swing.JPanel unitsTab;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
