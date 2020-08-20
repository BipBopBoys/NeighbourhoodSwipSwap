package com.criticalhit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Drawing extends Application {
    Solution globalBestSolution;

    public Drawing(Solution solution)
    {
        this.globalBestSolution = solution;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        Canvas canvas = new Canvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawBoxes(gc, canvas, globalBestSolution);
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    // Draw method
    private void drawBoxes(GraphicsContext gc, Canvas canvas, Solution solution) {
        try {
            gc.setFill(Color.BLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            int xLoc = 0;
            int yLoc = (int) canvas.getHeight();

            for(int i = 0; i < solution.getNumNeighbourhoods(); i++)
            {
                Neighbourhood neighbourhood = solution.getNeighbourhood(i);

                for(int j = 0; j < solution.getNeighbourhood(i).getBoxes().size(); j++)
                {
                    Box box = solution.getNeighbourhood(i).getBoxes().get(j);

                    // Provide the offset from the bottom of the neighbourhood.
                    yLoc -= box.getHeight();

                    // Draw the box.
                    gc.fillRect(xLoc, yLoc, box.getWidth(), box.getHeight());
                    gc.strokeRect(xLoc, yLoc, box.getWidth(), box.getHeight());

                    xLoc += box.getWidth(); // Move gc along to the next box.
                    yLoc += box.getHeight(); // Remove the offset provided earlier.
                }
                // Move gc to the start of the next neighbourhood.
                xLoc = 0;
                yLoc -= neighbourhood.getTotalHeight();
            }




        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
