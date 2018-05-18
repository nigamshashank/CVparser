package com.cv.parser.applicant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cv.parser.RegEx;
import com.cv.parser.entity.Applicant;
import com.cv.parser.entity.ApplicantDocument;
import com.cv.parser.helper.ParserHelper;

/**
 * This is for storing data in {@link} Applicant model;
 * 
 * @author RAYMARTHINKPAD
 *
 */
public class ParseApplicant {
    Logger logger = LoggerFactory.getLogger(ParseApplicant.class);

    List<ApplicantDocument> appDocList = new ArrayList<ApplicantDocument>();
    List<Applicant> applicants = new ArrayList<Applicant>();

    public ParseApplicant(List<ApplicantDocument> appDocList) {
	this.appDocList = appDocList;
    }

    private String findEmail(String details) {
	List<String> emailList = new ArrayList<String>();
	Pattern pattern = Pattern.compile(RegEx.EMAIL.toString(), Pattern.MULTILINE);
	Matcher matcher = pattern.matcher(details);
	while (matcher.find()) {
	    emailList.add(matcher.group());
	}
	return emailList.toString();
    }

    /**
     * a link without http://|https://|www is not considered a link i.e.
     * google.com (invalid)
     * 
     * @return
     */
    private String findLinks(String line) {
	List<String> links = new ArrayList<String>();
	Pattern pattern = Pattern.compile(RegEx.LINK.toString(),
		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	Matcher matcher = pattern.matcher(line);
	while (matcher.find()) {
	    links.add(matcher.group());
	}
	return links.toString();
    }

    /**
     * The introduction of an applicant may contains the name and the address
     * 
     * @return the profile containing name (most likely) and address (possibly)
     */
    private String findProfile(String line) {
	// copies everything up to the next section
	ParserHelper helper = new ParserHelper();
	List<Integer> indexes = helper.getIndexesOfSection(line);
	int beginIndex = 0;
	int endIndex = indexes.get(0);
	return line.substring(beginIndex, endIndex);
    }

    /**
     * Find phone numbers in the resume
     * 
     * @param line
     *            to search for
     * @return phone numbers found from resume
     */
    private String findPhoneNumber(String line) {
	List<String> phoneNumbers = new ArrayList<String>();
	Pattern pattern = Pattern.compile(RegEx.PHONE.toString(), Pattern.MULTILINE | Pattern.DOTALL);
	Matcher matcher = pattern.matcher(line);
	while (matcher.find()) {
	    phoneNumbers.add(matcher.group());
	}
	return phoneNumbers.toString();
    }

    private String findObjective(String line) {
	RegEx obj = RegEx.OBJECTIVE;
	ParserHelper helper = new ParserHelper();
	int beginIndex = helper.getIndexOfThisSection(obj, line);
	String objective = line.replaceFirst(RegEx.OBJECTIVE.toString(), ""); // section heading is removed
	int endIndex = helper.getIndexesOfSection(obj, objective).get(0);
	return objective.substring(beginIndex, endIndex);
    }

    public void applicantInfo() {
	for (ApplicantDocument ad : appDocList) {
	    Applicant applicant = new Applicant();
	    applicant.setPhoneNumber(findPhoneNumber(ad.getDetails()));
	    applicant.setEmail(findEmail(ad.getDetails()));
	    applicant.setLinks(findLinks(ad.getDetails()));
	    applicant.setProfile(findProfile(ad.getDetails()));
	    
	    // test if objective section exists in the first place
	    if (new ParserHelper().getIndexOfThisSection(RegEx.OBJECTIVE, ad.getDetails())  != -1) {
		applicant.setObjective(findObjective(ad.getDetails()));
	    } else {
		applicant.setObjective(null);
	    }
	    
	    this.applicants.add(applicant);
	}
    }

    public List<Applicant> getApplicants() {
	return applicants;
    }
}