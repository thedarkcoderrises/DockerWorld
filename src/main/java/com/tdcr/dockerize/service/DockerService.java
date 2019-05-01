package com.tdcr.dockerize.service;

import com.tdcr.dockerize.vo.ContainerVO;

import java.util.List;
import java.util.Map;

public interface DockerService {

    String listRunningContainers();

    String listStoppedContainers();

    List<ContainerVO> listAllContainers(String status);

    String inspectOnContainerId(String containerId);

    Map<String,String> getContainerStats(String containerId) throws Exception;

    String updateContainerStatus (String containerId, boolean status);

    void updateDockerClient (String dockerDaemonName);
}
