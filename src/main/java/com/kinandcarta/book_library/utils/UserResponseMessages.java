package com.kinandcarta.book_library.utils;

import lombok.experimental.UtilityClass;

/**
 * Utility class containing response messages related to User operations.
 */
@UtilityClass
public class UserResponseMessages {
    public static final String USER_DATA_UPDATED_RESPONSE = "Users data successfully updated.";

    public static final String USER_ROLE_UPDATED_RESPONSE = "Role successfully changed.";

    public static final String USER_DELETED_RESPONSE = "The user has been successfully deleted.";

    public static final String USER_PASSWORD_UPDATED_RESPONSE = "The password has been successfully updated.";

    public static final String USER_ROLE_ALREADY_ASSIGNED_RESPONSE = "User is already assigned to this role.";
}
