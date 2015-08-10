package com.pla.individuallife.sharedresource.model.vo;

import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.joda.time.DateTime;

public class ProposedAssuredBuilder {
    private String title;
    private String firstName;
    private String surname;
    private String nrc;
    private DateTime dateOfBirth;
    private Gender gender;
    private String mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseEmailAddress;
    private String spouseMobileNumber;
    private ResidentialAddress residentialAddress;
    private EmploymentDetail employmentDetail;
    private String otherName;
    private String relationShipId;

    public ProposedAssuredBuilder withOtherName(String otherName)
    {
        this.otherName=otherName;
        return this;
    }

    public ProposedAssuredBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProposedAssuredBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ProposedAssuredBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }


    public ProposedAssuredBuilder withNrc(String nrc) {
        this.nrc = nrc;
        return this;
    }

    public ProposedAssuredBuilder withDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProposedAssuredBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ProposedAssuredBuilder withMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public ProposedAssuredBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public ProposedAssuredBuilder withMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public ProposedAssuredBuilder withSpouseFirstName(String spouseFirstName) {
        this.spouseFirstName = spouseFirstName;
        return this;
    }

    public ProposedAssuredBuilder withSpouseLastName(String spouseLastName) {
        this.spouseLastName = spouseLastName;
        return this;
    }

    public ProposedAssuredBuilder withSpouseEmailAddress(String spouseEmailAddress) {
        this.spouseEmailAddress = spouseEmailAddress;
        return this;
    }

    public ProposedAssuredBuilder withSpouseMobileNumber(String mobileNumber) {
        this.spouseMobileNumber = mobileNumber;
        return this;
    }

    public ProposedAssuredBuilder withEmploymentDetail(EmploymentDetail employmentDetail) {
        this.employmentDetail = employmentDetail;
        return this;
    }

    public ProposedAssuredBuilder withResidentialAddress(ResidentialAddress residentialAddress) {
        this.residentialAddress = residentialAddress;
        return this;
    }

    public ProposedAssuredBuilder withRelationShipId(String relationShipId){
        this.relationShipId = relationShipId;
        return this;
    }

    public ProposedAssured createProposedAssured() {
        return new ProposedAssured(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, spouseMobileNumber, employmentDetail, residentialAddress,otherName);
    }

    public ProposedAssuredDto createProposedAssuredDto() {
        return new ProposedAssuredDto(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, spouseMobileNumber, employmentDetail, residentialAddress,otherName,relationShipId);
    }

    public static ProposedAssuredBuilder getProposedAssuredBuilder(ProposedAssuredDto dto) {
        ProposedAssuredBuilder builder = new ProposedAssuredBuilder();
        builder.withOtherName(dto.getOtherName())
                .withDateOfBirth(dto.getDateOfBirth())
                .withEmailAddress(dto.getEmailAddress())
                .withFirstName(dto.getFirstName())
                .withSurname(dto.getSurname())
                .withTitle(dto.getTitle())
                .withDateOfBirth(dto.getDateOfBirth())
                .withGender(dto.getGender())
                .withMobileNumber(dto.getMobileNumber())
                .withMaritalStatus(dto.getMaritalStatus())
                .withNrc(dto.getNrc())
                .withRelationShipId(dto.getRelationshipId())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmploymentDate(dto.getEmployment().getEmploymentDate())
                        .withEmploymentTypeId(dto.getEmployment().getEmploymentType())
                        .withEmployer(dto.getEmployment().getEmployer())
                        .withWorkPhone(dto.getEmployment().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(dto.getEmployment().getAddress1())
                                .withAddress2(dto.getEmployment().getAddress2())
                                .withProvince(dto.getEmployment().getProvince())
                                .withTown(dto.getEmployment().getTown())
                                .withPostalCode(dto.getEmployment().getPostalCode()).createAddress())
                        .withOccupationClass(dto.getEmployment().getOccupation()).createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                        .withAddress1(dto.getResidentialAddress().getAddress1())
                        .withAddress2(dto.getResidentialAddress().getAddress2())
                        .withProvince(dto.getResidentialAddress().getProvince())
                        .withPostalCode(dto.getResidentialAddress().getPostalCode())
                        .withTown(dto.getResidentialAddress().getTown()).createAddress(),
                        dto.getResidentialAddress().getHomePhone(),
                        dto.getResidentialAddress().getEmailAddress()));
        if(dto.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            builder.withSpouseEmailAddress(dto.getSpouse().getEmailAddress())
                    .withSpouseFirstName(dto.getSpouse().getFirstName())
                    .withSpouseMobileNumber(dto.getSpouse().getMobileNumber())
                    .withSpouseLastName(dto.getSpouse().getSurname());
        }

        return builder;
    }

    public static ProposedAssuredBuilder getProposedAssuredBuilder(ProposedAssured pa) {
        ProposedAssuredBuilder builder = new ProposedAssuredBuilder();
        builder.withOtherName(pa.getOtherName())
                .withDateOfBirth(pa.getDateOfBirth())
                .withEmailAddress(pa.getEmailAddress())
                .withFirstName(pa.getFirstName())
                .withSurname(pa.getSurname())
                .withTitle(pa.getTitle())
                .withDateOfBirth(pa.getDateOfBirth())
                .withGender(pa.getGender())
                .withMobileNumber(pa.getMobileNumber())
                .withMaritalStatus(pa.getMaritalStatus())
                .withNrc(pa.getNrc())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmploymentDate(pa.getEmploymentDetail().getEmploymentDate())
                        .withEmploymentTypeId(pa.getEmploymentDetail().getEmploymentTypeId())
                        .withEmployer(pa.getEmploymentDetail().getEmployer())
                        .withWorkPhone(pa.getEmploymentDetail().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(pa.getEmploymentDetail().getAddress().getAddress1())
                                .withAddress2(pa.getEmploymentDetail().getAddress().getAddress2())
                                .withProvince(pa.getEmploymentDetail().getAddress().getProvince())
                                .withPostalCode(pa.getEmploymentDetail().getAddress().getPostalCode())
                                .withTown(pa.getEmploymentDetail().getAddress().getTown()).createAddress())
                        .withOccupationClass(pa.getEmploymentDetail().getOccupationClass()).createEmploymentDetail())
                .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                        .withAddress1(pa.getResidentialAddress().getAddress().getAddress1())
                        .withAddress2(pa.getResidentialAddress().getAddress().getAddress2())
                        .withProvince(pa.getResidentialAddress().getAddress().getProvince())
                        .withPostalCode(pa.getResidentialAddress().getAddress().getPostalCode())
                        .withTown(pa.getResidentialAddress().getAddress().getTown()).createAddress(),
                        pa.getResidentialAddress().getHomePhone(),
                        pa.getResidentialAddress().getEmailAddress()));
        if(pa.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            builder.withSpouseEmailAddress(pa.getSpouseEmailAddress())
                    .withSpouseFirstName(pa.getSpouseFirstName())
                    .withSpouseMobileNumber(pa.getSpouseMobileNumber())
                    .withSpouseLastName(pa.getSpouseLastName());
        }

        return builder;
    }

}
