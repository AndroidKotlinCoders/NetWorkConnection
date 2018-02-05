package com.kotlincoders.android.networkconnection;


import android.os.Parcel;
import android.os.Parcelable;

public class Repository implements Parcelable {

    private String name, full_name,html_url;
    private Owner owner;

//    public Repository() {
//
//    }
//
//    public Repository(String name, Owner owner,String fullname) {
//
//        setName(name);
//        setOwner(owner);
//        setFullName(fullname);
//    }

    public Repository(Parcel in) {

        String[] data = new String[3];

        in.readStringArray(data);

        this.name = data[0];
        this.owner = new Owner(data[1]);
        this.full_name = data[2];
        this.html_url =data[3];
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;

    }

    public String getFullName() {

        return full_name;
    }

    public void setFullName(String fullname) {

        this.full_name = fullname;
    }

    public String getHTMLurl() {

        return html_url;
    }

    public void setHTMLurl(String html_url) {

        this.html_url = html_url;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{this.name, this.owner.getLogin(), this.full_name,this.html_url});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Repository createFromParcel(Parcel in) {

            return new Repository(in);

        }

        public Repository[] newArray(int size) {

            return new Repository[size];
        }

    };

    public class Owner {

        public Owner() {

        }

        public Owner(String login) {

            setLogin(login);
        }

        private String login;

        public String getLogin() {

            return login;
        }

        public void setLogin(String login) {

            this.login = login;
        }

    }
}

