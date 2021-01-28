package com.cyg.providerdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cyg
 * @date 2021/1/22 10:26
 **/
@RestController
public class ProviderController {


    @RequestMapping("/provider/demo")
    public String providerDemo() {
        return "ProviderDemo";
    }
}
