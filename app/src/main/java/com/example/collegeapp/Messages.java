package com.example.collegeapp;

/*

 NAME: Messages - This is the Entity class for Messages

 DESCRIPTION: This class retrieves the messages' information and stores in firebase database by
                its attributes

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class Messages {

    private String from, message, type, message_id, time, date, to, name;


    /*

     NAME: Messages::Messages() - default constructor for the class Contacts

     SYNOPSIS: public Messages()


     DESCRIPTION: default constructor for the class Contacts

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public Messages(){

    }

    /*

     NAME: Messages::Messages() - constructor for the class Contacts

     SYNOPSIS: public Messages(String from)
                from: Id of the sender as a string

     DESCRIPTION: When the chat page loads, this method is called.
                   The member variable from is initialized.
                   The function retrieves data needed to display in the conversations page.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public Messages(String from) {
        this.from = from;
    }

    /*

     NAME: Messages::getFrom() - returns the user id of the sender

     SYNOPSIS: public String getFrom()

     DESCRIPTION: This function returns user id of the sender

     RETURNS: user id of the sender as string

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public String getFrom() {
        return from;
    }


    /*

     NAME: Messages::setFrom() - sets the id of the sender

     SYNOPSIS: public void setFrom(String from)
               from: A string value of the id of the sender

     DESCRIPTION: This function sets the sender's id as the string received in the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setFrom(String from) {
        this.from = from;
    }


    /*

     NAME: Messages::getMessage() - returns the message

     SYNOPSIS: public String getMessage()

     DESCRIPTION: This function returns the message sent/received

     RETURNS: message as string

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public String getMessage() {
        return message;
    }

    /*

     NAME: Messages::setMessage() - sets the message sent

     SYNOPSIS: public void setMessage(String message)
               message: sent message as a string

     DESCRIPTION: This function sets the message sent

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setMessage(String message) {
        this.message = message;
    }

    /*

    NAME: Messages::getType() - returns the type of message

    SYNOPSIS: public String getType()

    DESCRIPTION: This function returns the type of message(text,image) sent/received

    RETURNS: type of message as string

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public String getType() {
        return type;
    }


    /*

     NAME: Messages::setType() - sets the type of message sent

     SYNOPSIS: public void setType(String type)
               type: type of the sent message as a string

     DESCRIPTION: This function sets the type(text,image) of the message sent

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setType(String type) {
        this.type = type;
    }


    /*

     NAME: Messages::Messages() - constructor for the class Contacts

     SYNOPSIS: public Messages(String from, String message, String type, String message_id, String time, String date, String to, String name)
                from: sender of the message
                message: message as string
                type: type of message(text/image)
                message_id: unique id for a message
                time: time when message is sent
                date: date when message is sent
                to: receiver of the message
                name: user name of the sender

     DESCRIPTION: When the chat page loads, this method is called.
                   The member variables are initialized.
                   The function retrieves data needed to display in the conversations page.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public Messages(String from, String message, String type, String message_id, String time, String date, String to, String name) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.message_id = message_id;
        this.time = time;
        this.date = date;
        this.to = to;
        this.name = name;
    }

    /*

    NAME: Messages::getMessageId() - returns the id of message

    SYNOPSIS: public String getMessageId()

    DESCRIPTION: This function returns the unique id of the message

    RETURNS: id of message as string

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public String getMessage_id() {
        return message_id;
    }


    /*

     NAME: Messages::setMessage_id() - sets the unique id message sent

     SYNOPSIS: setMessage_id(String message_id)
               message_id: unique id of the sent message as a string

     DESCRIPTION: This function sets the unique id message sent

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    /*

    NAME: Messages::getTime() - returns the time when message is sent

    SYNOPSIS: public String getTime()

    DESCRIPTION: This function returns the time when message is sent

    RETURNS: time of message as string

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public String getTime() {
        return time;
    }

    /*

     NAME: Messages::setTime() - sets the time when the message is sent

     SYNOPSIS: setTime(String time)
               time: time when the message is sent

     DESCRIPTION: This function sets the time when the message is sent

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setTime(String time) {
        this.time = time;
    }

    /*

    NAME: Messages::getDate() - returns the date when message is sent

    SYNOPSIS: public String getDate()

    DESCRIPTION: This function returns the date when message is sent

    RETURNS: date of message as string

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public String getDate() {
        return date;
    }


    /*

     NAME: Messages::setDate() - sets the date when the message is sent

     SYNOPSIS: setDate(String date)
               date: date when the message is sent

     DESCRIPTION: This function sets the date when the message is sent

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setDate(String date) {
        this.date = date;
    }

    /*

     NAME: Messages::getTo() - returns the user id of the receiver

     SYNOPSIS: public String getTo()

     DESCRIPTION: This function returns user id of the receiver

     RETURNS: user id of the receiver as string

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public String getTo() {
        return to;
    }


    /*

     NAME: Messages::setTo() - sets the id of the receiver

     SYNOPSIS: public void setTo(String to)
               from: A string value of the id of the receiver

     DESCRIPTION: This function sets the receiver's id as the string received in the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setTo(String to) {
        this.to = to;
    }

    /*

     NAME: Messages::getName() - returns the name of the sender

     SYNOPSIS: public String getName()

     DESCRIPTION: This function returns name of the sender

     RETURNS: name id of the receiver as string

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public String getName() {
        return name;
    }

    /*

     NAME: Messages::setName() - sets the name of the sender

     SYNOPSIS: public void setName(String name)
               from: A string value of the name of the sender

     DESCRIPTION: This function sets the sender's name as the string received in the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public void setName(String name) {
        this.name = name;
    }
}
