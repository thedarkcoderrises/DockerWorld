package com.tdcr.dockerize.ui;


import com.tdcr.dockerize.config.DockerConfig;
import com.tdcr.dockerize.service.DockerService;
import com.tdcr.dockerize.ui.mycomponent.FilteredGridLayout;
import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Theme("docker")
@SpringUI(path = "/docker")
@Push
public class DockerUI extends AbstractUI {

    @Autowired
    DockerService dockerService;
    @Autowired
    DockerConfig.DockerProps dockerProps;

    Panel footerPanel = new Panel();
    Panel gridPanel = new Panel();
    VerticalLayout rootLayout = new VerticalLayout();
    HorizontalLayout footerLayout = new HorizontalLayout();
    Button refreshBtn = new Button();
    Button statsBtn = new Button();
    Button updateContainerStatusBtn = new Button();
    FilteredGridLayout gridFilterRow ;
    Grid<ContainerVO> grid;
    Label screenName = new Label();
    ComboBox<String> ddMap = new ComboBox<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setTitleName();
        screenName.setCaption("Docker Container Details");
        screenName.setCaptionAsHtml(true);
        ddMap.setStyleName("comboBox");
        ddMap.setPlaceholder("Daemon...");
        ddMap.setItems(dockerProps.getDockerDaemonMap().keySet());
        setDockerDaemonListener();
        refreshBtn.setIcon(VaadinIcons.REFRESH);
        statsBtn.setIcon(VaadinIcons.PIE_CHART);
        updateContainerStatusBtn.setIcon(VaadinIcons.POWER_OFF);
        grid = new Grid<>(ContainerVO.class);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
        grid.setHeight("500px");
        setGridColumnFilter();
        grid.setColumnReorderingAllowed(true);
        grid.setDetailsGenerator(e -> showSelectedContainerStats(e));
        grid.setStyleGenerator(containerVO ->
                "running".equalsIgnoreCase(containerVO.getStatus()) ? "green" : "amber");
        gridPanel.setContent(grid);
        footerLayout.addComponents(statsBtn,refreshBtn,updateContainerStatusBtn);
        footerLayout.setComponentAlignment(refreshBtn , Alignment.MIDDLE_RIGHT);
        footerLayout.setComponentAlignment(statsBtn , Alignment.MIDDLE_RIGHT);
        footerLayout.setComponentAlignment(updateContainerStatusBtn,Alignment.MIDDLE_RIGHT);
        footerLayout.setSpacing(true);
        footerPanel.setContent(footerLayout);
        grid.addItemClickListener(e -> updateDetailVisibility(e));
        refreshBtn.addClickListener(e -> refreshData());
        updateContainerStatusBtn.addClickListener(e -> updateContainerStatus());
        statsBtn.addClickListener(e -> statSubWindow());
        footerPanel.addStyleName("transparentPanel");
        HorizontalLayout headerHl = new HorizontalLayout();
        headerHl.addComponents(screenName,ddMap);
        headerHl.setComponentAlignment(screenName,Alignment.MIDDLE_CENTER);
        rootLayout.addComponents(headerHl,gridPanel,footerPanel);
        setContent(rootLayout);
        setStyleName("backgroundimage");
    }

    private void setDockerDaemonListener() {
        ddMap.addValueChangeListener(event -> {
            if (event.getSource().isEmpty()) {
                Notification.show("Inavlid action!", Notification.Type.WARNING_MESSAGE);
            } else {
                dockerService.updateDockerClient(event.getValue());
                refreshBtn.click();
            }
        });
    }


    private void setGridColumnFilter() {
        gridFilterRow = new FilteredGridLayout(grid);
        HeaderRow filterHeaderRow = grid.appendHeaderRow();
        filterHeaderRow.getCell("containerName").setComponent(gridFilterRow.getContainerNameFilter());
        filterHeaderRow.getCell("imageName").setComponent(gridFilterRow.getImageNameFilter());
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

        if(containerVO.getContainerName().contains("socat") || containerVO.getContainerName().contains("dw") ){
            Notification.show("Inavlid action!", Notification.Type.WARNING_MESSAGE);
            return;
        }
        boolean status = !containerVO.getStatus().equalsIgnoreCase("running");
        String statsMsg = status == true? "Starting" :"Stopping";

        ConfirmDialog.show(UI.getCurrent(),"",
                statsMsg+" "+containerVO.getContainerName()+"...",
                "YES", "NO", new ConfirmDialog.Listener(){
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

    private Component showSelectedContainerStats(ContainerVO item) {
        VerticalLayout layout = new VerticalLayout();
        Panel detailPanel = new Panel();
        detailPanel.setContent(layout);
        Label emptyStat = new Label();
        if(item.getStatus().equalsIgnoreCase("stopped")){
            emptyStat.setValue("Container is not up!");
            emptyStat.setCaptionAsHtml(true);
            layout.addComponent(emptyStat);
            layout.setComponentAlignment(emptyStat, Alignment.MIDDLE_CENTER);

        }else {
            Thread statFeeder = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i =0;
                    while (i<10) {
                        if(grid.isDetailsVisible(item)){
                            access(new Runnable() {
                                @Override
                                public void run() {
                                    setStatDetails(item.getContainerId(), layout);
                                }
                            });
                            try {
                                Thread.sleep( 2000 );
                                layout.removeAllComponents();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            i+=1;
                        }else{
                            i=10;
                        }

                    }
                }
            });
            statFeeder.start();
        }
        return detailPanel;
    }

    private void setStatDetails(String containerId, VerticalLayout layout) {
        HorizontalLayout hl =  new HorizontalLayout();
        Label keyLabel;
        TextField valueTextField;
        Map<String,String> map =getStats(containerId);
        for(String key : map.keySet()){
            if(!StringUtils.isEmpty(map.get(key))){
                keyLabel = new Label(key+":");
                valueTextField = new TextField();
                valueTextField.setValue((map.get(key)));
                valueTextField.setEnabled(false);
                valueTextField.setHeight(90, Unit.PERCENTAGE);
                valueTextField.addStyleName("textField");
                hl.addComponents(keyLabel,valueTextField);
                hl.setComponentAlignment(valueTextField, Alignment.BOTTOM_CENTER);
                hl.setComponentAlignment(keyLabel, Alignment.MIDDLE_CENTER);
                hl.setHeight(100, Unit.PERCENTAGE);
                layout.addComponent(hl);
            }
        }
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
        gridFilterRow.getContainerNameFilter().clear();
        gridFilterRow.getImageNameFilter().clear();
    }

}

