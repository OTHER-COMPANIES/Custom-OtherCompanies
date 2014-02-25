-- DROP VIEW c_invoice_candidate_v;

CREATE OR REPLACE VIEW c_invoice_candidate_v AS 
 SELECT o.ad_client_id, o.ad_org_id, o.c_bpartner_id, o.c_order_id, 
    o.documentno, o.dateordered, o.c_doctype_id, 
    round(sum((l.qtyordered - l.qtyinvoiced) * l.priceactual), 2) AS totallines, 
    round(sum((l.qtyordered - l.qtyinvoiced) * l.priceactual) + sum((l.qtyordered - l.qtyinvoiced) * l.priceactual) * (tx.Rate/100), 2) GrandTotal,
    o.IsImmediateDelivery
   FROM c_order o
   JOIN c_orderline l ON o.c_order_id = l.c_order_id
   JOIN c_bpartner bp ON o.c_bpartner_id = bp.c_bpartner_id
   INNER JOIN C_Tax tx ON(tx.C_Tax_ID = l.C_Tax_ID)
   LEFT JOIN c_invoiceschedule si ON bp.c_invoiceschedule_id = si.c_invoiceschedule_id
  WHERE o.docstatus IN('CO', 'CL', 'IP') AND o.c_doctype_id IN ( SELECT c_doctype.c_doctype_id
   FROM c_doctype
  WHERE c_doctype.docbasetype = 'SOO' AND c_doctype.docsubtypeso NOT IN('ON', 'OB', 'WR')) 
  AND l.qtyordered <> l.qtyinvoiced AND (o.invoicerule = 'I' OR o.invoicerule = 'O' AND NOT (EXISTS ( SELECT 1
   FROM c_orderline zz1
  WHERE zz1.c_order_id = o.c_order_id AND zz1.qtyordered <> zz1.qtydelivered)) OR o.invoicerule = 'D' 
  AND l.qtyinvoiced <> l.qtydelivered OR o.invoicerule = 'S' AND bp.c_invoiceschedule_id IS NULL OR o.invoicerule = 'S' 
  AND bp.c_invoiceschedule_id IS NOT NULL AND (si.invoicefrequency IS NULL OR si.invoicefrequency = 'D' 
  OR si.invoicefrequency = 'W' OR si.invoicefrequency = 'T' 
  AND (trunc(o.dateordered) <= (firstof(getdate(), 'MM') + si.invoicedaycutoff - 1)
  AND trunc(getdate()) >= (firstof(o.dateordered, 'MM') + si.invoiceday - 1) 
  OR trunc(o.dateordered) <= (firstof(getdate(), 'MM') + si.invoicedaycutoff + 14) 
  AND trunc(getdate()) >= (firstof(o.dateordered, 'MM') + si.invoiceday + 14)) 
  OR si.invoicefrequency = 'M' AND trunc(o.dateordered) <= (firstof(getdate(), 'MM') + si.invoicedaycutoff - 1) 
  AND trunc(getdate()) >= (firstof(o.dateordered, 'MM') + si.invoiceday - 1)))
  GROUP BY o.ad_client_id, o.ad_org_id, o.c_bpartner_id, o.c_order_id, o.documentno, o.dateordered, o.c_doctype_id, tx.Rate, o.IsImmediateDelivery