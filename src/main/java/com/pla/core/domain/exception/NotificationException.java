package com.pla.core.domain.exception;

/**
 * Created by Admin on 6/18/2015.
 */
public class NotificationException extends RuntimeException{

    public NotificationException(String message) {
        super(message);
    }

    public static void raiseDuplicateEntryException(){
        throw  new NotificationException("Notification Template is already uploaded");
    }

    public static void raiseErrorInReload(){
        throw new NotificationException("Notification Template Reload Failed");
    }

    public static void raiseProcessIsNotValid(String processType,String lineOfBusiness){
        throw new NotificationException("The process " + processType + " is not associated with " + lineOfBusiness);
    }
}
