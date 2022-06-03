package com.redhat.vcs_kjar.drools;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "html:target/cucumber_html/vaccinationCardNextStepValidation.html",
                            "json:target/json-vaccinationCardNextStepValidation.json",
                            "junit:target/junit-vaccinationCardNextStepValidation.xml",
                            "pretty",
                            "summary"},
                    glue = {"com.redhat.vcs_kjar.drools"},
                    features = {"src/test/resources/features/vaccinationCardNextStep.feature"},
                    tags = "@vaccinationCardNextStep")
public class VaccinationCardNextStepTest {
}