package edu.carroll.bankapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * This class is used to display one-time (flash) messages to the user, such as the result of an operation.
 */
public class FlashHelper {
    /**
     * Adds a message to be displayed after redirect
     *
     * @param redirectAttributes - a RedirectAttributes object to store flash attributes to
     * @param message            - the message to display to the user
     */
    public static void flash(RedirectAttributes redirectAttributes, String message) {
        List<String> messages = new ArrayList<>();

        // Retrieve existing messages from the flash attribute
        List<String> existingMessages = (List<String>) redirectAttributes.getFlashAttributes().get("messages");

        // Check if there are existing messages
        if (existingMessages != null) {
            messages.addAll(existingMessages);
        }

        messages.add(message);

        // Set the updated list back to the flash attribute
        redirectAttributes.addFlashAttribute("messages", messages);
    }
}