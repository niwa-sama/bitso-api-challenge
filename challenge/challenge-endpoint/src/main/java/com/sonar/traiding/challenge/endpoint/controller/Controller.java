/*
 * Classname: Controller
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 19/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.endpoint.controller;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sonar.traiding.challenge.core.bitso.business.Order;
import com.sonar.traiding.challenge.core.bitso.business.OrderBook;
import com.sonar.traiding.challenge.core.bitso.business.Trade;
import com.sonar.traiding.challenge.core.bitso.rest.BitsoRestConnector;
import com.sonar.traiding.challenge.core.bitso.rest.Operation;
import com.sonar.traiding.challenge.core.bitso.rest.Operation.Builder;
import com.sonar.traiding.challenge.core.bitso.rest.Operation.Param;
import com.sonar.traiding.challenge.core.bitso.websocket.BitsoWebSocketClient;
import com.sonar.traiding.challenge.endpoint.view.FormattedCellFactory;
import com.sonar.traiding.challenge.endpoint.view.TradeCellFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main Controller for application
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 19/10/2017
 */
public class Controller implements Initializable {
	private static final int DEFAULT_MAX_UPTICKS = 3;
	private static final int DEFAULT_MAX_DOWNTICKS = 3;
	private static final double DEFAULT_BITCOINT_SELL = 1.0;
	private static final double DEFAULT_BITCOINT_BUY = 1.0;
	
	@FXML private ComboBox<String> cbRecentOperSize;
	@FXML private ComboBox<String> cbBestBidsSize;
	@FXML private TableView<Trade> tblRecentOperation;
	@FXML private TableView<Order> tblBestBids;
	@FXML private TableView<Order> tblBestAsks;
	@FXML private ImageView imgZeroTicksHelp;
	@FXML private TextField tfUpticks;
	@FXML private TextField tfDownticks;
	@FXML private TextField tfBitcoinsSell;
	@FXML private TextField tfBitcoinsBuy;
	@FXML private CheckBox ckbIgnoreZeroTicks;
	
	private Stage parent;
	private Stage popup;
	
	private double centerXPosition;
	private double centerYPosition;
	
	private JSONParser tradesJP = new JSONParser();
	private JSONParser bidsJP = new JSONParser();
	private Builder tradesBuilder;
	private int maxUpTicks = DEFAULT_MAX_UPTICKS;
	private int maxDownTicks = DEFAULT_MAX_DOWNTICKS;
	private int upTicks;
	private int downTicks;
	private int zeroTicks;
	private double bitcoinsSell = DEFAULT_BITCOINT_SELL;
	private double bitcoinsBuy = DEFAULT_BITCOINT_BUY;
	private boolean ignoreZeroTicks = true;
	private double lastRate;

	private OrderBook orderBook = new OrderBook();
	private List<Trade> sellAndBuy = new ArrayList<>();
	
	private ObservableList<Trade> tradeList;
	private ObservableList<Order> bidsList;
	private ObservableList<Order> asksList;
	
	public Controller() {
	}
	
	public void setParent(Stage parent) {
		this.parent = parent;
	}

	public void setPopUp(Stage popup) {
		this.popup = popup;
		this.popup.setOnShown(e -> {
			this.popup.setX(centerXPosition - popup.getWidth() / 2);
			this.popup.setY(centerYPosition - popup.getHeight() / 2);
		});
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tradeList = FXCollections.observableArrayList(new Trade[0]);
		tblRecentOperation.setItems(tradeList);
		bidsList = FXCollections.observableArrayList(new Order[0]);
		tblBestBids.setItems(bidsList);
		asksList = FXCollections.observableArrayList(new Order[0]);
		tblBestAsks.setItems(asksList);
		tblRecentOperation.getColumns().stream().forEach(column -> column.setCellFactory(new TradeCellFactory()));
		FormattedCellFactory<String> cf = new FormattedCellFactory<>();
		cf.setAlignment(Pos.CENTER_RIGHT);
		cbBestBidsSize.setCellFactory(cf);
		cbRecentOperSize.setCellFactory(cf);
		InputStream imgStream = getClass().getResourceAsStream("/com/sonar/traiding/challenge/endpoint/view/question-icon.png");
		Image image = new Image(imgStream);
		imgZeroTicksHelp.setPreserveRatio(false);
		imgZeroTicksHelp.setImage(image);
		Tooltip zeroTicksHelp = new Tooltip("If selected, zero-ticks counts like upticks or downticks, "
				+ "if only zero-ticks are received and limit trades is reached, "
				+ "the next non-zero-tick trade define if virtual trade will be for selling or buying."
				+ "\nIf not selected, when zero-tick is received, upticks and downticks are reseted.");
		zeroTicksHelp.setMaxWidth(400);
		zeroTicksHelp.setWrapText(true);
		zeroTicksHelp.setAutoHide(false);
		Tooltip.install(imgZeroTicksHelp, zeroTicksHelp);
		BitsoWebSocketClient.getInstance().registerConnectListener(this::webSocketConnection);
		BitsoWebSocketClient.getInstance().startWebSocket();
	}
	
