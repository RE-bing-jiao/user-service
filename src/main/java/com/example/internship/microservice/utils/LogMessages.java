package com.example.internship.microservice.utils;

public class LogMessages {
    public static final String USER_CREATED = "User created successfully with ID: {}";
    public static final String USER_RETRIEVED = "User retrieved successfully with ID: {}";
    public static final String USERS_RETRIEVED = "Retrieved {} users with pagination. Page: {}, Size: {}";
    public static final String USER_UPDATED = "User updated successfully with ID: {}";
    public static final String USER_ACTIVATED = "User activated successfully with ID: {}";
    public static final String USER_DEACTIVATED = "User deactivated successfully with ID: {}. Related cards deactivated.";
    public static final String USER_DELETED = "User deleted successfully with ID: {}";
    public static final String USER_NOT_FOUND = "User not found with ID: {}";
    public static final String USER_SEARCH_STARTED = "Starting user search with name: {}, surname: {}";
    public static final String USER_EMAIL_DUPLICATE = "Email already exists: {}";
    public static final String EMAIL_DUPLICATE_MESSAGE = "Email already exists. Please use a different email.";

    public static final String CARD_CREATED = "Payment card created successfully with ID: {} for user ID: {}";
    public static final String CARD_RETRIEVED = "Payment card retrieved successfully with ID: {}";
    public static final String CARDS_RETRIEVED = "Retrieved {} payment cards with pagination. Page: {}, Size: {}";
    public static final String CARDS_BY_USER_RETRIEVED = "Retrieved {} payment cards for user ID: {} with pagination. Page: {}, Size: {}";
    public static final String CARD_UPDATED = "Payment card updated successfully with ID: {}";
    public static final String CARD_ACTIVATED = "Payment card activated successfully with ID: {}";
    public static final String CARD_DEACTIVATED = "Payment card deactivated successfully with ID: {}";
    public static final String CARD_DELETED = "Payment card deleted successfully with ID: {}";
    public static final String CARD_NOT_FOUND = "Payment card not found with ID: {}";
    public static final String CARD_LIMIT_EXCEEDED = "Card limit exceeded for user ID: {}. Maximum 5 cards allowed.";
    public static final String CARD_USER_NOT_FOUND = "User not found with ID: {} while creating payment card";
    public static final String CARD_NUMBER_DUPLICATE = "Card number already exists: {}";

    public static final String ERROR_OCCURRED = "An error occurred in {}: {}";
    public static final String VALIDATION_ERROR = "Validation error occurred: {}";

    public static final String METHOD_START = "Starting execution of method: {}";
    public static final String METHOD_END = "Completed execution of method: {}";

    public LogMessages() {
    }
}
