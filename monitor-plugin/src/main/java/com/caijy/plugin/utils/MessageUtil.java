package com.caijy.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * @Description
 * 右下角的吐司提示
 * @Date 2020/8/28
 * @Created by fangla
 */
public class MessageUtil {
    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }

    public static void info(String message) {
        Notification info = notificationGroup.createNotification(message, NotificationType.INFORMATION);
        Notifications.Bus.notify(info, null);
    }

    public static void error(String message) {
        Notification error = notificationGroup.createNotification(message, NotificationType.ERROR);
        Notifications.Bus.notify(error, null);
    }

    public static void warn(String message) {
        Notification warn = notificationGroup.createNotification(message, NotificationType.WARNING);
        Notifications.Bus.notify(warn, null);
    }
}
