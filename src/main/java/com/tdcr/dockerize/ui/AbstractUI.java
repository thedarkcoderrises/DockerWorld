package com.tdcr.dockerize.ui;

import com.vaadin.ui.UI;


public abstract class AbstractUI extends UI {

    AbstractUI (){
        super();
    }

   void  setTitleName(){
        UI.getCurrent().getPage().setTitle("Docker");
    }

}
