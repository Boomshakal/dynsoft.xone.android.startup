package dynsoft.xone.android.sbo;

public class DocType {
    
    public int TypeID;
    public String TypeName;
    public String MainTable;
    public String LineTable;
    public String CardType;
    public String ObjectName;
    public String BodyName;
    public String LineName1;
    public String LineName2;

    public DocType(int typeID, String typeName, String mainTable, String lineTable, String cardType, String objectName, String bodyName, String lineName1, String lineName2)
    {
        this.TypeID = typeID;
        this.TypeName = typeName;
        this.MainTable = mainTable;
        this.LineTable = lineTable;
        this.CardType = cardType;
        this.ObjectName = objectName;
        this.BodyName = bodyName;
        this.LineName1 = lineName1;
        this.LineName2 = lineName2;
    }
    
    public final static DocType NONE = new DocType(-1, "", "", "", "", "", "", "", "");
    
    public final static DocType OQUT = new DocType(23, "���۱���", "OQUT", "QUT1", "C", "oQuotations", "Documents", "Document_Lines", "");
    public final static DocType ORDR = new DocType(17, "���۶���", "ORDR", "RDR1", "C", "oOrders", "Documents", "Document_Lines", "");
    public final static DocType ORDN = new DocType(16, "�����˻�", "ORDN", "RDN1", "C", "oReturns", "Documents", "Document_Lines", "");
    public final static DocType ODLN = new DocType(15, "���۽���", "ODLN", "DLN1", "C", "oDeliveryNotes", "Documents", "Document_Lines", "");
    public final static DocType OINV = new DocType(13, "���۷�Ʊ", "OINV", "INV1", "C", "oInvoices", "Documents", "Document_Lines", "");
    
    public final static DocType OPOR = new DocType(22, "�ɹ�����", "OPOR", "POR1", "S", "oPurchaseOrders", "Documents", "Document_Lines", "");
    public final static DocType OPDN = new DocType(20, "�ɹ��ջ�", "OPDN", "PDN1", "S", "oPurchaseDeliveryNotes", "Documents", "Document_Lines", "");
    public final static DocType OPCH = new DocType(18, "Ԥ����Ʊ", "OPCH", "PCH1", "S", "oPurchaseInvoices", "Documents", "Document_Lines", "");
    public final static DocType ORPD = new DocType(21, "�ɹ��˻�", "ORPD", "RPD1", "S", "oPurchaseReturns", "Documents", "Document_Lines", "");
    
    public final static DocType OIGN = new DocType(59, "����ջ�", "OIGN", "IGN1", "", "oInventoryGenEntry", "Documents", "Document_Lines", "");
    public final static DocType OIGE = new DocType(60, "��淢��", "OIGE", "IGE1", "", "oInventoryGenExit", "Documents", "Document_Lines", "");
    public final static DocType OWTR = new DocType(67, "���ת��", "OWTR", "WTR1", "", "oStockTransfer", "StockTransfer", "StockTransfer_Lines", "");
    
    public final static DocType OWOR = new DocType(202, "��������", "OWOR", "WOR1", "", "oPruductionOrders", "Documents", "Document_Lines", "");
    
}
