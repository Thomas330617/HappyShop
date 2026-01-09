package ci553.happyshop.client.customer;
import ci553.happyshop.catalogue.Product; //To allow customer view to work with product information (Thomas Mcmahon Dixon)
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import javax.print.DocFlavor;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;

/**
 * The CustomerView is separated into two sections by a line :
 *
 * 1. Search Page – Always visible, allowing customers to browse and search for products.
 * 2. the second page – display either the Trolley Page or the Receipt Page
 *    depending on the current context. Only one of these is shown at a time.
 */

public class CustomerView  {
    public CustomerController cusController;

    public CustomerModel model;

    public void setModel(CustomerModel model) {
        this.model = model;
    }

    private final int WIDTH = UIStyle.customerWinWidth;
    private final int HEIGHT = UIStyle.customerWinHeight;
    private final int COLUMN_WIDTH = WIDTH / 2 - 10;

    private HBox hbRoot; // Top-level layout manager
    private VBox vbTrolleyPage;  //vbTrolleyPage and vbReceiptPage will swap with each other when need
    private VBox vbReceiptPage;

    TextField tfId; //for user input on the search page. Made accessible so it can be accessed or modified by CustomerModel
    TextField tfName; //for user input on the search page. Made accessible so it can be accessed by CustomerModel

    //Code to connect button sound method to buttons (Thomas Mcmahon Dixon)


