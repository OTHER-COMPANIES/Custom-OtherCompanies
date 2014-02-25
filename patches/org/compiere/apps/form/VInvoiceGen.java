/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.compiere.apps.form;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.grid.ed.VComboBox;
import org.compiere.grid.ed.VLookup;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MOrder;
import org.compiere.model.MRMA;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

/**
 * Generate Invoice (manual) view class
 * 
 */
public class VInvoiceGen extends InvoiceGen implements FormPanel, ActionListener, VetoableChangeListener
{
	private VGenPanel panel;
	
	/**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	FormFrame			*/
	private FormFrame 		m_frame;
	/**	North Panel			*/
	private CPanel northPanel = new CPanel();
	/**	Layout				*/
	private GridBagLayout 	parameterLayout = new GridBagLayout();

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(VInvoiceGen.class);
	//

	private CLabel lOrg = new CLabel();
	private VLookup fOrg;
	private CLabel lBPartner = new CLabel();
	private VLookup fBPartner;	
	private CLabel     lDocType = new CLabel();
	private CLabel     lDocumentType = new CLabel();
	private CLabel     lIsImmediateDelivery = new CLabel();
	private VComboBox  cmbDocType = new VComboBox();
	private VComboBox  cmbDocumentType = new VComboBox();
	private VComboBox  cmbIsImmediateDelivery = new VComboBox();
	private CLabel     lDocAction = new CLabel();
	private VLookup    docAction;
	
