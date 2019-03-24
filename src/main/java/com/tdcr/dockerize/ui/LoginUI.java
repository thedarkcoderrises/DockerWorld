package com.tdcr.dockerize.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@SpringUI
public class LoginUI extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        LoginForm component = new LoginForm();
        component.addLoginListener(e -> {
            if (e.getLoginParameter("username").equals("admin")) {
                Notification.show("Login success");
            } else {
                Notification.show("Login failed!");
            }
        });
        setContent(component);
    }
}
