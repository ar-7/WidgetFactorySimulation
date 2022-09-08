package com.company;

import java.util.Scanner;

public class Main {

    // Declaring variables
    static Scanner scanInput; // initialise scanner for input
    static int factoryQuantity = 0; // int for storing the quantities of factory available
    static int centralWarehouseTotal = 0; // int for logging the number of widgets delivered to the central warehouse
    static int productionQuantity = 5000; // number of widgets to be produced
    static int productionDistribution = 0; // int for storing the number of widgets each factory will be allocated to produce
    static int productionRemainder = 0; // int for storing the result of the calculated left over widgets
    static int completeCounter = 0; // int for counting the factories that have completed their production run

    public static void main(String[] args) {

        scanInput = new Scanner(System.in); // Creates a scanner object for taking input

        /// Menu begins
        System.out.println("Welcome User!\nThis application allows you to define parameters for the production of 5000 widgets.");
        System.out.println("\nPlease define how many foundries are available:");

        // validate against wrong input. Loops until correct input is given
        while (!scanInput.hasNextInt() || (factoryQuantity = scanInput.nextInt()) <= 0) {
            System.out.println("Sorry, I'm going to need a positive number, please try again.");
            scanInput.nextLine(); // This is important! Stuck in the loop otherwise
        }

        System.out.println("\nThank you, " + factoryQuantity + " foundries are available.\n"); // Response to user

        // Divide production quantity by number of factories to split up production among factories
        productionDistribution = productionQuantity / factoryQuantity;

        // Work out remainder of production run
        productionRemainder = productionQuantity % factoryQuantity;

        // Loop that calls the production runs for the defined number of factories.
        for (int i = 0; i < factoryQuantity; i++) {
            if (i < productionRemainder) { // statement checks whether remainder should be added to the factory's allocation
                factoryAllocation(i + 1, productionDistribution + 1); // sends the factory id and production amount plus remainder to the factory allocator
            } else {
                factoryAllocation(i + 1, productionDistribution); // sends the factory id and production amount to the factory allocator
            }
        }
    }

    // Function for allocating factory id and widget amount to processing threads
    public static void factoryAllocation(int factoryID, int productionDistribution) {
        // Generates a new thread running the widgetOutput function
        Thread factoryThread = new Thread(() -> widgetOutput(factoryID, productionDistribution)); // Widget output being called with lambda expression
        factoryThread.start(); // Starts the new thread
    }

    // Function that simulates widget production in the factory
    static void widgetOutput(int factoryID, int productionDistribution) {

        // Loops the number of production runs
        for (int i = 1; i <= productionDistribution; i++) {
            CentralWarehouseReceiving.activate(i, factoryID); // Trigger a new thread to increment the central warehouse stock total
        }

        // Production run is complete at this point
        completeCounterTracker.activate(); // Triggers a thread that adds the factory to the completed factory total

        // Report completion
        System.out.println("Factory " + factoryID + " is complete. " + completeCounter + " factories are complete.");
    }
}

// Class containing a synchronized thread for tracking the widgets delivered to the central warehouse
// Synchronization protects the shared resource of the completeCounter variable
class completeCounterTracker extends Thread {
    public synchronized static void activate() {
        Main.completeCounter++; // tick up the counter of complete factories
    }
}

// Class containing a synchronized thread for tracking the widgets delivered to the central warehouse
// Synchronization protects the shared resource of the centralWarehouseTotal variable
class CentralWarehouseReceiving extends Thread {
    public synchronized static void activate(int widgetNo, int factoryID) {
        Main.centralWarehouseTotal = Main.centralWarehouseTotal + 1;
        System.out.println("Widget #" + widgetNo + " received from factory " + factoryID); // reports delivery of a widget
        System.out.println("*** Central warehouse total received: " + Main.centralWarehouseTotal + " ***"); // Reports the total number of widgets in the warehouse
    }
}