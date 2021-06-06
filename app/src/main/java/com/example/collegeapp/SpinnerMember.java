package com.example.collegeapp;

/*

 NAME: SpinnerMember - This is the entity class for the spinner used in the dropdown to get the
                       college of the user

 DESCRIPTION: When a new user logs in for the first time, a spinner is used to show a dropdown with
              the names of college from which the user is able to select one.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class SpinnerMember  {

    private String college_name;


    /*

    NAME: SpinnerMember::SpinnerMember() - default constructor for the class SpinnerMember

    SYNOPSIS: public SpinnerMember()

    DESCRIPTION: default constructor for the class SpinnerMember

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public SpinnerMember(){

    }

    /*

    NAME: SpinnerMember::SpinnerMember() - constructor for the class SpinnerMember

    SYNOPSIS: public SpinnerMember(String college_name)
              college_name: name of the college to be shown in the dropdown

    DESCRIPTION: constructor for the class SpinnerMember

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public SpinnerMember(String college_name) {
        this.college_name = college_name;
    }


    /*

    NAME: SpinnerMember::getCollege_name() - gets the name of the college

    SYNOPSIS: public void getCollege_name()

    DESCRIPTION: This function gets the name of the college to be displayed

    RETURNS: The name of the college to be displayed

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public String getCollege_name() {
        return college_name;
    }

    /*

    NAME: SpinnerMember::setCollege_name() - sets the name of the college

    SYNOPSIS: public void setCollege_name(String college_name)
              college_name: name of the college to be displayed

    DESCRIPTION: This function sets the name of the college to be displayed

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }
}
