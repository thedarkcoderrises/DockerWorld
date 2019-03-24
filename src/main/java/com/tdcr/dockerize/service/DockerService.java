package com.tdcr.dockerize.service;

import com.tdcr.dockerize.vo.ContainerVO;

import java.util.List;

public interface DockerService {

    String listRunningContainers();

    String listStoppedContainers();

    List<ContainerVO> listAllContainers(String status);

    String inspectOnContainerId(String containerId);
}
