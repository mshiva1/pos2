package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

    // WEBSITE PAGES

    @RequestMapping(value = "")
    public ModelAndView index() {
        return mav("orders.html");
    }

    @RequestMapping(value = "/site/brands")
    public ModelAndView brand() {
        return mav("brand.html");
    }

    @RequestMapping(value = "/site/products")
    public ModelAndView product() {
        return mav("product.html");
    }

    @RequestMapping(value = "/site/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/site/neworder")
    public ModelAndView create() {
        return mav("neworder.html");
    }

    @RequestMapping(value = "/site/orders")
    public ModelAndView orders() {
        return mav("orders.html");
    }

    @RequestMapping(value = "/site/salesreport")
    public ModelAndView salesreport() {
        return mav("salesreports.html");
    }

    @RequestMapping(value = "/site/inventoryreport")
    public ModelAndView inventoryreport() {
        return mav("productreport.html");
    }

    @RequestMapping(value = "/site/brandreport")
    public ModelAndView brandreport() {
        return mav("brandreport.html");
    }

}
