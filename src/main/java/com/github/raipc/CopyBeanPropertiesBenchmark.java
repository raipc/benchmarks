package com.github.raipc;

import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class CopyBeanPropertiesBenchmark {
    private WebServiceNotification webServiceNotification;

    @Setup
    public void prepareWebNotification() {
        WebServiceNotification notification = new WebServiceNotification();
        notification.setFirstWebNotificationConfigName("telegram-group");
        notification.setRepeatWebNotificationConfigName("telegram-group");
        notification.setDisabled(false);
        notification.setId(1234L);
        notification.setName("test notification");
        notification.setRuleName("test-rule");
        notification.setSchedule("");
        notification.setUseInThresholdOnly(false);
        webServiceNotification = notification;
    }

    @SneakyThrows
    private static <T> T cloneBean(T bean) {
        final T copy = (T) bean.getClass().newInstance();
        org.springframework.beans.BeanUtils.copyProperties(bean, copy);
        return copy;
    }

    @Benchmark
    public NotificationModel withApacheCommons() throws InvocationTargetException, IllegalAccessException {
        final NotificationModel notificationModel = new NotificationModel();
        BeanUtils.copyProperties(notificationModel, webServiceNotification);
        return notificationModel;
    }

    @Benchmark
    public NotificationModel withSpring() {
        final NotificationModel notificationModel = new NotificationModel();
        org.springframework.beans.BeanUtils.copyProperties(webServiceNotification, notificationModel);
        return notificationModel;
    }

    @Benchmark
    public WebServiceNotification cloneWithApacheCommons() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return (WebServiceNotification)BeanUtils.cloneBean(webServiceNotification);
    }

    @Benchmark
    public WebServiceNotification cloneWithSpring() throws InstantiationException, IllegalAccessException {
        return cloneBean(webServiceNotification);
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public class NotificationModel {
        private long id;
        private String name;
        private String ruleName;
        private String schedule;
        private String bodyFirst;
        private String bodyRepeat;
        private String bodyCancel;
        private String recipients;
        private String firstSubject;
        private String repeatSubject;
        private String cancelSubject;
        private boolean merge;
        private boolean firstDetails;
        private boolean repeatDetails;
        private boolean cancelDetails;
        private boolean delete;
        private boolean useInThresholdOnly;
        private boolean enabled;
        private String repeatIntervalTime;
        private int priority;
        private Boolean firstEnabled;
        private Boolean repeatEnabled;
        private Boolean cancelEnabled;

        private String firstWebNotificationConfigName;
        private String repeatWebNotificationConfigName;
        private String cancelWebNotificationConfigName;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static abstract class Notification {
        private long id;
        private String name;
        private boolean useInThresholdOnly;
        private boolean disabled;
        private String ruleName;
        private String schedule;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WebServiceNotification extends Notification {
        private String firstWebNotificationConfigName;
        private String repeatWebNotificationConfigName;
        private String cancelWebNotificationConfigName;
    }
}
