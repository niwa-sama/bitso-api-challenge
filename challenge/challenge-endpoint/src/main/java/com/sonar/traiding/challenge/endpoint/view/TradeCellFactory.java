/*
 * Classname: TradeCellFactory
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 24/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.endpoint.view;

import com.sonar.traiding.challenge.core.bitso.business.Trade;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Cell Factory to render cells in the Trades TableView
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 24/10/2017
 */
public class TradeCellFactory<S,T> implements Callback<TableColumn<Trade, Object>, TableCell<Trade, Object>> {
	@Override
	public TableCell<Trade, Object> call(TableColumn<Trade, Object> param) {
		return new TableCell<Trade, Object>() {
			@Override
			protected void updateItem(Object item, boolean empty) {
				if(item == null || empty) {
					setText("");
				} else {
					super.updateItem(item, empty);
					setText(item.toString());
					if (getTableView().getItems().get(getTableRow().getIndex()).getVirtual())
						setTextFill(Color.BLUE);
					else
						setTextFill(Color.BLACK);
				}
			}
		};
	}
}
