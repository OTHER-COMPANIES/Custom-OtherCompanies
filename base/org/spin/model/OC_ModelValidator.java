package org.spin.model;

import org.compiere.model.MBPBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPayment;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

/**
 * Other Companies Model Validator
 * @author <a href="mailto:yamelsenih@gmail.com">Yamel Senih</a>
 *  
 */
public class OC_ModelValidator implements org.compiere.model.ModelValidator {

	/**
	 * Constructor.
	 */
	public OC_ModelValidator() {
		super();
	} // ModelValidator

	/** Logger */
	private static CLogger log = CLogger.getCLogger(OC_ModelValidator.class);
	/** Client */
	private int m_AD_Client_ID = -1;
	
	/**
	 * Initialize Validation
	 * 
	 * @param engine
	 *            validation engine
	 * @param client
	 *            client
	 */
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		// client = null for global validator
		if (client != null) {
			m_AD_Client_ID = client.getAD_Client_ID();
			log.info(client.toString());
		} else {
			log.info("Initializing global validator: " + this.toString());
		}
		//	Add Listener for Payment Doc Validate
		engine.addDocValidate(MPayment.Table_Name, this);
		engine.addModelChange(MPaySelectionCheck.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return m_AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		log.info("AD_User_ID=" + AD_User_ID);
		return null;
	}

	/**
	 * Model Change of a monitored Table. Called after
	 * PO.beforeSave/PO.beforeDelete when you called addModelChange for the
	 * table
	 * 
	 * @param po
	 *            persistent object
	 * @param type
	 *            TYPE_
	 * @return error message or null
	 * @exception Exception
	 *                if the recipient wishes the change to be not accept.
	 */
	public String modelChange(PO po, int type) throws Exception {
		//Carlos Parada Set BP_BankAccount to PaySelection if have Payment
		if ((type == TYPE_AFTER_CHANGE || type == TYPE_AFTER_NEW)&& po.get_TableName().equals(MPaySelectionCheck.Table_Name)){
			MPaySelectionCheck psch = (MPaySelectionCheck) po;
			if (psch.getC_Payment_ID()!=0 && psch.getC_BP_BankAccount_ID()==0){
				MBPBankAccount[] bpas = MBPBankAccount.getOfBPartner (Env.getCtx(), psch.getC_BPartner_ID());
				
				for (int i =0;i<bpas.length;i++)
					if (psch.getPaymentRule().equals(bpas[i].get_ValueAsString("PaymentRule")) &&
							bpas[i].get_ValueAsBoolean("IsDefault")){
						psch.setC_BP_BankAccount_ID(bpas[i].getC_BP_BankAccount_ID());
						psch.save();
					}
			}
				
		}
		return null;
	} // modelChange

	@Override
	public String docValidate(PO po, int timing) {
		// TODO Auto-generated method stub
		return null;
	}

	//Carlos Parada Comment Rewrite in new Process
	/*
	@SuppressWarnings("unused")
	@Override
	public String docValidate(PO po, int timing) {
		if(timing == TIMING_AFTER_COMPLETE){
			if(po.get_TableName().equals(MPayment.Table_Name)){
				log.fine(MPayment.Table_Name + " -- TIMING_AFTER_COMPLETE");
				MPayment payment = (MPayment) po;
				//Dixon Martinez
				if(payment.getReversal_ID() != 0)
					return null;
				boolean ok = MPaySelectionCheck.deleteGeneratedDraft(Env.getCtx(), payment.getC_Payment_ID(),payment.get_TrxName());
				//
				int C_PaySelectionCheck_ID = 0;
				MPaySelectionCheck psc = MPaySelectionCheck.getOfPayment(Env.getCtx(), payment.getC_Payment_ID(), payment.get_TrxName());
				
				if (psc != null)
					C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
				else
				{
					//if(payment.getTenderType() != null)
						//return null;
					psc = MPaySelectionCheck.createForPayment(Env.getCtx(), payment.getC_Payment_ID(), payment.get_TrxName());
					if (psc != null)
						C_PaySelectionCheck_ID = psc.getC_PaySelectionCheck_ID();
				}
				
				log.fine(MPayment.Table_Name + " -- TIMING_AFTER_COMPLETE -----> C_PaySelectionCheck_ID=" + C_PaySelectionCheck_ID);
				
			}
		} else if(timing == TIMING_BEFORE_REVERSECORRECT || timing == TIMING_BEFORE_VOID){
			if(po.get_TableName().equals(MPayment.Table_Name)){
				log.fine(MPayment.Table_Name + " -- TIMING_BEFORE_REVERSECORRECT || TIMING_BEFORE_VOID");
			}
		}
		return null;
	}
	*/

}
