package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;

import ci553.happyshop.catalogue.Product;


public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

    //Code for customerView to access model for effective collaboration with product items (Thomas Mcmahon Dixon)


    public CustomerModel getCusModel() {
        return cusModel;
    }

    //Code to allow customerView to update trolley with quantity spinners (Thomas Mcmahon Dixon)

    public void updateTrolley() {

        cusModel.cusView.refreshTrolleyView();

    }

    public void updateProductQuantity(Product product, int newQuantity) {
        cusModel.updateProductQuantity(product, newQuantity);
    }

    public void removeProductFromTrolley(Product product) {
        cusModel.removeProductFromTrolley(product);
    }
}
