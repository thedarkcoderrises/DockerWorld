package com.tdcr.dockerize.controller;

import com.tdcr.dockerize.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {

    @Value("${application.message}")
    String message;

    @Value("${application.appname}")
    String appname;

    @Autowired
    DockerService dockerService;

    @RequestMapping("/")
    String home() {
        return message + " " + appname;
    }

    @RequestMapping(value="/ps",produces= MediaType.APPLICATION_JSON_VALUE)
    String dockerps() {
        return dockerService.listRunningContainers();
    }

    @RequestMapping(value="/psa",produces= MediaType.APPLICATION_JSON_VALUE)
    String dockerpsa() {
        return dockerService.listAllContainers("");
    }

    @RequestMapping(value="/inspect/{containerId}", method = RequestMethod.POST)
    String dockerInspect (@PathVariable("containerId") String containerId){
       return dockerService.inspectOnContainerId(containerId);
    }



}
