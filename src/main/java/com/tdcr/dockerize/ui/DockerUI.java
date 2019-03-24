package com.tdcr.dockerize.ui;


import com.tdcr.dockerize.service.DockerService;
import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringUI(path = "/docker")
public class DockerUI extends UI {

    @Autowired
    DockerService dockerService;

    VerticalLayout rootLayout = new VerticalLayout();
    HorizontalLayout actions = new HorizontalLayout();
    Label text =  new Label("This is Vaadin app");
    Button refreshBtn = new Button();
    Grid<ContainerVO> grid;
    Label screenName = new Label("Docker Container Details");


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout gridLayout = new VerticalLayout();
        refreshBtn.setIcon(VaadinIcons.REFRESH);
        this.grid = new Grid<>(ContainerVO.class);
        grid.setHeight("200px");
        grid.setWidth("1000px");
        grid.setColumns("containerId", "containerName", "memorySizeInMB", "status");
        refreshBtn.addClickListener(e -> refreshData());
        actions.addComponents(grid);
        gridLayout.addComponents(actions,refreshBtn);
        gridLayout.setComponentAlignment(actions, Alignment.MIDDLE_CENTER);
        gridLayout.setComponentAlignment(refreshBtn, Alignment.MIDDLE_LEFT);
        rootLayout.addComponents(screenName,gridLayout);
        setContent(rootLayout);
    }

    private void refreshData() {
        List<ContainerVO> lst = dockerService.listAllContainers(null);
        grid.setItems(lst);
    }
}
