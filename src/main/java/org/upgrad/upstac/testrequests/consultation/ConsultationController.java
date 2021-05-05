package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);




    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @Autowired
    TestRequestFlowService  testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;


    //Implement this method to get the list of test requests having status as 'LAB_TEST_COMPLETED'
    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations()  {


        return testRequestQueryService.findBy(RequestStatus.COMPLETED);  // Implement this method

    }

    //Implement this method to return the list of test requests assigned to current doctor
    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor()  {

        //Implement this method

        User doctor = userLoggedInService.getLoggedInUser();   //returns current logged in doctor
        return testRequestQueryService.findByDoctor(doctor);   //returns tests assigned to logged in doctor


    }


    //Implement this method to assign a particular test request to current doctor(logged in user)
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        // Implement this method
        try {
        User doctor= userLoggedInService.getLoggedInUser();   //returns current logged in doctor
        return testRequestUpdateService.assignForConsultation(id,doctor);  //assigns particular test requests to logged in doctor

        }
        catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    // Implement this method to update the result of the current test request id with test doctor comments
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id,@RequestBody CreateConsultationRequest testResult) {

        // Implement this method
        try {
        User doctor=userLoggedInService.getLoggedInUser();   //returns current logged in doctor
        return testRequestUpdateService.updateConsultation(id,testResult,doctor);  //update test request id with doctor comments


        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



}
