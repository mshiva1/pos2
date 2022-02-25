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

    @RequestMapping(value = "/site/brand")
    public ModelAndView brand() {
        return mav("brand.html");
    }

    @RequestMapping(value = "/site/product")
    public ModelAndView product() {
        return mav("product.html");
    }

    @RequestMapping(value = "/site/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/site/create")
    public ModelAndView create() {
        return mav("create.html");
    }

    @RequestMapping(value = "/site/orders")
    public ModelAndView orders() {
        return mav("orders.html");
    }

    @RequestMapping(value = "/site/edit")
    public ModelAndView edits() {
        return mav("edit.html");
    }

    @RequestMapping(value = "/site/brandreport")
    public ModelAndView brandreport() {
        return mav("brandreport.html");
    }

    @RequestMapping(value = "/site/productreport")
    public ModelAndView inventoryreport() {
        return mav("productreport.html");
    }

    @RequestMapping(value = "/site/salesreport")
    public ModelAndView salesreport() {
        return mav("salesreport.html");
    }

    @RequestMapping(value = "/site/pos")
    public ModelAndView pos() {
        return mav("pos.html");
    }

    @RequestMapping(value = "/site/pricing")
    public ModelAndView pricing() {
        return mav("pricing.html");
    }

    @RequestMapping(value = "/site/features")
    public ModelAndView features() {
        return mav("features.html");
    }

}
