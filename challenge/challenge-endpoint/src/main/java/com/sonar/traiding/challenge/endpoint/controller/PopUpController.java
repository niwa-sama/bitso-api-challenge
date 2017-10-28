/*
 * Classname: PopUpController
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 27/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.endpoint.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controller for popup window
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 27/10/2017
 */
public class PopUpController {
	private Stage parent;
	private Stage popup;
	
	public void setParent(Stage parent) {
		this.parent = parent;
	}

	public void setPopUp(Stage popup) {
		this.popup = popup;
	}
	
	@FXML void onActionClosePopUp(ActionEvent event) {
		popup.hide();
		parent.getScene().getRoot().setEffect(null);
	}
}
