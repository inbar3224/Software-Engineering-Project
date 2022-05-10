package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;

public class FlowerBoundaryController {
    @FXML private AnchorPane Item;
    @FXML private Button editbtn;
    @FXML private ImageView flowerImg;

    private String imageUrl;

    @FXML private Button returnbtn;
    @FXML private Button addtocartbtn;

    @FXML private TextField txt;
    @FXML private Label descriptiontxt;
    @FXML private TextField pricetxt;
    Flower flower = new Flower();

    @FXML
    void initialize() {
        flower = CatalogBoundaryController.getCurrentFlower();
        txt.setText(flower.getName());
        descriptiontxt.setText(flower.getType());
        pricetxt.setText((flower.getPrice()) + "");
        txt.setEditable(false);
        descriptiontxt.setWrapText(true);

        pricetxt.setEditable(false);
        flowerImg.setImage(new Image(getClass().getResourceAsStream(flower.getImageurl())));
    }

    @FXML
    void edit(ActionEvent event) throws IOException {
        if(editbtn.getText().equals("Update")) {
            pricetxt.setEditable(true);
            editbtn.setText("done");
            pricetxt.setStyle("-fx-background-color: white;");
        }
        else {
            pricetxt.setStyle("-fx-background-color: transparent;-fx-font-size:  14px;-fx-font-weight: bold;-fx-font-style: italic");
            pricetxt.setEditable(false);
            editbtn.setText("Update");
            flower.setPrice(Double.parseDouble(pricetxt.getText().toString()));
            ArrayList<Object> arr = new ArrayList<>();
            arr.add("#updateflower");
            arr.add(flower.getId());
            arr.add(flower.getPrice());
            arr.add(flower.getDiscount());

            App.getClient().sendToServer(arr);
        }
    }

    @FXML
    void returnBtn(ActionEvent event) throws IOException {
        try {
            App.setRoot("catalogboundary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addBtn(ActionEvent event) throws IOException {
        try {
            CatalogBoundaryController.addToCart(flower);
            App.setRoot("catalogboundary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}