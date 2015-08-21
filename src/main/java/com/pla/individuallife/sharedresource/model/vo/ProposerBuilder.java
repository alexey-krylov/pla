package com.pla.individuallife.sharedresource.model.vo;

import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

public class ProposerBuilder {
    private String title;
    private String firstName;
    private String surname;
    private String nrc;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime employmentDate;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateOfBirth;
    private Gender gender;
    private String mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseEmailAddress;
    private String spouseMobileNumber;
    private EmploymentDetail employmentDetail;
    private ResidentialAddress residentialAddress;
    private String otherName;
    private boolean isProposedAssured;
    private String clientId;

    public ProposerBuilder withOtherName(String otherName)
    {
        this.otherName=otherName;
        return this;
    }

    public ProposerBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProposerBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ProposerBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public ProposerBuilder withNrc(String nrc) {
        this.nrc = nrc;
        return this;
    }

    public ProposerBuilder withIsProposedAssured(boolean isProposedAssured){
        this.isProposedAssured  = isProposedAssured;
        return this;
    }
    public ProposerBuilder withDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProposerBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ProposerBuilder withMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public ProposerBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public ProposerBuilder withMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public ProposerBuilder withSpouseFirstName(String spouseFirstName) {
        this.spouseFirstName = spouseFirstName;
        return this;
    }

    public ProposerBuilder withSpouseLastName(String spouseLastName) {
        this.spouseLastName = spouseLastName;
        return this;
    }

    public ProposerBuilder withSpouseMobileNumber(String mobileNumber) {
        this.spouseMobileNumber = mobileNumber;
        return this;
    }

    public ProposerBuilder withSpouseEmailAddress(String spouseEmailAddress) {
        this.spouseEmailAddress = spouseEmailAddress;
        return this;
    }

    public ProposerBuilder withEmploymentDetail(EmploymentDetail employmentDetail) {
        this.employmentDetail = employmentDetail;
        return this;
    }

    public ProposerBuilder withResidentialAddress(ResidentialAddress residentialAddress) {
        this.residentialAddress = residentialAddress;
        return this;
    }

    public ProposerBuilder withClientId(String clientId){
        this.clientId = clientId;
        return this;
    }

    public Proposer createProposer() {
        return new Proposer(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, spouseMobileNumber, employmentDetail, residentialAddress,isProposedAssured,otherName,clientId);
    }

    public ProposerDto createProposerDto() {
        return new ProposerDto(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, spouseMobileNumber, employmentDetail, residentialAddress,isProposedAssured,otherName,clientId);
    }

    public static ProposerBuilder getProposerBuilder(ProposerDto dto) {

        ProposerBuilder builder = new ProposerBuilder();
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
                .withClientId(dto.getClientId())
                .withIsProposedAssured(dto.getIsProposedAssured())
                .withEmploymentDetail(new EmploymentDetailBuilder()
                        .withEmploymentDate(dto.getEmployment().getEmploymentDate())
                        .withEmploymentTypeId(dto.getEmployment().getEmploymentType())
                        .withEmployer(dto.getEmployment().getEmployer())
                        .withWorkPhone(dto.getEmployment().getWorkPhone())
                        .withAddress(new AddressBuilder()
                                .withAddress1(dto.getEmployment().getAddress1())
                                .withAddress2(dto.getEmployment().getAddress2())
                                .withProvince(dto.getEmployment().getProvince())
                                .withPostalCode(dto.getEmployment().getPostalCode())
                                .withTown(dto.getEmployment().getTown()).createAddress())
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

    public static ProposerBuilder getProposerBuilder(Proposer proposer) {

        ProposerBuilder builder = new ProposerBuilder();
        builder.withOtherName(proposer.getOtherName())
                .withDateOfBirth(proposer.getDateOfBirth())
                .withEmailAddress(proposer.getEmailAddress())
                .withFirstName(proposer.getFirstName())
                .withSurname(proposer.getSurname())
                .withTitle(proposer.getTitle())
                .withDateOfBirth(proposer.getDateOfBirth())
                .withGender(proposer.getGender())
                .withMobileNumber(proposer.getMobileNumber())
                .withMaritalStatus(proposer.getMaritalStatus())
                .withNrc(proposer.getNrc())
                .withClientId(proposer.getClientId())
                .withIsProposedAssured(proposer.getIsProposedAssured());
        if(proposer.getEmploymentDetail() != null && proposer.getResidentialAddress() != null) {
            builder.withEmploymentDetail(new EmploymentDetailBuilder()
                    .withEmploymentDate(proposer.getEmploymentDetail().getEmploymentDate())
                    .withEmploymentTypeId(proposer.getEmploymentDetail().getEmploymentTypeId())
                    .withEmployer(proposer.getEmploymentDetail().getEmployer())
                    .withWorkPhone(proposer.getEmploymentDetail().getWorkPhone())
                    .withAddress(new AddressBuilder()
                            .withAddress1(proposer.getEmploymentDetail().getAddress().getAddress1())
                            .withAddress2(proposer.getEmploymentDetail().getAddress().getAddress2())
                            .withProvince(proposer.getEmploymentDetail().getAddress().getProvince())
                            .withPostalCode(proposer.getEmploymentDetail().getAddress().getPostalCode())
                            .withTown(proposer.getEmploymentDetail().getAddress().getTown()).createAddress())
                    .withOccupationClass(proposer.getEmploymentDetail().getOccupationClass()).createEmploymentDetail())
                    .withResidentialAddress(new ResidentialAddress(new AddressBuilder()
                            .withAddress1(proposer.getResidentialAddress().getAddress().getAddress1())
                            .withAddress2(proposer.getResidentialAddress().getAddress().getAddress2())
                            .withProvince(proposer.getResidentialAddress().getAddress().getProvince())
                            .withPostalCode(proposer.getResidentialAddress().getAddress().getPostalCode())
                            .withTown(proposer.getResidentialAddress().getAddress().getTown()).createAddress(),
                            proposer.getResidentialAddress().getHomePhone(),
                            proposer.getResidentialAddress().getEmailAddress()));
        }
        if(proposer.getMaritalStatus() != null && proposer.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            builder.withSpouseEmailAddress(proposer.getSpouseEmailAddress())
                    .withSpouseFirstName(proposer.getSpouseFirstName())
                    .withSpouseMobileNumber(proposer.getSpouseMobileNumber())
                    .withSpouseLastName(proposer.getSpouseLastName());
        }

        return builder;

    }
}
