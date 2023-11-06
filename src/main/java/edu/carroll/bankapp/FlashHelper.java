package edu.carroll.bankapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class FlashHelper {
    // Helper function to append messages to the flash attribute
    public static void flash(RedirectAttributes redirectAttributes, String message) {
        List<String> messages = new ArrayList<String>();

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