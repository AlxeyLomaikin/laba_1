package laba_1.model;

import java.io.Serializable;

/**
 * Created by User on 01.04.2015.
 */
public class doctor implements Serializable {
    //������������ ID � �������
    private int ID;
    private String name;
    private String surname;
    private String occupation;
    private int age;
    public doctor() {
    }
    public doctor(String name, String surname, String occupation, int age) {
        this.name = name;
        this.surname = surname;
        this.occupation = occupation;
        this.age = age;
    }
    public int getID () {return ID;}
    public void setID (int ID) {this.ID = ID;}
    public String getName() {return name;}
    public void setName (String name) {this.name = name;}
    public String getSurname () {
        return surname;
    }
    public void setSurname (String surname){
        this.surname = surname;
    }
    public String getOccupation () {
        return occupation;
    }
    public void setOccupation (String occupation){
        this.occupation = occupation;
    }
    public int getAge () {return age;}
    public void setAge (int age){
        this.age = age;
    }
}
