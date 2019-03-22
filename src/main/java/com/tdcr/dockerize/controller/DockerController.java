package com.tdcr.dockerize.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {

    @Value("${application.message}")
    String message;

    @Value("${application.appname}")
    String appname;

    @RequestMapping("/")
    String home() {
        return message + " " + appname;
    }

}
