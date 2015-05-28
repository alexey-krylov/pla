/**
 * Created by pradyumna on 24-05-2015.
 */

angular.module('pla.individual.proposal', [])
    .factory('ValidationError', function () {
        function ValidationError(message, fieldName) {
            this.name = 'Validation Error';
            this.message = message || 'Default Message';
            this.fieldName = fieldName;
        };
        return ValidationError;
    })
    .factory('Spouse', ['ValidationError', function (ValidationError) {
        function Spouse(firstName, surname, mobileNumber, emailAddress) {
            if (!firstName) throw new ValidationError("Please enter Spouse FirstName.", 'spouseFirstName')
            this.firstName = firstName;
            if (!surname) throw new ValidationError("Please enter Spouse Surname.", "spouseSurname")
            this.surname = surname;
            if (!mobileNumber) throw new ValidationError("Please enter Spouse Mobile Number.", "spouseMobileNumber")
            this.mobileNumber = mobileNumber;
            this.emailAddress = emailAddress;
        };
        Spouse.build = function (data) {
            return new Spouse(
                data.firstName,
                data.surname,
                data.mobileNumber,
                data.emailAddress
            );
        };
        return Spouse;
    }])
    .factory('ProposedAssured', ['ValidationError', 'Employment', 'ResidentialAddress', 'Spouse', function (ValidationError, Employment, ResidentialAddress, Spouse) {
        function ProposedAssured(title, firstName, surname, otherName, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus) {
            if (!title) throw new ValidationError("Please enter Title.", 'title')
            this.title = title;
            if (!firstName) throw new ValidationError("Please enter FirstName.", 'firstName')
            this.firstName = firstName;
            if (!surname) throw new ValidationError("Please enter Surname.", 'surname')
            this.surname = surname;
            this.otherName = otherName || "";
            if (!nrc) throw new ValidationError("Please enter NRC.", 'nrc')
            this.nrc = nrc;
            if (!dateOfBirth) throw new ValidationError("Please enter Date of Birth.", 'dateOfBirth')
            this.dateOfBirth = dateOfBirth;
            if (!gender) throw new ValidationError("Please enter Sex.", 'gender')
            this.gender = gender;
            if (!mobileNumber) throw new ValidationError("Please enter Mobile Number.", 'mobileNumber')
            this.mobileNumber = mobileNumber;
            this.emailAddress = emailAddress || "";
            if (!maritalStatus) throw new ValidationError("Please enter Marital Status.", 'maritalStatus')
            this.maritalStatus = maritalStatus;
        };
        ProposedAssured.build = function (data) {
            return new ProposedAssured(
                data.title,
                data.firstName,
                data.surname,
                data.otherName,
                data.nrc,
                data.dateOfBirth,
                data.gender,
                data.mobileNumber,
                data.emailAddress,
                data.maritalStatus
            );
        }
        ProposedAssured.prototype.constructor = ProposedAssured;

        ProposedAssured.prototype.setEmployment = function (data) {
            this.employment = Employment.build(data);
        };

        ProposedAssured.prototype.setResidentialAddress = function (data) {
            this.residentialAddress = ResidentialAddress.build(data);
        };

        ProposedAssured.prototype.setSpouse = function (data) {
            this.spouse = Spouse.build(data);
        };
        return ProposedAssured;
    }])
    .factory('Proposer', function () {
        function Proposer(title, firstName, surname, otherName, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseDetail) {
            ProposedAssured.call(this, title, firstName, surname, otherName, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseDetail);
        };
        Proposer.prototype = Object.create(ProposedAssured.prototype);
        Proposer.prototype.constructor = Proposer;
        return Proposer;
    })
    .factory('Employment', function (ValidationError) {
        function Employment(occupation, employer, employmentDate, employmentType, address1, address2, postalCode, province, town, workphone) {
            if (!occupation) throw new ValidationError("Please enter Occupation.", 'occupation');
            this.occupation = occupation;
            if (!employer) throw new ValidationError("Please enter Employer.", 'employer');
            this.employer = employer;
            if (!employmentDate) throw new ValidationError("Please enter Employment Date.", 'employmentDate');
            this.employmentDate = employmentDate;
            if (!employmentType) throw new ValidationError("Please enter Employment Type.", 'employmentType');
            this.employmentType = employmentType;
            if (!address1) throw new ValidationError("Please enter Address Line 1.", 'address1');
            this.address1 = address1;
            this.address2 = address2;
            this.postalCode = postalCode;
            if (!province) throw new ValidationError("Please enter Province.", 'postalCode');
            this.province = province;
            if (!town) throw new ValidationError("Please enter Town.", 'town');
            this.town = town;
            this.workphone = workphone;
        };
        Employment.build = function (data) {
            return new Employment(data.occupation,
                data.employer, data.employmentDate, data.employmentType, data.address1, data.address2, data.postalCode, data.province, data.town, data.workphone);
        }
        return Employment;
    })
    .factory('ResidentialAddress', function (ValidationError) {
        function ResidentialAddress(address1, address2, postalCode, province, town, homePhone, emailAddress) {
            if (!address1) throw new ValidationError("Please enter Address Line 1.", 'address1');
            this.address1 = address1;
            this.address2 = address2;
            this.postalCode = postalCode;
            if (!province) throw new ValidationError("Please enter Province.", 'postalCode');
            this.province = province;
            if (!town) throw new ValidationError("Please enter Town.", 'town');
            this.town = town;
            this.homePhone = homePhone;
            this.emailAddress = emailAddress;
        };
        ResidentialAddress.build = function (data) {
            return new ResidentialAddress(data.address1, data.address2, data.postalCode, data.province, data.town, data.homePhone, data.emailAddress);
        }
        return ResidentialAddress;
    })
    .factory('Proposer', function () {
        function PlanDetail(planId, minEntryAge, maxEntryAge) {
            this.minEntryAge = minEntryAge;
            this.maxEntryAge = maxEntryAge;
            this.planId = planId;

        };
        PlanDetail.prototype.constructor = PlanDetail;
        return PlanDetail;
    })
    .factory('PolicyTerm', function () {
        function PolicyTerm(policyTermType, value, maxMaturityAge) {
            this.policyTermType = policyTermType;
            if (policyTermType == "SPECIFIED_VALUES") {
                this.term = value;
                this.maxMaturityAge = maxMaturityAge;
            } else if (policyTerm == "MATURITY_AGE_DEPENDENT") {
                this.maturityAge = value;
            }
        };
        PolicyTerm.prototype.constructor = PolicyTerm;
        return PolicyTerm;
    })
    .service('ProposalService', ['ProposedAssured', '$http', function (ProposedAssured, $http) {
        this.saveProposedAssured = function (proposedAssured, spouseDetail, employment, residentialAddress, proposedAssuredAsProposer, quotationId) {
            var proposedAssured = ProposedAssured.build(proposedAssured);
            proposedAssured.setResidentialAddress(residentialAddress);
            proposedAssured.setEmployment(employment);
            proposedAssured.setSpouse(spouseDetail);
            var proposer = null;
            console.log(' proposedAssuredAsProposer = ' + proposedAssuredAsProposer);
            if (proposedAssuredAsProposer) {
                proposer = angular.copy(proposedAssured);
            }
            $http.post('/pla/individuallife/proposal/saveProposedAssured', {"proposedAssured": proposedAssured, "proposer": proposer});
        }
    }]);

