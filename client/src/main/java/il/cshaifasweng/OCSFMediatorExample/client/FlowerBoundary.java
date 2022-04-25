package il.cshaifasweng.OCSFMediatorExample.client;



import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


public class FlowerBoundary {


    @FXML
    private AnchorPane Item;


    @FXML
    private Button editbtn;

    @FXML
    private ImageView flowerImg;

    private String imageUrl;

    @FXML
    private Button returnbtn;

    @FXML
    private TextField txt;
    @FXML
    private TextArea descriptiontxt;
    @FXML
    private TextField pricetxt;
    Flower flower=new Flower();


    // private String[] itemstr={"flower 1","flower 2","flower 3","flower 4","flower 5","flower 6"};


    @FXML
    void initialize(){
        flower=CatalogBoundary.getCurrentflower();
        txt.setText(flower.getName());
        descriptiontxt.setText(flower.getType());
        pricetxt.setText((flower.getPrice())+"");
        txt.setEditable(false);
        descriptiontxt.setWrapText(true);

        descriptiontxt.setEditable(false);
        pricetxt.setEditable(false);
        flowerImg.setImage(new Image(getClass().getResourceAsStream(flower.getImageurl())));
        // txt.setText(itemstr[0]);
        // img=new ImageView(new Image("target/image1.png"));

    }


    @FXML
    void edit(ActionEvent event) throws IOException {
        if(editbtn.getText().equals("edit")) {

            pricetxt.setEditable(true);
            editbtn.setText("done");

        }
        else{



            pricetxt.setEditable(false);
            editbtn.setText("edit");
            flower.setPrice(Double.parseDouble(pricetxt.getText().toString()));
            ArrayList<Object> arr=new ArrayList<>();
            arr.add("#updateflower");
            arr.add(flower.getId());
            arr.add(flower.getPrice());
            arr.add( flower.getDiscount());

            App.getClient().sendToServer(arr);

        }
    }
    @FXML
    void returnbtn(ActionEvent event) throws IOException {
        try {

            App.setRoot("catalogboundary");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
