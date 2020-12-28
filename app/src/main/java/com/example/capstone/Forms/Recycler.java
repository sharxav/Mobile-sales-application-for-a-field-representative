package com.example.capstone.Forms;

public class Recycler {
    String fname,lname,company,email;
    String phone;

    public Recycler(String fname,String lname,String company,String email,String phone)
    {
        this.fname=fname;
        this.lname=lname;
        this.company=company;
        this.email=email;
        this.phone=phone;
    }

    public Recycler() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
