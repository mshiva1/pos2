package com.increff.pos.util;

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

public class PdfGeneration {
    public static final String RESOURCES_DIR;
    public static final String OUTPUT_DIR;

    static {
        RESOURCES_DIR = "src//main//resources//";
        OUTPUT_DIR = "src//main//resources//output//";
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
