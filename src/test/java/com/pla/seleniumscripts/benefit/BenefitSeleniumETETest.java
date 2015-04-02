/*
 * Copyright (c) 3/31/15 12:01 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.seleniumscripts.benefit;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * @author: Samir
 * @since 1.0 31/03/2015
 */
@Ignore
public class BenefitSeleniumETETest {

    private String baseUrl;
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new HtmlUnitDriver();
        baseUrl = "http://5.9.249.195:6443/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl + "pla/login");
        driver.findElement(By.xpath("//input[@name='username']")).clear();
        driver.findElement(By.xpath("//input[@name='username']")).sendKeys("admin");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.xpath("html/body/div[1]/div/div/div/div[2]/form/button")).click();
    }


    @Test
    public void createBenefit() {
;
        //Core -> Benefits
        driver.findElement(By.cssSelector("span.caret")).click();
        driver.findElement(By.xpath("//a[contains(@href, '/pla/core/benefit/listbenefit')]")).click();
        //Create Benefit
        driver.findElement(By.xpath("(//button[@type='button'])[8]")).click();
        driver.findElement(By.xpath("//input[@id='benefitName']")).clear();
        driver.findElement(By.xpath("//input[@id='benefitName']")).sendKeys("Emergency Ambulance");
        driver.findElement(By.xpath("//button[@id='createUpdate']")).click();
        driver.findElement(By.xpath("//button[@id='cancel-button']")).click();
    }

    @Test
    public void updateBenefit() {


        //Core -> Benefits
        driver.findElement(By.cssSelector("span.caret")).click();
        driver.findElement(By.xpath("//a[contains(@href, '/pla/core/benefit/listbenefit')]")).click();

        driver.findElement(By.xpath("(//button[@type='button'])[9]")).click();
        driver.findElement(By.xpath("//input[@id='benefitName']")).clear();
        driver.findElement(By.xpath("//input[@id='benefitName']")).sendKeys("All Emergency123 Benefit");
        driver.findElement(By.xpath("//button[@id='createUpdate']")).click();
        driver.findElement(By.xpath("//button[@id='cancel-button']")).click();
    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}
