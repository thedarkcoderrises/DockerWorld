package com.tdcr.dockerize.vo;

import com.github.dockerjava.api.model.Container;

public class ContainerVO {

    String containerId;
    String containerName;
    String status;
    long memorySizeInMB;

    public ContainerVO(Container container) {
        super();
        this.setContainerId(container.getId());
        this.setContainerName(container.getNames()[0]);
        this.setMemorySizeInMB((container.getSizeRootFs()/1024)/1024);
        this.setStatus(container.getStatus().startsWith("Up")? "Running":"Stopped");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public long getMemorySizeInMB() {
        return memorySizeInMB;
    }

    public void setMemorySizeInMB(long memorySize) {
        this.memorySizeInMB = memorySize;
    }
}
