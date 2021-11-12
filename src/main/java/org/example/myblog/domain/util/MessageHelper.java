package org.example.myblog.domain.util;

import org.example.myblog.domain.User;

public class MessageHelper {
    public static String getAuthorName(User author) {
        return author != null ? author.getUsername() : "<none>";
    }
}
