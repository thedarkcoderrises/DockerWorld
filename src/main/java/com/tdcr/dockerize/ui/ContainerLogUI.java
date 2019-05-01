package com.tdcr.dockerize.ui;

import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.ui.*;

public class ContainerLogUI extends Window {

    public ContainerLogUI(ContainerVO containerVO) {
        initUI(containerVO);
    }

    TextArea logDiv =new TextArea(); ;

    private void initUI(ContainerVO containerVO) {
        VerticalLayout rootLayout = new VerticalLayout();
        Panel logPanel = new Panel();
        Label screenName = new Label("Container log for "+containerVO.getContainerName());
        center();
        setHeight(90f, Unit.PERCENTAGE);
        setWidth(90f, Unit.PERCENTAGE);
        logDiv.setSizeFull();
        rootLayout.addComponents(logDiv);
        logPanel.setSizeFull();
        rootLayout.setSizeFull();
        logPanel.setContent(rootLayout);
        this.setContent(logPanel);
    }

    void updateLogDiv(String logs){
        logDiv.setValue(logs);
    }
}
