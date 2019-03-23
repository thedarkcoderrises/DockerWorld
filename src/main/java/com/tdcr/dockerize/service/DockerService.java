package com.tdcr.dockerize.service;

public interface DockerService {

    String listRunningContainers();

    String listStoppedContainers();

    String listAllContainers(String status);

    String inspectOnContainerId(String containerId);
}
