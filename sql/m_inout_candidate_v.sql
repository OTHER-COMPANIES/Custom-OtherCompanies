﻿-- View: m_inout_candidate_v

-- DROP VIEW m_inout_candidate_v;

CREATE OR REPLACE VIEW m_inout_candidate_v AS 
 SELECT o.ad_client_id, o.ad_org_id, o.c_bpartner_id, o.c_order_id, 
    o.documentno, o.dateordered, o.c_doctype_id, o.poreference, o.description, 
    o.salesrep_id, l.m_warehouse_id, 
    sum((l.qtyordered - l.qtydelivered) * l.priceactual) AS totallines, o.IsImmediateDelivery
   FROM c_order o
   JOIN c_orderline l ON o.c_order_id = l.c_order_id
  WHERE o.docstatus = 'CO'::bpchar AND o.isdelivered = 'N'::bpchar AND (o.c_doctype_id IN ( SELECT c_doctype.c_doctype_id
      FROM c_doctype
     WHERE c_doctype.docbasetype = 'SOO'::bpchar AND (c_doctype.docsubtypeso <> ALL (ARRAY['ON'::bpchar, 'OB'::bpchar, 'WR'::bpchar])))) AND o.deliveryrule <> 'M'::bpchar AND (l.m_product_id IS NULL OR (EXISTS ( SELECT p.m_product_id, 
       p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, 
       p.updated, p.updatedby, p.value, p.name, p.description, p.documentnote, 
       p.help, p.upc, p.sku, p.c_uom_id, p.salesrep_id, p.issummary, 
       p.isstocked, p.ispurchased, p.issold, p.isbom, p.isinvoiceprintdetails, 
       p.ispicklistprintdetails, p.isverified, p.c_revenuerecognition_id, 
       p.m_product_category_id, p.classification, p.volume, p.weight, 
       p.shelfwidth, p.shelfheight, p.shelfdepth, p.unitsperpallet, 
       p.c_taxcategory_id, p.s_resource_id, p.discontinued, p.discontinuedby, 
       p.processing, p.s_expensetype_id, p.producttype, p.imageurl, 
       p.descriptionurl, p.guaranteedays, p.r_mailtext_id, p.versionno, 
       p.m_attributeset_id, p.m_attributesetinstance_id, p.downloadurl, 
       p.m_freightcategory_id, p.m_locator_id, p.guaranteedaysmin, 
       p.iswebstorefeatured, p.isselfservice, p.c_subscriptiontype_id, 
       p.isdropship, p.isexcludeautodelivery, p.group1, p.group2, p.istoformule, 
       p.lowlevel, p.unitsperpack, p.discontinuedat, p.copyfrom
      FROM m_product p
     WHERE l.m_product_id = p.m_product_id AND p.isexcludeautodelivery = 'N'::bpchar))) AND l.qtyordered <> l.qtydelivered AND o.isdropship = 'N'::bpchar AND (l.m_product_id IS NOT NULL OR l.c_charge_id IS NOT NULL) AND NOT (EXISTS ( SELECT iol.m_inoutline_id, 
       iol.ad_client_id, iol.ad_org_id, iol.isactive, iol.created, 
       iol.createdby, iol.updated, iol.updatedby, iol.line, iol.description, 
       iol.m_inout_id, iol.c_orderline_id, iol.m_locator_id, iol.m_product_id, 
       iol.c_uom_id, iol.movementqty, iol.isinvoiced, 
       iol.m_attributesetinstance_id, iol.isdescription, iol.confirmedqty, 
       iol.pickedqty, iol.scrappedqty, iol.targetqty, iol.ref_inoutline_id, 
       iol.processed, iol.qtyentered, iol.c_charge_id, iol.c_project_id, 
       iol.c_projectphase_id, iol.c_projecttask_id, iol.c_campaign_id, 
       iol.c_activity_id, iol.user1_id, iol.user2_id, iol.ad_orgtrx_id, 
       iol.m_rmaline_id, iol.reversalline_id, io.m_inout_id, io.ad_client_id, 
       io.ad_org_id, io.isactive, io.created, io.createdby, io.updated, 
       io.updatedby, io.issotrx, io.documentno, io.docaction, io.docstatus, 
       io.posted, io.processing, io.processed, io.c_doctype_id, io.description, 
       io.c_order_id, io.dateordered, io.isprinted, io.movementtype, 
       io.movementdate, io.dateacct, io.c_bpartner_id, 
       io.c_bpartner_location_id, io.m_warehouse_id, io.poreference, 
       io.deliveryrule, io.freightcostrule, io.freightamt, io.deliveryviarule, 
       io.m_shipper_id, io.c_charge_id, io.chargeamt, io.priorityrule, 
       io.dateprinted, io.c_invoice_id, io.createfrom, io.generateto, 
       io.sendemail, io.ad_user_id, io.salesrep_id, io.nopackages, io.pickdate, 
       io.shipdate, io.trackingno, io.ad_orgtrx_id, io.c_project_id, 
       io.c_campaign_id, io.c_activity_id, io.user1_id, io.user2_id, 
       io.datereceived, io.isintransit, io.ref_inout_id, io.createconfirm, 
       io.createpackage, io.isapproved, io.isindispute, io.volume, io.weight, 
       io.m_rma_id, io.reversal_id, io.isdropship, io.dropship_bpartner_id, 
       io.dropship_location_id, io.dropship_user_id, io.processedon
      FROM m_inoutline iol
   JOIN m_inout io ON iol.m_inout_id = io.m_inout_id
  WHERE iol.c_orderline_id = l.c_orderline_id AND (io.docstatus = ANY (ARRAY['IP'::bpchar, 'WC'::bpchar]))))
  GROUP BY o.ad_client_id, o.ad_org_id, o.c_bpartner_id, o.c_order_id, o.documentno, o.dateordered, o.c_doctype_id, o.poreference, o.description, o.salesrep_id, l.m_warehouse_id, o.IsImmediateDelivery