/*

 .factory('ProposalBuilder', function () {
 function ProposalBuilder() {
 var proposer;
 var proposedAssured;
 var planDetail;
 var policyTerm;
 var premiumPaymentTerm;
 var sumAssured;

 function calculateAgeNextBirthday() {
 var dateOfBirth = proposedAssured.dateOfBirth;
 var ageNextBirthDay = parseInt(moment().diff(moment(dateOfBirth), "years") + 1);
 console.log(ageNextBirthDay);
 return ageNextBirthDay;
 };
 function validateProposedAssuredAge() {
 if (!this.planDetail) return false;
 var minEntryAge = parseInt(this.planDetail.minEntryAge);
 var maxEntryAge = parseInt(this.planDetail.maxEntryAge);
 var ageNextBirthDay = calculateAgeNextBirthday();
 if (ageNextBirthDay < minEntryAge) {
 throw new ValidationError("Minimum Entry Age of Proposed Assured is " + this.planDetail.minEntryAge + " ")
 }
 if (ageNextBirthDay > maxEntryAge) {
 throw new ValidationError("Maximum Entry Age of Proposed Assured is " + this.planDetail.maxEntryAge + " ")
 }
 return true;
 };
 function validatePolicyTerm() {
 if (!policyTerm.policyTermType) return false;
 if (!proposedAssured.dateOfBirth)return false;
 var ageNextBirthDay = calculateAgeNextBirthday();
 if ("SPECIFIED_VALUES" == policyTerm.policyTermType) {
 var ageAtMaturity = ageNextBirthDay + policyTerm.term;
 console.log("Age at maturity" + ageAtMaturity);
 if (ageAtMaturity > policyTerm.maxMaturityAge) throw new ValidationError("Not a valid Policy Term. Age at maturity should be less than " + ageAtMaturity);
 } else {
 if (ageNextBirthDay > policyTerm.maturityAge) throw new ValidationError("Not a valid Policy Term.");
 }
 return true;
 };
 return {
 withProposedAssured: function (pa) {
 proposedAssured = pa;
 return this;
 },
 withProposer: function (p) {
 proposer = p;
 return this;
 },
 withPlanDetail: function (pd) {
 planDetail = pd;
 return this;
 },
 withPolicyTerm: function (pt) {
 policyTerm = pt;
 return this;
 },
 withPremiumPaymentTerm: function (ppt) {
 premiumPaymentTerm = ppt;
 return this;
 },
 withSumAssured: function (sa) {
 this.sumAssured = sa;
 },
 build: function () {
 console.log('Proposer ' + JSON.stringify(proposer));
 console.log('Proposed Assured ' + JSON.stringify(proposedAssured));
 console.log('Plan ' + JSON.stringify(planDetail));
 var isValidAge = validateProposedAssuredAge();
 validatePolicyTerm();
 }
 }
 };
 return ProposalBuilder;
 })


 var spouse = new Spouse('Shubhra', 'Mishra', '9341095288');
 var proposedAssured = new ProposedAssured('Mr.', 'Pradyumna', 'Mohapatra', 'Raja', 'AHRPM3387J', new Date('1938-02-17'), 'MALE', 9343044175, 'raja_navy@yahoo.com', 'MARRIED', spouse);
 var proposer = new Proposer('Mr.', 'Pradyumna', 'Mohapatra', 'Raja', 'AHRPM3387J', new Date('1978-02-17'), 'MALE', 9343044175, 'raja_navy@yahoo.com', 'MARRIED', spouse);
 var planDetail = new PlanDetail("kla", 21, 100);
 var policyTerm = new PolicyTerm("SPECIFIED_VALUES", 15, 50);
 var proposalBuilder = new ProposalBuilder();
 proposalBuilder.withProposedAssured(proposedAssured);
 proposalBuilder.withProposer(proposer);
 proposalBuilder.withPlanDetail(planDetail);
 proposalBuilder.withPolicyTerm(policyTerm);
 proposalBuilder.build();*/
/*
 proposal.setProposedAssured = proposedAssured;
 proposal.setPlanDetail = {minEntryAge: 21, maxEntryAge: 50};
 proposal.copyProposedAssuredToProposer();
 proposal.setPolicyTerm = {maxMaturityAge: 65, term: 15, policyTermType: "SPECIFIED_VALUES"};
 var isPolicyTermValid = proposal.validatePolicyTerm();
 var isValidEntryCriteria = proposal.validateProposedAssuredAge();
 console.log('isValidEntryCriteria ' + isValidEntryCriteria);
 console.log(' isPolicyTermValid by Value ' + isPolicyTermValid);
 proposal.setPolicyTerm = {maturityAge: 65, policyTermType: "MATURITY_AGE_DEPENDENT"};
 isPolicyTermValid = proposal.validatePolicyTerm();
 console.log(' isPolicyTermValid by Age ' + isPolicyTermValid);

 */
