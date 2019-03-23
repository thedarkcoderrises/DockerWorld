package com.tdcr.dockerize.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.tdcr.dockerize.service.DockerService;
import com.tdcr.dockerize.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DockerServiceImpl implements DockerService {

    private static Logger LOG = LoggerFactory.getLogger(DockerServiceImpl.class);

    @Autowired
    DockerClient dockerClient;

    @Override
    public String listRunningContainers() {
        return listAllContainers("running");
    }

    @Override
    public String listStoppedContainers() {
        return listAllContainers("exited");
    }

    @Override
    public String listAllContainers(String status) {
        ListContainersCmd cmd = null;
        List<Container> lst = new ArrayList<>();
        if(StringUtils.isEmpty(status)){
            cmd = dockerClient.listContainersCmd().withShowSize(true)
                    .withShowAll(true);
        }else{
            cmd = dockerClient.listContainersCmd().withShowSize(true)
                    .withShowAll(true).withStatusFilter(status);
        }
         lst= cmd.exec(); //  docker ps -a -s -f status=${status}
        logContainerDetails(lst);
        return DataUtil.listToJson(Collections.singletonList(lst)).toString();
    }

    private void logContainerDetails(List<Container> lst) {
        int runningContainerCnt = 0;
        int stoppedContainerCnt = 0;
        DecimalFormat dec = new DecimalFormat("0.00");
        for (Container container :
                lst) {
            if(container.getStatus().startsWith("Up")){
                runningContainerCnt+=1;
                LOG.info("containerName: {}, containerId: {}, memorySize: {}MB",
                        container.getNames().toString(),container.getId(),dec.format((container.getSizeRootFs()/1024.0)/1024.0));
            }else{
                stoppedContainerCnt+=1;
            }
        }
        LOG.info("Total Container:{}",lst.size());
        LOG.info("Total RunningContainer:{}",runningContainerCnt);
        LOG.info("Total StoppedContainerCnt:{}",stoppedContainerCnt);

    }

    @Override
    public String inspectOnContainerId(String containerId) {
         InspectContainerResponse container = dockerClient.inspectContainerCmd(containerId).exec();
         return container.toString();
    }
}
