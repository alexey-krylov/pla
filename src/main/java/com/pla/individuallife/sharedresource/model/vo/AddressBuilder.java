package com.pla.individuallife.sharedresource.model.vo;

public class AddressBuilder {
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;

    public AddressBuilder withAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public AddressBuilder withAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public AddressBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressBuilder withProvince(String province) {
        this.province = province;
        return this;
    }

    public AddressBuilder withTown(String town) {
        this.town = town;
        return this;
    }


    public Address createAddress() {
        return new Address(address1, address2, postalCode, province, town);
    }
}