/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.desktop.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.application.Action;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.swing.desktop.party.PartyPanel;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing ownership shares
 */
public class SharePanel extends ContentPanel {

    private class RightHolderFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PartyPanel.PARTY_SAVED)) {
                rrrShareBean.addOrUpdateRightholder((PartySummaryBean) 
                        ((PartyPanel)evt.getSource()).getParty());
                tableOwners.clearSelection();
            }
        }
    }
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR_SHARE = "updatedRrrShare";

    public SharePanel(RrrShareBean rrrShareBean, RrrBean.RRR_ACTION rrrAction) {
        this.rrrAction = rrrAction;
        prepareRrrShareBean(rrrShareBean);
    
        initComponents();
        
        customizeComponents();
        customizeForm(rrrAction);
        customizeOwnersButtons(null);
    }

    private RrrShareBean CreateRrrShareBean() {
        if (rrrShareBean == null) {
            rrrShareBean = new RrrShareBean();
        }
        return rrrShareBean;
    }

    private void prepareRrrShareBean(RrrShareBean rrrShareBean) {
        if (rrrShareBean == null) {
            this.rrrShareBean = new RrrShareBean();
        } else {
            this.rrrShareBean = rrrShareBean.copy();
        }
    }
     
       /** Applies customization of component L&F. */
    private void customizeComponents() {
   
//    BUTTONS   
    LafManager.getInstance().setBtnProperties(btnAddOwner);
    LafManager.getInstance().setBtnProperties(btnEditOwner);
    LafManager.getInstance().setBtnProperties(btnRemoveOwner);
    LafManager.getInstance().setBtnProperties(btnViewOwner);
    LafManager.getInstance().setBtnProperties(btnSave);
    
    
//    LABELS    
    LafManager.getInstance().setLabProperties(jLabel1);
    LafManager.getInstance().setLabProperties(jLabel2);
    
//    FORMATTED TXT
    LafManager.getInstance().setFormattedTxtProperties(txtDenominator);
    LafManager.getInstance().setFormattedTxtProperties(txtNominator);
    
    }
    
    private void customizeForm(RrrBean.RRR_ACTION rrrAction) {
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        } else if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setEnabled(false);
            txtNominator.setEditable(false);
            txtDenominator.setEditable(false);
            btnAddOwner.setEnabled(false);
            btnRemoveOwner.setEnabled(false);
        }

        rrrShareBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrShareBean.SELECTED_RIGHTHOLDER_PROPERTY)) {
                    customizeOwnersButtons((PartySummaryBean) evt.getNewValue());
                }
            }
        });
    }

    /** 
     * Enables or disables rightholders buttons, depending on 
     * selection in the list of rightholders and user rights. 
     */
    private void customizeOwnersButtons(PartySummaryBean party) {
        boolean isReadOnly = rrrAction == RrrBean.RRR_ACTION.VIEW;
        
        btnAddOwner.getAction().setEnabled(!isReadOnly);
        btnEditOwner.getAction().setEnabled(party != null && !isReadOnly);
        btnRemoveOwner.getAction().setEnabled(party != null && !isReadOnly);
        btnViewOwner.getAction().setEnabled(party != null);
    }

    private void openRightHolderForm(PartySummaryBean partySummaryBean, boolean isReadOnly) {
        PartyPanel partyForm;

        if (partySummaryBean != null) {
            partyForm = new PartyPanel(true, partySummaryBean, isReadOnly, true);
        } else {
            partyForm = new PartyPanel(true, null, isReadOnly, true);
        }

        RightHolderFormListener listener = new RightHolderFormListener();
        partyForm.addPropertyChangeListener(listener);
        getMainContentPanel().addPanel(partyForm, MainContentPanel.CARD_PERSON, true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrShareBean = CreateRrrShareBean();
        popupOwners = new javax.swing.JPopupMenu();
        menuAddOwner = new javax.swing.JMenuItem();
        menuRemoveOwner = new javax.swing.JMenuItem();
        menuEditOwner = new javax.swing.JMenuItem();
        menuViewOwner = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDenominator = new javax.swing.JFormattedTextField();
        txtNominator = new javax.swing.JFormattedTextField();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddOwner = new javax.swing.JButton();
        btnRemoveOwner = new javax.swing.JButton();
        btnEditOwner = new javax.swing.JButton();
        btnViewOwner = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOwners = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar2 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();

        popupOwners.setName("popupOwners"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SharePanel.class, this);
        menuAddOwner.setAction(actionMap.get("addOwner")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SharePanel.class);
        menuAddOwner.setText(resourceMap.getString("menuAddOwner.text")); // NOI18N
        menuAddOwner.setName("menuAddOwner"); // NOI18N
        popupOwners.add(menuAddOwner);

        menuRemoveOwner.setAction(actionMap.get("removeOwner")); // NOI18N
        menuRemoveOwner.setText(resourceMap.getString("menuRemoveOwner.text")); // NOI18N
        menuRemoveOwner.setName("menuRemoveOwner"); // NOI18N
        popupOwners.add(menuRemoveOwner);

        menuEditOwner.setAction(actionMap.get("editOwner")); // NOI18N
        menuEditOwner.setText(resourceMap.getString("menuEditOwner.text")); // NOI18N
        menuEditOwner.setName("menuEditOwner"); // NOI18N
        popupOwners.add(menuEditOwner);

        menuViewOwner.setAction(actionMap.get("viewOwner")); // NOI18N
        menuViewOwner.setText(resourceMap.getString("menuViewOwner.text")); // NOI18N
        menuViewOwner.setName("menuViewOwner"); // NOI18N
        popupOwners.add(menuViewOwner);

        setHeaderPanel(headerPanel);
        setName("Form"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(resourceMap.getString("jLabel2.toolTipText")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtDenominator.setFormatterFactory(FormattersFactory.getInstance().getShortFormatterFactory());
        txtDenominator.setName("txtDenominator"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${denominator}"), txtDenominator, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtNominator.setFormatterFactory(FormattersFactory.getInstance().getShortFormatterFactory());
        txtNominator.setName("txtNominator"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${nominator}"), txtNominator, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(resourceMap.getString("headerPanel.titleText")); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(resourceMap.getString("groupPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddOwner.setAction(actionMap.get("addOwner")); // NOI18N
        btnAddOwner.setText(resourceMap.getString("btnAddOwner.text")); // NOI18N
        btnAddOwner.setName("btnAddOwner"); // NOI18N
        jToolBar1.add(btnAddOwner);

        btnRemoveOwner.setAction(actionMap.get("removeOwner")); // NOI18N
        btnRemoveOwner.setText(resourceMap.getString("btnRemoveOwner.text")); // NOI18N
        btnRemoveOwner.setName("btnRemoveOwner"); // NOI18N
        jToolBar1.add(btnRemoveOwner);

        btnEditOwner.setAction(actionMap.get("editOwner")); // NOI18N
        btnEditOwner.setFocusable(false);
        btnEditOwner.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditOwner.setName("btnEditOwner"); // NOI18N
        btnEditOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEditOwner);

        btnViewOwner.setAction(actionMap.get("viewOwner")); // NOI18N
        btnViewOwner.setText(resourceMap.getString("btnViewOwner.text")); // NOI18N
        btnViewOwner.setName("btnViewOwner"); // NOI18N
        jToolBar1.add(btnViewOwner);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableOwners.setComponentPopupMenu(popupOwners);
        tableOwners.setName("tableOwners"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRightHolderList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, eLProperty, tableOwners);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lastName}"));
        columnBinding.setColumnName("Last Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${selectedRightHolder}"), tableOwners, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableOwners);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnSave.setIcon(resourceMap.getIcon("btnSave.icon")); // NOI18N
        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNominator, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDenominator, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(163, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtNominator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDenominator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (rrrShareBean.validate(true).size() < 1) {
            firePropertyChange(UPDATED_RRR_SHARE, null, rrrShareBean);
            close();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    @Action
    public void viewOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrShareBean.getSelectedRightHolder(), true);
        }
    }

    @Action
    public void removeOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrShareBean.removeSelectedRightHolder();
        }
    }

    @Action
    public void addOwner() {
        openRightHolderForm(null, false);
    }

    @Action
    public void editOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrShareBean.getSelectedRightHolder(), false);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddOwner;
    private javax.swing.JButton btnEditOwner;
    private javax.swing.JButton btnRemoveOwner;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnViewOwner;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuViewOwner;
    private javax.swing.JPopupMenu popupOwners;
    private org.sola.clients.beans.administrative.RrrShareBean rrrShareBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwners;
    private javax.swing.JFormattedTextField txtDenominator;
    private javax.swing.JFormattedTextField txtNominator;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}