	/**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		log.info("");
		m_WindowNo = WindowNo;
		m_frame = frame;
		Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "Y");

		panel = new VGenPanel(this, WindowNo, frame);
		//	
		northPanel = panel.getParameterPanel();
		northPanel.setLayout(parameterLayout);
		//northPanel
		try
		{
			super.dynInit();
			dynInit();
			jbInit();
			//	Initial Load
			initialLoad();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "init", ex);
		}
	}	//	init
	
	/**
	 * @author Yamel Senih
	 * Add Initial Load
	 */
	private void initialLoad(){
		m_AD_Org_ID = fOrg.getValue();
		executeQuery();
	}
	
	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
	}	//	dispose
	
	/**
	 *	Static Init.
	 *  <pre>
	 *  selPanel (tabbed)
	 *      fOrg, fBPartner
	 *      scrollPane & miniTable
	 *  genPanel
	 *      info
	 *  </pre>
	 *  @throws Exception
	 */
	void jbInit() throws Exception
	{
		lOrg.setLabelFor(fOrg);
		lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		lBPartner.setLabelFor(fBPartner);
		lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		lDocAction.setLabelFor(docAction);
		lDocAction.setText(Msg.translate(Env.getCtx(), "DocAction"));
		lDocType.setLabelFor(cmbDocType);
		lDocumentType.setLabelFor(cmbDocumentType);
		lIsImmediateDelivery.setLabelFor(cmbIsImmediateDelivery);
		
		northPanel.add(lOrg, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(fOrg, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(lBPartner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(fBPartner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(lDocType, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(cmbDocType, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(lDocumentType, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(cmbDocumentType, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(lIsImmediateDelivery, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(cmbIsImmediateDelivery, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(lDocAction, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		northPanel.add(docAction, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}	//	jbInit
	
	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		MLookup orgL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2163, DisplayType.TableDir);
		fOrg = new VLookup ("AD_Org_ID", false, false, true, orgL);
		fOrg.setValue(Env.getAD_Org_ID(Env.getCtx()));
		//	lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		fOrg.addVetoableChangeListener(this);
		
		MLookup docActionL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 3494 /* C_Invoice.DocStatus */, 
				DisplayType.List, Env.getLanguage(Env.getCtx()), "DocAction", 135 /* _Document Action */,
				false, "AD_Ref_List.Value IN ('CO','PR')");
		docAction = new VLookup("DocAction", true, false, true,docActionL);
		docAction.setValue("CO");
		//  lDcoACtion.setText((Msg.translate(Env.getCtx(), "DocAction")););
		docAction.addVetoableChangeListener(this);
		
		//
		MLookup bpL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2762, DisplayType.Search);
		fBPartner = new VLookup ("C_BPartner_ID", false, false, true, bpL);
		//lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fBPartner.addVetoableChangeListener(this);
		
		//Document Type Sales Order/Vendor RMA
        lDocType.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));
        cmbDocType.addItem(new KeyNamePair(MOrder.Table_ID, Msg.translate(Env.getCtx(), "Order")));
        cmbDocType.addItem(new KeyNamePair(MRMA.Table_ID, Msg.translate(Env.getCtx(), "CustomerRMA")));
        cmbDocType.addActionListener(this);
        //	
        lIsImmediateDelivery.setText(Msg.translate(Env.getCtx(), "IsImmediateDelivery"));
        cmbIsImmediateDelivery.addItem(new KeyNamePair(0, ""));
        cmbIsImmediateDelivery.addItem(new KeyNamePair(1, Msg.translate(Env.getCtx(), "yes")));
        cmbIsImmediateDelivery.addItem(new KeyNamePair(2, Msg.translate(Env.getCtx(), "no")));
        cmbIsImmediateDelivery.addActionListener(this);
        //	
        lDocumentType.setText(Msg.translate(Env.getCtx(), "C_DocTypeTarget_ID"));
        cmbDocumentType.addActionListener(this);
        //	Load Document Type
        loadDocumentType();
        
        panel.getStatusBar().setStatusLine(Msg.getMsg(Env.getCtx(), "InvGenerateSel"));//@@
	}	//	fillPicks
	
	public void executeQuery()
	{
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
		//	Yamel Senih 2013-11-09, 10:56:09
		//	Add Document Type
		KeyNamePair documentTypeKNPair = (KeyNamePair)cmbDocumentType.getSelectedItem();
		if(documentTypeKNPair != null)
			m_C_DocTypeTarget_ID = documentTypeKNPair.getKey();
		else
			m_C_DocTypeTarget_ID = 0;
		
		KeyNamePair immDKNPair = (KeyNamePair)cmbIsImmediateDelivery.getSelectedItem();
		m_ImmediateDelivery = immDKNPair.getKey();
		
		//	End Yamel Senih
		executeQuery(docTypeKNPair, panel.getMiniTable());
	}   //  executeQuery
	
	/**
	 *	Action Listener
	 *  @param e event
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (cmbDocType.equals(e.getSource()))
		{
			loadDocumentType();
			return;
		} else if(cmbDocumentType.equals(e.getSource())
				|| cmbIsImmediateDelivery.equals(e.getSource())) {
			executeQuery();
		    return;
		}
		
		validate();
	}	//	actionPerformed
	
	public void validate()
	{
		panel.saveSelection();
		
		ArrayList<Integer> selection = getSelection();
		if (selection != null && selection.size() > 0 && isSelectionActive())		
			panel.generate();
		else
			panel.dispose();
	}

	/**
	 *	Vetoable Change Listener - requery
	 *  @param e event
	 */
	public void vetoableChange(PropertyChangeEvent e)
	{
		log.info(e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("AD_Org_ID"))
			m_AD_Org_ID = e.getNewValue();
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			m_C_BPartner_ID = e.getNewValue();
			fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		executeQuery();
	}	//	vetoableChange
	
	/**************************************************************************
	 *	Generate Shipments
	 */
	public String generate()
	{
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
		String docActionSelected = (String)docAction.getValue();	
		return generate(panel.getStatusBar(), docTypeKNPair, docActionSelected);
	}	//	generateShipments
	
	/**
	 * Load Document Type
	 */
	private void loadDocumentType(){
		cmbDocumentType.removeAllItems();
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
		ArrayList<KeyNamePair> bpartnerData = getDocTypeData(docTypeKNPair.getKey());
		for(KeyNamePair pp : bpartnerData)
			cmbDocumentType.addItem(pp);
		cmbDocumentType.setSelectedIndex(0);
	}
	
}