    //Code to play sound when buttons are clicked (THOMAS MCMAHON DIXON)
    private void playButtonSound() {


        try {
            //Code to access sound from resource sound directory
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sounds/buttonClick.wav")
            );

            //Code to open the audio clip to be played (Thomas Mcmahon Dixon)
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            //code to play the sound retrieved (Thomas Mcmahon Dixon)
            clip.start();
        } catch (Exception e) {
            //If the sound fails, UI will still work (Thomas Mcmahon Dixon)
            System.err.println("Error: Sound button not playing");
        }
    }

    //four controllers needs updating when program going on
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductInfo;//product text info in searchPage
    private VBox vbTrolleyItems; //was changed to represent each row as a grid row (Thomas Mcmahon Dixon)
    private TextArea taReceipt;//in receipt page

    // Holds a reference to this CustomerView window for future access and management
    // (e.g., positioning the removeProductNotifier when needed).
    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        vbTrolleyPage = CreateTrolleyPage();
        vbReceiptPage = createReceiptPage();

        // Create a divider line
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(4);
        line.setStroke(Color.PINK);
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4); // Give it some space
        lineContainer.setAlignment(Pos.CENTER);

        hbRoot = new HBox(10, vbSearchPage, lineContainer, vbTrolleyPage); //initialize to show trolleyPage
        hbRoot.setAlignment(Pos.CENTER);
        hbRoot.setStyle(UIStyle.rootStyle);

        Scene scene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Customer Client");
        WinPosManager.registerWindow(window,WIDTH,HEIGHT); //calculate position x and y for this window
        window.show();
        viewWindow=window;// Sets viewWindow to this window for future reference and management.
    }

    private VBox createSearchPage() {
        Label laPageTitle = new Label("Search by Product ID/Name");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        Label laId = new Label("ID:      ");
        laId.setStyle(UIStyle.labelStyle);
        tfId = new TextField();
        tfId.setPromptText("eg. 0001");
        tfId.setStyle(UIStyle.textFiledStyle);
        HBox hbId = new HBox(10, laId, tfId);

        Label laName = new Label("Name:");
        laName.setStyle(UIStyle.labelStyle);
        tfName = new TextField();
        tfName.setPromptText("implement it if you want");
        tfName.setStyle(UIStyle.textFiledStyle);
        HBox hbName = new HBox(10, laName, tfName);

        Label laPlaceHolder = new Label(  " ".repeat(15)); //create left-side spacing so that this HBox aligns with others in the layout.
        Button btnSearch = new Button("Search");
        btnSearch.setStyle(UIStyle.buttonStyle);
        btnSearch.setOnAction(this::buttonClicked);
        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        btnAddToTrolley.setOnAction(this::buttonClicked);
        HBox hbBtns = new HBox(10, laPlaceHolder,btnSearch, btnAddToTrolley);

        ivProduct = new ImageView("imageHolder.jpg");
        ivProduct.setFitHeight(60);
        ivProduct.setFitWidth(60);
        ivProduct.setPreserveRatio(true); // Image keeps its original shape and fits inside 60×60
        ivProduct.setSmooth(true); //make it smooth and nice-looking

        lbProductInfo = new Label("Thank you for shopping with us.");
        lbProductInfo.setWrapText(true);
        lbProductInfo.setMinHeight(Label.USE_PREF_SIZE);  // Allow auto-resize
        lbProductInfo.setStyle(UIStyle.labelMulLineStyle);
        HBox hbSearchResult = new HBox(5, ivProduct, lbProductInfo);
        hbSearchResult.setAlignment(Pos.CENTER_LEFT);

        VBox vbSearchPage = new VBox(15, laPageTitle, hbId, hbName, hbBtns, hbSearchResult);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 15px;");

        return vbSearchPage;
    }

    private VBox CreateTrolleyPage() {
        Label laPageTitle = new Label("🛒🛒  Trolley 🛒🛒");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

      vbTrolleyItems = new VBox(10);
      vbTrolleyItems.setPrefSize(WIDTH/2, HEIGHT-50); //Changed to allow appropriate colum for interface (Thomas Mcmahon Dixon)
      vbTrolleyItems.setStyle("-fx-padding: 10px;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setOnAction(this::buttonClicked);
        btnCheckout.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnCancel,btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        vbTrolleyPage = new VBox(15, laPageTitle, vbTrolleyItems, hbBtns); //Edited to allow rows (Thomas Mcmahon Dixon)
        vbTrolleyPage.setPrefWidth(COLUMN_WIDTH);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 15px;");
        return vbTrolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-50);

        Button btnCloseReceipt = new Button("OK & Close"); //btn for closing receipt and showing trolley page
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);

        btnCloseReceipt.setOnAction(this::buttonClicked);

        vbReceiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        vbReceiptPage.setPrefWidth(COLUMN_WIDTH);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }


    private void buttonClicked(ActionEvent event) {

        //Allows button sound to be played in customer interface (Thomas Mcmahon Dixon)
        playButtonSound();

        try{
            Button btn = (Button)event.getSource();
            String action = btn.getText();
            if(action.equals("Add to Trolley")){
                showTrolleyOrReceiptPage(vbTrolleyPage); //ensure trolleyPage shows if the last customer did not close their receiptPage
            }
            if(action.equals("OK & Close")){
                showTrolleyOrReceiptPage(vbTrolleyPage);
            }
            cusController.doAction(action);
        }
        catch(SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Code to refresh trolley items and adds a spinner for increasing/decreasing quantity (Thomas Mcmahon Dixon)

    public void refreshTrolleyView() {
        vbTrolleyItems.getChildren().clear(); //Will erase any previous rows if needed (Thomas Mcmahon Dixon)
        for (Product p : cusController.getCusModel().getTrolley()) {
            //will retrieve trolley product information from the codebase models (Thomas Mcmahon Dixon)

            //Code to  calculate the subtotal based on product increase (Thomas Mcmahon Dixon)

            double subtotal = p.getUnitPrice() * p.getOrderedQuantity();

            String productInfo = String.format(
                    "%s - %s | Quantity: %d |   Subtotal: £%.2f",
                    p.getProductId(),
                    p.getProductDescription(),
                    p.getOrderedQuantity(),
                    subtotal
            );


            Label lbl = new Label(productInfo);

            //Allows text to display clearly (Thomas Mcmahon Dixon)
            lbl.setWrapText(true);
            lbl.setMaxWidth(vbTrolleyItems.getPrefWidth() - 20);

            //Spinners to allow quantity control

            Spinner<Integer> spQty = new Spinner<>(1, p.getStockQuantity(), p.getOrderedQuantity());
            spQty.setEditable(true); //Allows user to edit spinner controls (Thomas Mcmahon Dixon)


            spQty.valueProperty().addListener((obs, oldVal, newVal) -> {
                cusController.updateProductQuantity(p, newVal); //Will change receipts when edited again (Thomas Mcmahon Dixon)
            });

            //Button to allow customers to remove products they may not want from trolley (Thomas Mcmahon Dixon)

            Button btnRemove = new Button("Remove item");
            btnRemove.setStyle(UIStyle.buttonStyle); //Ensures button is styled with the interface
            btnRemove.setMinWidth(120); //Ensures text fits in button
            btnRemove.setOnAction(e -> {
                cusController.removeProductFromTrolley(p);
            });


            //Ensure layout is correctly set (Thomas Mcmahon Dixon)

            HBox row = new HBox(10, lbl, spQty, btnRemove);
            row.setAlignment(Pos.CENTER_LEFT);
            vbTrolleyItems.getChildren().add(row);
        }
    }


    public void update(String imageName, String searchResult, String trolley, String receipt) {

        ivProduct.setImage(new Image(imageName));
        lbProductInfo.setText(searchResult);
        // Ensures trolley display is updated since it's edited to be interactive (Thomas Mcmahon Dixon)
        refreshTrolleyView();
        if (!receipt.equals("")) {
            showTrolleyOrReceiptPage(vbReceiptPage);
            taReceipt.setText(receipt);
        }
    }

    // Replaces the last child of hbRoot with the specified page.
    // the last child is either vbTrolleyPage or vbReceiptPage.
    private void showTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(viewWindow.getX(), viewWindow.getY(),
                  viewWindow.getWidth(), viewWindow.getHeight());
    }
}
