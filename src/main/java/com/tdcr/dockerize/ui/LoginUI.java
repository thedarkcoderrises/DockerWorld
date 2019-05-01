package com.tdcr.dockerize.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
@Theme("docker")
@SpringUI
public class LoginUI extends AbstractUI {


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setTitleName();
        LoginForm component = new LoginForm();
        component.addLoginListener(e -> {
            if ("admin".equals(e.getLoginParameter("username")) && "admin".equals(e.getLoginParameter("password"))) {
                Notification.show("Login success");
                getUI().getPage().setLocation("/docker");
            } else {
                Notification.show("Login failed!", Notification.Type.WARNING_MESSAGE);
            }
        });
        setContent(component);
        setStyleName("backgroundimage");
    }
}
