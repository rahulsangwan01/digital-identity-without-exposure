package com.loganalysis;

/**
 * DataProcessor: A module to simulate business logic and error handling.
 * This class captures both successful operations and deliberate failures
 * for the Hadoop MapReduce analysis phase.
 */
public class DataProcessor {

    public static void main(String[] args) {
        // Log the start of the module
        LoggerUtil.log("INFO", "MODULE_START", "DataProcessor engine initiated");

        int numerator;
        int denominator;

        // Check if arguments were passed from the script/command line
        try {
            if (args.length >= 2) {
                numerator = Integer.parseInt(args[0]);
                denominator = Integer.parseInt(args[1]);
            } else {
                // Default fallback values if no arguments provided
                numerator = 100;
                denominator = 10;
            }

            System.out.println("-------------------------------------------");
            System.out.println("Processing Data: " + numerator + " / " + denominator);
            System.out.println("-------------------------------------------");

            // Perform the calculation
            // If denominator is 0, this will throw an ArithmeticException
            int result = numerator / denominator;

            // Log Success
            System.out.println("SUCCESS: Result is " + result);
            LoggerUtil.log("INFO", "CALCULATION_SUCCESS", "Result: " + result);

        } catch (ArithmeticException e) {
    // 1. Log the Error for Hadoop and Postgres
    System.err.println("[!] ERROR DETECTED: Division by zero.");
    LoggerUtil.log("ERROR", "MATH_ERROR", "Critical failure: Division by zero attempted");
    
    // 2. Add a tiny pause (0.5 seconds)
    // This gives the Postgres driver enough time to finish the 'INSERT'
    try { Thread.sleep(500); } catch (InterruptedException ie) { /* ignore */ }
    
} catch (NumberFormatException e) {
            // Handle cases where non-numeric arguments are passed
            System.err.println("[!] ERROR DETECTED: Invalid input format.");
            LoggerUtil.log("ERROR", "INPUT_ERROR", "Non-numeric input detected: " + e.getMessage());
            
        } catch (Exception e) {
            // Generic catch-all for unexpected issues
            LoggerUtil.log("ERROR", "SYSTEM_FAULT", "Unexpected exception: " + e.getMessage());
            
        } finally {
            // Log the end of the module execution
            LoggerUtil.log("INFO", "MODULE_END", "DataProcessor task completed");
            System.out.println("-------------------------------------------");
        }
    }
}