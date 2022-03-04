package com.increff.pos.util;

import com.increff.pos.model.OrderItemData1;
import org.apache.fop.apps.*;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfHelper {
    public static final String RESOURCES_DIR;
    public static final String OUTPUT_DIR;

    static {
        RESOURCES_DIR = "src//main//resources//";
        OUTPUT_DIR = "src//main//resources//output//";
    }

    public String getxmlStream(Timestamp order, Timestamp invoice, Integer id, List<OrderItemData1> items, Float total) {

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        StringBuilder ret = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <?xml-stylesheet type=\"application/xml\"?> <users-data> <header-section> <data-type >Invoice</data-type> <odate>");
        ret.append(dateFormat.format(new Date(order.getTime())));
        ret.append("</odate><idate>");
        ret.append(dateFormat.format(new Date(invoice.getTime())));
        ret.append("</idate><id>");
        ret.append(id);
        ret.append("</id><total>");
        ret.append(total);
        ret.append("</total></header-section>");
        Integer i = 0;
        for (OrderItemData1 oip : items) {
            ret.append("<table-data><sno>");
            ret.append(++i);
            ret.append("</sno><brandName>");
            ret.append(oip.getBrandName());
            ret.append("</brandName><categoryName>");
            ret.append(oip.getCategoryName());
            ret.append("</categoryName><name>");
            ret.append(oip.getName());
            ret.append("</name><barcode>");
            ret.append(oip.getBarcode());
            ret.append("</barcode><quantity>");
            ret.append(oip.getQuantity());
            ret.append("</quantity><price>");
            ret.append(oip.getSellingPrice());
            ret.append("</price><total>");
            ret.append(oip.getQuantity() * oip.getSellingPrice());
            ret.append("</total></table-data>");
        }
        ret.append("</users-data>");
        return ret.toString();
    }

    public void convertToPDF(String xmlstr, Integer id) throws IOException, FOPException, TransformerException {
        FileWriter myWriter = new FileWriter(RESOURCES_DIR + "//data.xml");
        myWriter.write(xmlstr);
        myWriter.close();
        // the XSL FO file
        File xsltFile = new File(RESOURCES_DIR + "//template.xsl");
        // the XML file which provides the input
        StreamSource xmlSource = new StreamSource(new File(RESOURCES_DIR + "//data.xml"));
        // create an instance of fop factory
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        // a user agent is needed for transformation
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        // Setup output
        OutputStream out;
        String name = "//invoice.pdf";
        out = new java.io.FileOutputStream(OUTPUT_DIR + name);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }
    }
}
