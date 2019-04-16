package com.tdcr.dockerize.ui.mycomponent;

import com.tdcr.dockerize.vo.ContainerVO;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class FilteredGridLayout extends HorizontalLayout {

    private Grid grid;
    private final TextField containerNameFilter;
    private final TextField imageNameFilter;


    public FilteredGridLayout(Grid grid) {
        containerNameFilter = new TextField();
        containerNameFilter.setPlaceholder("Container...");
        containerNameFilter.setHeight(70, Unit.PERCENTAGE);
        containerNameFilter.addValueChangeListener(this::onNameFilterTextChange);

        imageNameFilter = new TextField();
        imageNameFilter.setPlaceholder("Image...");
        imageNameFilter.setHeight(70, Unit.PERCENTAGE);
        imageNameFilter.addValueChangeListener(this::onImageNameFilterTextChange);
        this.grid = grid;
    }

    private void onNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<ContainerVO> dataProvider = (ListDataProvider<ContainerVO>) grid.getDataProvider();
        dataProvider.setFilter(ContainerVO::getContainerName, s -> caseInsensitiveContains(s, event.getValue()));
    }


    private void onImageNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<ContainerVO> dataProvider = (ListDataProvider<ContainerVO>) grid.getDataProvider();
        dataProvider.setFilter(ContainerVO::getImageName, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }

    public TextField getContainerNameFilter() {
        return containerNameFilter;
    }

    public TextField getImageNameFilter() {
        return imageNameFilter;
    }
}