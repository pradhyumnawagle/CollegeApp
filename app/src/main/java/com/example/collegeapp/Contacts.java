package com.example.collegeapp;

/*

 NAME: Contacts - This is the Entity class for User information.

 DESCRIPTION: This class retrieves the users information and stores in firebase database by
                its attributes

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
public class Contacts {

    public String uname, ubio, uimage, ucollege;



    public Contacts(){

    }

    /*

     NAME: Contacts::Contacts() - constructor for the class Contacts

     SYNOPSIS: public Contacts(String uname, String ubio, String uimage)
                uname: A string that is the user's name
                ubio: A string value that is the user's bio
                uimage: The user's display image

     DESCRIPTION: When the personal conversation i.e. chat page loads, this method is called.
                   The member variables are initialized.
                   The function retrieves data needed to display in the conversations page.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

    public Contacts(String uname, String ubio, String uimage) {
        this.uname = uname;
        this.ubio = ubio;
        this.uimage = uimage;
        this.ucollege = ucollege;
    }

    /*

     NAME: Contacts::getUname() - returns the user name

     SYNOPSIS: public String getUname()

     DESCRIPTION: This function returns the user's name

     RETURNS: a string value of the username

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public String getUname() {
        return uname;
    }


    /*

     NAME: Contacts::setUname() - sets the user name

     SYNOPSIS: public void setUname(String uname)
               uname: A string value of the username to be set

     DESCRIPTION: This function sets the user's name as the string received as the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /*

     NAME: Contacts::getUname() - returns the user's bio

     SYNOPSIS: public String getUbio()

     DESCRIPTION: This function returns the user's bio

     RETURNS: a string value of the user's bio

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public String getUbio() {
        return ubio;
    }


    /*

     NAME: Contacts::setUbio() - sets the user's bio

     SYNOPSIS: public void setUbio(String ubio)
               ubio: A string value of the user's bio to be set

     DESCRIPTION: This function sets the user's bio as the string received as the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public void setUbio(String ubio) {
        this.ubio = ubio;
    }

    /*

     NAME: Contacts::getUname() - returns the user's display image

     SYNOPSIS: public String getImage()

     DESCRIPTION: This function returns the user's display image as a string

     RETURNS: a string value of the user's image

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public String getUimage() {
        return uimage;
    }


    /*

     NAME: Contacts::setUimage() - sets the user's image

     SYNOPSIS: public void setUbio(String uimage)
               uimage: A string value of the user's image to be set

     DESCRIPTION: This function sets the user's image as the string received as the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public void setUimage(String uimage) {
        this.uimage = uimage;
    }


    /*

     NAME: Contacts::getUcollege() - returns the user's college name

     SYNOPSIS: public String getUcollege()

     DESCRIPTION: This function returns the user's college name

     RETURNS: a string value of the college associated woith the user

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public String getUcollege() {
        return ucollege;
    }

    /*

     NAME: Contacts::setUcollege() - sets the user's college name

     SYNOPSIS: public void setUcollege(String ucollege)
               ucollege: A string value of the user's college to be set

     DESCRIPTION: This function sets the user's college name as the string received as the parameter

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    public void setUcollege(String ucollege) {
        this.ucollege = ucollege;
    }
}
