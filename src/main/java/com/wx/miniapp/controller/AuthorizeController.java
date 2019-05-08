package com.wx.miniapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/com/wx/authorize")
public class AuthorizeController {

    @GetMapping(value = "/authorize")
    public String querySessionKey(@RequestParam String code){
        return "Ok";
    }

}
