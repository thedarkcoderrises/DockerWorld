package com.tdcr.dockerize.ui;


import com.tdcr.dockerize.service.DockerService;
import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringUI(path = "/docker")
public class DockerUI extends UI {

    @Autowired
    DockerService dockerService;

    Panel footerPanel = new Panel();
    Panel gridPanel = new Panel();
    VerticalLayout rootLayout = new VerticalLayout();
    HorizontalLayout footerLayout = new HorizontalLayout();
    Button refreshBtn = new Button();
    Button statsBtn = new Button();
    Button updateContainerStatusBtn = new Button();
    Grid<ContainerVO> grid;
    Label screenName = new Label("Docker Container Details");

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        refreshBtn.setIcon(VaadinIcons.REFRESH);
        statsBtn.setIcon(VaadinIcons.PIE_CHART);
        updateContainerStatusBtn.setIcon(VaadinIcons.POWER_OFF);
        grid = new Grid<>(ContainerVO.class);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
        grid.setHeight("500px");
        grid.setColumns("containerId", "containerName", "memorySizeInMB", "status", "port", "runningSince");
        grid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        grid.setColumnReorderingAllowed(true);
        grid.setDetailsGenerator(e -> showSelectedContainerStats(e.getContainerId(),e.getStatus()));
        gridPanel.setContent(grid);
        footerLayout.addComponents(statsBtn,refreshBtn,updateContainerStatusBtn);
        rootLayout.addComponents(screenName,gridPanel,footerPanel);
        footerLayout.setComponentAlignment(refreshBtn , Alignment.MIDDLE_RIGHT);
        footerLayout.setComponentAlignment(statsBtn , Alignment.MIDDLE_RIGHT);
        footerLayout.setComponentAlignment(updateContainerStatusBtn,Alignment.MIDDLE_RIGHT);
        footerLayout.setSpacing(true);
        footerPanel.setContent(footerLayout);
        grid.addItemClickListener(e -> updateDetailVisibility(e));
        refreshBtn.addClickListener(e -> refreshData());
        updateContainerStatusBtn.addClickListener(e -> updateContainerStatus());
        statsBtn.addClickListener(e -> statSubWindow());
        setContent(rootLayout);
    }

    private void statSubWindow() {
        ContainerVO selectedRow = getSelectedRow();
        if(selectedRow == null) return;

        ContainerStatUI popup = new ContainerStatUI(selectedRow);
        UI.getCurrent().removeWindow(popup);
        UI.getCurrent().addWindow(popup);
    }

    private void updateContainerStatus() {
        ContainerVO containerVO = getSelectedRow();
        if(containerVO == null) return;

        if(containerVO.getContainerName().contains("socat")){
            Notification.show("Inavlid action!", Notification.Type.WARNING_MESSAGE);
            return;
        }
        boolean status = !containerVO.getStatus().equalsIgnoreCase("running");
        String statsMsg = status == true? "Starting" :"Stopping";

        ConfirmDialog.show(UI.getCurrent(),"",
                statsMsg+" "+containerVO.getContainerName()+"...", "YES", "NO", new ConfirmDialog.Listener(){

            @Override
            public void onClose(ConfirmDialog confirmDialog) {
                if(confirmDialog.isConfirmed()){
                    String result = dockerService.updateContainerStatus(containerVO.getContainerId(),status);
                    if(containerVO.getContainerId().equals(result)){
                        Notification.show("Container status updated successfully!!");
                        refreshBtn.click();
                    }else{
                        Notification.show("Failed to update!", Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });

    }

    private ContainerVO getSelectedRow() {
        Set<ContainerVO> selectedItems = grid.getSelectedItems();
        if(selectedItems.isEmpty()){
            Notification.show("Please select a container!");
            return null;
        }
        return (ContainerVO) selectedItems.toArray()[0];
    }

    private void updateDetailVisibility(Grid.ItemClick<ContainerVO> e) {
        ContainerVO vo = e.getItem();
        grid.setDetailsVisible(vo ,!grid.isDetailsVisible(vo));
    }

    private Component showSelectedContainerStats(String containerId,String status) {
        HorizontalLayout layout = new HorizontalLayout();
        Panel detailPanel = new Panel();
        TextField rowDetails = new TextField();
        if(status.equalsIgnoreCase("stopped")){
            rowDetails.setValue("Container is not up!");
        }else {
            rowDetails.setValue(statDetails(containerId));
        }
        rowDetails.setCaptionAsHtml(true);
        layout.addComponent(rowDetails);
        layout.setComponentAlignment(rowDetails, Alignment.MIDDLE_CENTER);
        detailPanel.setContent(layout);
        detailPanel.setSizeFull();
        return detailPanel;
    }

    private String statDetails(String containerId) {
        StringBuilder finalStr = new StringBuilder("<b>Stats</b> :");
        Map<String,String> map =getStats(containerId);
        for(String key : map.keySet()){
            if(!StringUtils.isEmpty(map.get(key))){
                finalStr.append(key);
                finalStr.append(":");
                finalStr.append(map.get(key));
                finalStr.append(";");
            }
        }
        return finalStr.toString();
    }


    private Map<String,String> getStats(String containerId) {
        Map<String,String> stats =new HashMap<>();
        if(StringUtils.isEmpty(containerId)) return stats;
        try {
          stats = dockerService.getContainerStats(containerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }


    private void refreshData() {
        List<ContainerVO> lst = dockerService.listAllContainers(null);
        grid.setItems(lst);
    }
}
