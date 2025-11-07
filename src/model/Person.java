package model;
public abstract class Person {
    protected String id;
    protected String fullName;
    protected String phone;
    protected String address;

    public Person() {}

    public Person(String id, String fullName, String phone, String address) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // Getter/Setter tiêu chuẩn
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }


    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public abstract void displayInfo();
}