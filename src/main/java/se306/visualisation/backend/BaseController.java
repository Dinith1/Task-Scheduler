package se306.visualisation.backend;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseController {


    /**
     * BaseController for all other controllers to inherit that require access to the name database
     * and other application wide objects
     *
     *  @author Dhruv Phadnis, Vanessa Ciputra
     */

        protected Stage primaryStage;

        public final void setup(Stage primaryStage) {
            this.primaryStage = primaryStage;
        }

        /**
         * Called before new scene is visible and after FXML fields are initialised.
         * Equivalent to Initializable interface.
         */
        public void init() {
            // Empty by default
        }
}
