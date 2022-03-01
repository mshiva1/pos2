package com.increff.pos.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AboutAppServiceTest extends AbstractUnitTest {

    @Autowired
    private AboutAppService service;

    @Test
    public void testServiceApis() {
        assertEquals("POS Application", service.getName());
        assertEquals("v1.0", service.getVersion());
    }

}
