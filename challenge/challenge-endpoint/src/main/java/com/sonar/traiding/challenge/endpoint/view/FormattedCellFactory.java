/*
 * Classname: TradeCellFactory
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 24/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.endpoint.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * Cell Factory to render items in ComboBox
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 24/10/2017
 */
public class FormattedCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {
	private TextAlignment textAlignment = TextAlignment.LEFT;
	private Pos alignment = Pos.CENTER_LEFT;
	
	@Override
	public ListCell<T> call(ListView<T> param) {
		ListCell<T> listCell = new ListCell<T>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				if(item == null || empty) {
					setText("");
					setGraphic(null);
				} else {
					super.updateItem(item, empty);
					if (item instanceof Node) {
						setText(null);
						setGraphic((Node) item);
					} else {
						setText(item.toString());
						setGraphic(null);
					}
				}
			}
		};
		listCell.setTextAlignment(textAlignment);
		listCell.setAlignment(alignment);
		Insets padding = new Insets(5, 5, 5, 5);
		listCell.setPadding(padding);
		return listCell;
	}

	/**
	 * Return the alignment
	 * @return the alignment
	 */
	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	/**
	 * Set the alignment
	 * @param alignment the alignment to set
	 */
	public void setTextAlignment(TextAlignment alignment) {
		this.textAlignment = alignment;
	}

	/**
	 * Return the alignment
	 * @return the alignment
	 */
	public Pos getAlignment() {
		return alignment;
	}

	/**
	 * Set the alignment
	 * @param alignment the alignment to set
	 */
	public void setAlignment(Pos alignment) {
		this.alignment = alignment;
	}
}
