<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
    <xsl:attribute-set name="tableBorder">
        <xsl:attribute name="border">solid 0.1mm black</xsl:attribute>
    </xsl:attribute-set>
    <xsl:output encoding="UTF-8" indent="yes" method="xml" standalone="no" omit-xml-declaration="no"/>
    <xsl:template match="users-data">
        <fo:root language="EN">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-portrait" page-height="297mm" page-width="210mm"
                                       margin-top="20mm" margin-bottom="20mm" margin-left="20mm" margin-right="20mm">
                    <fo:region-body margin-top="25mm" margin-bottom="20mm"/>
                    <fo:region-before region-name="xsl-region-before" extent="25mm" display-align="before"
                                      precedence="true"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4-portrait">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block text-align="center" font-size="200%">
                        <fo:basic-link external-destination="http://increff.com">INVOICE</fo:basic-link>
                    </fo:block>
                    <fo:block>Order ID:<xsl:value-of select="header-section/id"/>
                    </fo:block>
                    <fo:block>Invoice Date:
                        <xsl:value-of select="header-section/idate"/>
                    </fo:block>
                    <fo:block>Order Date:
                        <xsl:value-of select="header-section/odate"/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" reference-orientation="0">
                    <fo:block>
                        &#160;&#160;
                    </fo:block>
                    <fo:block>ITEMS:</fo:block>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black"
                              border-width="0.35mm" border-style="solid" text-align="center" display-align="center"
                              space-after="5mm">
                        <fo:table-column column-width="proportional-column-width(10)"/>
                        <fo:table-column column-width="proportional-column-width(30)"/>
                        <fo:table-column column-width="proportional-column-width(35)"/>
                        <fo:table-column column-width="proportional-column-width(30)"/>
                        <fo:table-column column-width="proportional-column-width(30)"/>
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-column column-width="proportional-column-width(25)"/>
                        <fo:table-column column-width="proportional-column-width(35)"/>
                        <fo:table-body font-size="95%">
                            <fo:table-row height="8mm">
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>S No</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Brand</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Category</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Unit Price (in Rs)</fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                    <fo:block>Amount (in Rs)</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:for-each select="table-data">
                                <fo:table-row>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="sno"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="name"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="barcode"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="brandName"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="categoryName"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="quantity"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="price"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="tableBorder">
                                        <fo:block margin-left="1mm" text-align="left">
                                            <xsl:value-of select="total"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>

                    <fo:block text-align="right" font-size="125%">
                        Grand Total : Rs
                        <xsl:value-of select="header-section/total"/>
                    </fo:block>

                    <fo:block id="end-of-document">
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>