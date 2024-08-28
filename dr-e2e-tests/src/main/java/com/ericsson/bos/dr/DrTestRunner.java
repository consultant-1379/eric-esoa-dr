/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.bos.dr;

import org.testng.TestNG;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Runner Class Used to execute TestNG tests
 */
public class DrTestRunner {
    /**
     * @param args Program Arguments
     */
    public static void main(String[] args) {
        final TestNG testNG = new TestNG();

        final XmlSuite suite = new XmlSuite();
        suite.setName("DR Tests");

        final List<XmlPackage> packages = new ArrayList<>();
        packages.add(new XmlPackage("com.ericsson.bos.dr.tests"));

        final XmlTest test = new XmlTest(suite);
        test.setName("DR Test");
        test.setXmlPackages(packages);

        final List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);

        testNG.setXmlSuites(suites);
        testNG.run();

        System.exit(0);
    }
}
