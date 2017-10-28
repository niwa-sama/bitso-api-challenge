/*
 * Classname: Application
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 09/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.endpoint;

import java.io.IOException;

import com.sonar.traiding.challenge.core.activemq.Broker;
import com.sonar.traiding.challenge.core.bitso.rest.BitsoRestConnector;
import com.sonar.traiding.challenge.core.bitso.websocket.BitsoWebSocketClient;
import com.sonar.traiding.challenge.endpoint.controller.Controller;
import com.sonar.traiding.challenge.endpoint.controller.PopUpController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main class to be invoked for JavaFX to start application
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 09/10/2017
 */
public class Application extends javafx.application.Application {
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Starting ActiveMQ embedded...");
		Broker.start();
		System.out.println("Done");
		System.out.println("Configuring and starting Bitso REST client...");
		BitsoRestConnector.getInstance().configure(BitsoRestConnector.BITSO_REST_URL);
		System.out.println("Done");
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sonar traiding");
		this.primaryStage.setResizable(false);
		initRootLayout();
		Runtime.getRuntime().addShutdownHook(new Shutdown());
	}

	private void initRootLayout() {
		try {
			Controller controller = new Controller();
			FXMLLoader loader = new FXMLLoader(Application.class.getResource("view/MainView.fxml"));
			loader.setController(controller);
			AnchorPane rootLayout = loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.setOnHiding(controller::onHideRoot);
			primaryStage.show();
			
			FXMLLoader dialogLoader = new FXMLLoader(Application.class.getResource("view/PopUp.fxml"));
			PopUpController popUpController = new PopUpController();
			dialogLoader.setController(popUpController);
			TitledPane dialogPane = dialogLoader.load();
			Scene dialogScene = new Scene(dialogPane);
			Stage dialog = new Stage(StageStyle.TRANSPARENT);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			dialog.setScene(dialogScene);
			controller.setPopUp(dialog);
			controller.setParent(primaryStage);
			popUpController.setPopUp(dialog);
			popUpController.setParent(primaryStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class Shutdown extends Thread {
		@Override
		public void run() {
			System.out.println("Stopping WebSocket to Bitso...");
			BitsoWebSocketClient.getInstance().stopWebSocket();
			System.out.println("Done");
			System.out.println("Stopping ActiveMQ embedded...");
			Broker.stop();
			System.out.println("Done");
			System.out.println("Stopping REST client...");
			BitsoRestConnector.getInstance().stop();
			System.out.println("Done");
		}
	}
}
