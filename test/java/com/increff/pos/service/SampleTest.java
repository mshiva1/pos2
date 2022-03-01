package com.increff.pos.service;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class SampleTest {

    @Test
    public void testFiles() {
        InputStream is = null;
        is = SampleTest.class.getResourceAsStream("/com/increff/pos/BrandSample.tsv");
        assertNotNull(is);
    }

}