	public void webSocketConnection() {
		BitsoWebSocketClient.getInstance().subscribeToTrades();
		BitsoWebSocketClient.getInstance().subscribeToDiffOrders();
		tradesBuilder = Operation.builder()
				.trades()
				.setParameter(Param.BOOK, "btc_mxn")
				.setParameter(Param.SORT, "desc")
				.setParameter(Param.LIMIT, String.valueOf(10));
		Trade[] trades = readTrades();
		this.tradeList.addAll(trades);
		synchronized (bidsList) {
			this.orderBook = readOrderBook();
			updateBookOrder(10);
		}
		cbRecentOperSize.getEditor().setOnKeyTyped(this::onKeyTypedOnlyDigits);
		cbBestBidsSize.getEditor().setOnKeyTyped(this::onKeyTypedOnlyDigits);
		BitsoWebSocketClient.getInstance().registerTradesListener(this::newTrade);
		BitsoWebSocketClient.getInstance().registerDiffOrdersListener(this::newDiffOrder);
	}
	
	public void newTrade(String trade) {
		synchronized (tradeList) {
			try {
				JSONObject tradeJ = (JSONObject) tradesJP.parse(trade);
				String book = (String) tradeJ.get("book");
				JSONArray payload = (JSONArray) tradeJ.get("payload");
				for (Object o : payload) {
					JSONObject t = (JSONObject) o;
					final double rate = Double.parseDouble((String) t.get("r"));
					if (Double.compare(rate, lastRate) == 0) {
						if(ignoreZeroTicks) {
							++zeroTicks;
						} else {
							zeroTicks = 0;
							downTicks = 0;
							upTicks = 0;
						}
					} else if (rate > lastRate) {
						downTicks = 0;
						++upTicks;
						lastRate = rate;
					} else if (rate < lastRate) {
						upTicks = 0;
						++downTicks;
						lastRate = rate;
					}
					if (upTicks > 0 && zeroTicks + upTicks >= maxUpTicks) {
						Trade sell = new Trade();
						sell.setBook(book);
						sell.setTradeID(0L);
						sell.setMarkerSide("sell");
						sell.setPrice(rate);
						sell.setAmount(bitcoinsSell);
						sell.setCreatedAt(getUTCLocalTime());
						sell.setVirtual(true);
						sellAndBuy.add(sell);
						upTicks = 0;
						downTicks = 0;
						zeroTicks = 0;
					} else if (downTicks > 0 && zeroTicks + downTicks >= maxDownTicks) {
						Trade buy = new Trade();
						buy.setBook(book);
						buy.setTradeID(0L);
						buy.setMarkerSide("buy");
						buy.setPrice(rate);
						buy.setAmount(bitcoinsBuy);
						buy.setCreatedAt(getUTCLocalTime());
						buy.setVirtual(true);
						sellAndBuy.add(buy);
						downTicks = 0;
						upTicks = 0;
						zeroTicks = 0;
					}
				}
				Trade[] trades = readTrades();
				updateTrades(trades);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateTrades(Trade[] trades) {
		this.tradeList.clear();
		this.tradeList.addAll(trades);
		if(!sellAndBuy.isEmpty()) {
			this.tradeList.addAll(sellAndBuy);
			Collections.sort(this.tradeList);
			int size = Integer.parseInt(cbRecentOperSize.getValue());
			this.tradeList.remove(size, this.tradeList.size());
		}
	}
	
	private void updateBookOrder(int size) {
		this.bidsList.clear();
		this.asksList.clear();
		Iterator<Order> i = this.orderBook.getBids().iterator();
		for (int c = 0; c < size && i.hasNext(); c++) {
			this.bidsList.add(i.next());
		}
		i = this.orderBook.getAsks().iterator();
		for (int c = 0; c < size && i.hasNext(); c++) {
			this.asksList.add(i.next());
		}
	}
	
	private void updateBookOrder(List<Order> newAsks, List<Order> newBids, List<Order> cancelledAsks, List<Order> cancelledBids) {
		this.orderBook.getBids().removeAll(cancelledBids);
		this.orderBook.getAsks().removeAll(cancelledAsks);
		this.orderBook.getBids().removeAll(newBids);
		this.orderBook.getAsks().removeAll(newAsks);
		this.orderBook.getBids().addAll(newBids);
		this.orderBook.getAsks().addAll(newAsks);
		int size = Integer.parseInt(cbBestBidsSize.getValue());
		Collections.sort(this.orderBook.getAsks(), (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
		Collections.sort(this.orderBook.getBids(), (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
		updateBookOrder(size);
	}
	
	private void newDiffOrder(String diffOrder) {
		List<Order> newAsks = new ArrayList<>();
		List<Order> newBids = new ArrayList<>();
		List<Order> cancelledAsks = new ArrayList<>();
		List<Order> cancelledBids = new ArrayList<>();
		synchronized (bidsList) {
			try {
				JSONObject tradeJ = (JSONObject) tradesJP.parse(diffOrder);
				JSONArray payload = (JSONArray) tradeJ.get("payload");
				String book = (String) tradeJ.get("book");
				Long sequence = (Long) tradeJ.get("sequence");
				if (sequence > this.orderBook.getSequence()) {
					if (this.orderBook.getSequence() + 1 == sequence)  {
						for(Object o : payload) {
							Order order = parseDiffOrder(book, o);
							if (order.getStatus().equalsIgnoreCase("open")) {
								if (order.getMarkerSide().equalsIgnoreCase("buy"))
									newBids.add(order);
								else
									newAsks.add(order);
							} else {
								if (order.getMarkerSide().equalsIgnoreCase("buy"))
									cancelledBids.add(order);
								else
									cancelledAsks.add(order);
							}
						}
						this.orderBook.setSequence(sequence);
						updateBookOrder(newAsks, newBids, cancelledAsks, cancelledBids);
					} else {
						this.orderBook = readOrderBook();
						int size = Integer.parseInt(cbBestBidsSize.getValue());
						updateBookOrder(size);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Trade[] readTrades() {
		try {
			String response = BitsoRestConnector.getInstance().invoke(tradesBuilder.build().path());
			JSONObject json = (JSONObject) tradesJP.parse(response);
			List<Trade> tradeList = (List) ((JSONArray) json.get("payload")).stream().map(this::parseTrade).collect(Collectors.toList());
			return tradeList.toArray(new Trade[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Trade[0];
	}
		
	private OrderBook readOrderBook() {
		Operation orderBook = Operation.builder()
				.orderBook()
				.addParameter(Param.BOOK, "btc_mxn")
				.addParameter(Param.AGGREGATE, "false")
				.build();
		try {
			String response = BitsoRestConnector.getInstance().invoke(orderBook.path());
			JSONObject json = (JSONObject) bidsJP.parse(response);
			JSONObject payload = (JSONObject) json.get("payload");
			List<Order> bidList = (List) ((JSONArray) payload.get("bids")).stream().map(this::parseOrder).collect(Collectors.toList());
			List<Order> asksList = (List) ((JSONArray) payload.get("asks")).stream().map(this::parseOrder).collect(Collectors.toList());
			
			OrderBook ob = new OrderBook();
			ob.setBids(bidList);
			ob.setAsks(asksList);
			ob.setUpdatedAt((String) payload.get("updated_at"));
			ob.setSequence(Long.valueOf((String)payload.get("sequence")));
			return ob;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new OrderBook();
	}
	
	private Order parseDiffOrder(String book, Object o) {
		JSONObject json = (JSONObject) o;
		Order t = new Order();
		t.setBook((String) json.get(book));
		t.setTimestamp((Long)json.get("d"));
		if (json.get("a") != null)
			t.setAmount(Double.valueOf((String)json.get("a")));
		t.setMarkerSide((Long)json.get("t") == 1 ? "sell" : "buy");
		t.setPrice(Double.valueOf((String)json.get("r")));
		t.setOrderID((String)json.get("o"));
		t.setStatus((String)json.get("s"));
		return t;
	}
	
	private Trade parseTrade(Object o) {
		JSONObject json = (JSONObject) o;
		Trade t = new Trade();
		t.setBook((String) json.get("book"));
		t.setCreatedAt((String)json.get("created_at"));
		t.setAmount(Double.valueOf((String)json.get("amount")));
		t.setMarkerSide((String)json.get("maker_side"));
		t.setPrice(Double.valueOf((String)json.get("price")));
		t.setTradeID((Long)json.get("tid"));
		return t;
	}

	private Order parseOrder(Object o) {
		JSONObject json = (JSONObject) o;
		Order bid = new Order();
		bid.setBook((String) json.get("book"));
		bid.setPrice(Double.valueOf((String)json.get("price")));
		bid.setAmount(Double.valueOf((String)json.get("amount")));
		bid.setOrderID((String)json.get("oid"));
		return bid;
	}
	
	private LocalDateTime getUTCLocalTime() {
		return LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
	}
	
	@FXML
	private void onKeyTypedOnlyDigits(KeyEvent event) {
		final String selected = event.getCharacter();
		if(!selected.matches("\\d"))
			event.consume();
	}

	@FXML
	private void onKeyTypedOnlyDigitsAndDot(KeyEvent event) {
		final String selected = event.getCharacter();
		if (!selected.matches("[\\d\\.]"))
			event.consume();
		if (selected.matches("\\."))
			if (event.getSource() instanceof TextInputControl) {
				if (((TextInputControl) event.getSource()).getText().contains("."))
					event.consume();
			}
	}

	@FXML
	private void onActionCbRecentOperSize(ActionEvent event) {
		synchronized (tradeList) {
			String selected = cbRecentOperSize.getValue();
			final int size = Integer.parseInt(selected);
			if (size > 100) {
				selected = "100";
				cbRecentOperSize.setValue(selected);
			}
			this.tradesBuilder.setParameter(Param.LIMIT, selected);
			Trade[] trades = readTrades();
			updateTrades(trades);
		}
	}
	
	@FXML
	private void onActionCbBestBidsSize(ActionEvent event) {
		synchronized (bidsList) {
			String selected = cbBestBidsSize.getValue();
			int size = Integer.parseInt(selected);
			updateBookOrder(size);
		}
	}
	
	@FXML
	private void onActionApply(ActionEvent event) {
		synchronized (tradeList) {
			if (tfUpticks.getText().isEmpty()) {
				tfUpticks.setText(String.valueOf(DEFAULT_MAX_UPTICKS));
				this.maxUpTicks = DEFAULT_MAX_UPTICKS;
			} else
				this.maxUpTicks = Integer.parseInt(tfUpticks.getText());
			
			if (tfDownticks.getText().isEmpty()) {
				tfDownticks.setText(String.valueOf(DEFAULT_MAX_DOWNTICKS));
				maxDownTicks = DEFAULT_MAX_DOWNTICKS;
			} else
				this.maxDownTicks = Integer.parseInt(tfDownticks.getText());
			
			if (tfBitcoinsSell.getText().isEmpty()) {
				tfBitcoinsSell.setText(String.valueOf(DEFAULT_BITCOINT_SELL));
				bitcoinsSell = DEFAULT_BITCOINT_SELL;
			} else
				bitcoinsSell = Double.parseDouble(tfBitcoinsSell.getText());
			
			if (tfBitcoinsBuy.getText().isEmpty()) {
				tfBitcoinsBuy.setText(String.valueOf(DEFAULT_BITCOINT_BUY));
				bitcoinsBuy = DEFAULT_BITCOINT_BUY;
			} else
				bitcoinsBuy = Double.parseDouble(tfBitcoinsBuy.getText());
			ignoreZeroTicks = ckbIgnoreZeroTicks.isSelected();
		}
		centerYPosition = parent.getY() + parent.getHeight() / 2;
		centerXPosition = parent.getX() + parent.getWidth() / 2;
		popup.show();
		parent.getScene().getRoot().setEffect(new BoxBlur());
	}
	
	public void onHideRoot(WindowEvent e) {
		Runtime.getRuntime().exit(0);
	}
}
