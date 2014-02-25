
UPDATE LVE_PersonType SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_TaxUnit SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WC_ProductCharge SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_Combination SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_Concept SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_Relation SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_Type SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_ConceptGroup SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_Withholding SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;
UPDATE LVE_WH_Config SET AD_Org_ID = 0 WHERE AD_Org_ID = 50001;

UPDATE LVE_PersonType SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_TaxUnit SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WC_ProductCharge SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_Combination SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_Concept SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_Relation SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_Type SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_ConceptGroup SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_Withholding SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 
UPDATE LVE_WH_Config SET AD_Client_ID = 1000000 WHERE AD_Client_ID =11; 

UPDATE C_DocType SET AD_Client_ID = 1000000 WHERE C_DocType_ID IN (50016, 50015, 50017,50014);

UPDATE C_DocType_Trl SET AD_Client_ID = 1000000 WHERE C_DocType_ID IN ( 50015,50016,50017,50014,50014);


UPDATE AD_PrintFormat SET AD_Client_ID = 1000000, AD_Org_ID = 0 WHERE AD_PrintFormat_ID IN (50111,50110,50112,50113,50114,50115);

UPDATE AD_PrintFormatItem SET AD_Client_ID = 1000000, AD_Org_ID = 0 WHERE AD_PrintFormat_ID IN (50111,50110,50112,50113,50114,50115);
UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000, AD_Org_ID = 0
WHERE AD_PrintFormatItem_ID IN (
SELECT AD_PrintFormatItem_ID FROM AD_PrintFormatItem 
WHERE AD_PrintFormat_ID IN (SELECT AD_PrintFormat_ID FROM AD_PrintFormat
WHERE AD_ReportView_ID IN (SELECT AD_ReportView_ID From AD_ReportView WHERE EntityType = 'ECA02')
)
);

UPDATE AD_PrintFormatItem_Trl SET AD_Client_ID = 1000000, AD_Org_ID = 0 WHERE AD_PrintFormatItem_ID IN (
SELECT AD_PrintFormatItem_ID FROM AD_PrintFormatItem_Trl  WHERE AD_PrintFormatItem_ID IN (
SELECT AD_PrintFormatItem_ID FROM AD_PrintFormatItem WHERE AD_PrintFormat_ID IN (50110,50112,50113,50111)
));
