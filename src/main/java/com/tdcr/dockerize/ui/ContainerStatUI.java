package com.tdcr.dockerize.ui;

import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.ui.*;

public class ContainerStatUI extends Window {

    public ContainerStatUI(ContainerVO containerVO) {
        initUI(containerVO);
    }

    private void initUI(ContainerVO containerVO) {
        VerticalLayout rootLayout = new VerticalLayout();
        Label screenName = new Label("Container Stats for "+containerVO.getContainerName());
        center();
        setHeight(90f, Unit.PERCENTAGE);
        setWidth(90f, Unit.PERCENTAGE);
        rootLayout.addComponent(screenName);
        this.setContent(rootLayout);
        Notification.show("In progress");
    }
}
