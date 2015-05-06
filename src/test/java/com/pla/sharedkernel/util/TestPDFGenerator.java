package com.pla.sharedkernel.util;

import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 5/5/2015.
 */
public class TestPDFGenerator {

    public static void main(String[] args) throws IOException, JRException {
        byte[] data = PDFGeneratorUtils.createPDFReportByList(getDataBeanList(), "jasperpdf/template/grouplife/glQuotation.jrxml");
        File file = new File("glquotation.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static ArrayList<AgentBean> getDataBeanList() {
        ArrayList<AgentBean> dataBeanList = new ArrayList<AgentBean>();
        List<CoverDetail> coverDetails = new ArrayList<CoverDetail>();
        List<Annexure> annexures = new ArrayList<Annexure>();
        CoverDetail coverDetail = new CoverDetail();
        coverDetail.setPlanCoverageName("SSS");
        coverDetail.setPlanCoverageSumAssured("350000");
        coverDetail.setCategory("category1");
        coverDetail.setRelationship("relationshipt1");
        for (int i = 0; i < 5; i++) {
            coverDetails.add(coverDetail);
        }
        Annexure annexure = new Annexure();
        annexure.setAge("45");
        annexure.setAnnualIncome("34000");
        annexure.setCategory("Category");
        annexure.setBasicPremium("2343");
        annexure.setDob("12/04/2015");
        annexure.setNrc("234443344");
        annexure.setSex("F");
        annexure.setInsuredName("Ram");
        annexure.setStatus("Singel");
        for (int i = 0; i < 5; i++) {
            annexures.add(annexure);
        }
        dataBeanList.add(produce("Nischitha", "Kurunji", "Miss", "1221kejw", "963210317", "Living Stone A", "847 A Block,Sahakar Nagar,1st main,Hebbal Road,Bangalore", "AWER#$44", "12/04/2015", "23/7/2020", "24", "4500", "Plan Name"
                , "Net Premium", "Add on Benefits", "weigher of excess", "profit and solvency", "additional doscount ladoing", "65", "4500", coverDetails, annexures));
        return dataBeanList;
    }

    /**
     * This method returns a DataBean object,
     * with name and country set in it.
     */
    private static AgentBean produce(String firstName, String lastName, String agentSalutation, String agentCode, String agentMobileNumber, String agentBranch, String proposerAdddress, String quotationNumber, String fromDate, String toDate, String totalLivesCovered, String totalSumInsured,
                              String planName, String netPremium, String addOnBenefits, String weigherOfExcess, String profitAndSolvency, String additionalDiscountLoading, String serviceTax, String totalPremium, List<CoverDetail> coverDetails, List<Annexure> annexures) {
        AgentBean agentBean = new AgentBean();
        agentBean.setProposerName("Nth Dimenzion");
        agentBean.setProposerPhoneNumber("0802574500");
        agentBean.setAgentName(firstName + " " + lastName);
        agentBean.setAgentSalutation(agentSalutation);
        agentBean.setAgentBranch(agentBranch);
        agentBean.setAgentMobileNumber(agentMobileNumber);
        agentBean.setAgentCode(agentCode);
        agentBean.setCoverDetails(coverDetails);
        agentBean.setAnnexure(annexures);
        agentBean.setProposerAddress(proposerAdddress);
        agentBean.setQuotationNumber(quotationNumber);
        agentBean.setCoveragePeriod(fromDate + "-" + toDate);
        agentBean.setLivesCovered(totalLivesCovered);
        agentBean.setSumAssured(totalSumInsured);
        agentBean.setPlanName(planName);
        agentBean.setNetPremium(netPremium);
        agentBean.setAddOnBenefits(addOnBenefits);
        agentBean.setWaiverOfExcessLoadings(weigherOfExcess);
        agentBean.setProfitAndSolvencyLoading(profitAndSolvency);
        agentBean.setAdditionalDiscountLoading(additionalDiscountLoading);
        agentBean.setServiceTax(serviceTax);
        agentBean.setTotalPremium(totalPremium);
        agentBean.setSpecialConditions("sdsdsdsds");
        agentBean.setAddOnBenefitsPercentage("20%");
        agentBean.setWaiverOfExcessLoadingsPercentage("10%");
        return agentBean;
    }
}
