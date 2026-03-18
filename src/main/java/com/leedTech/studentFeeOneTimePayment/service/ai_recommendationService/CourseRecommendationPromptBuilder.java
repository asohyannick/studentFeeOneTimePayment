package com.leedTech.studentFeeOneTimePayment.service.ai_recommendationService;

import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseRecommendationPromptBuilder {

public String build( StudentProfile profile,
                     List < Course > availableCourses,
                     int numberOfRecommendations,
                     String focus) {
	
	User student = profile.getStudent();
	
	String studentSection = String.format("""
                STUDENT PROFILE:
                - Name             : %s %s
                - Student Number   : %s
                - Current Class    : %s
                - Academic Year    : %s
                - GPA              : %s
                - Enrollment Status: %s
                - Native Language  : %s
                - Hobbies          : %s
                - Extracurricular  : %s
                - Special Needs    : %s
                - Focus Area       : %s
                """,
			student.getFirstName(),
			student.getLastName(),
			profile.getStudentNumber(),
			profile.getCurrentClass()              != null ? profile.getCurrentClass()              : "N/A",
			profile.getAcademicYear()              != null ? profile.getAcademicYear()              : "N/A",
			profile.getGpa()                       != null ? profile.getGpa().toString()            : "N/A",
			profile.getEnrollmentStatus()          != null ? profile.getEnrollmentStatus().name()   : "N/A",
			profile.getNativeLanguage()            != null ? profile.getNativeLanguage()            : "N/A",
			profile.getHobbies()                   != null ? profile.getHobbies()                   : "N/A",
			profile.getExtracurricularActivities() != null ? profile.getExtracurricularActivities() : "N/A",
			profile.getSpecialNeeds()              != null ? profile.getSpecialNeeds()              : "None",
			focus                                  != null ? focus                                  : "balanced learning"
	);
	
	String courseSection = availableCourses.stream()
			                       .map(c -> String.format(
					                       "- ID: %s | Code: %s | Name: %s | Dept: %s | Level: %s | " +
							                       "Credits: %d | Semester: %s | Mandatory: %s | Elective: %s | " +
							                       "Prerequisites: %s | Objectives: %s",
					                       c.getId(),
					                       c.getCourseCode(),
					                       c.getCourseName(),
					                       c.getDepartment(),
					                       c.getCourseLevel()       != null ? c.getCourseLevel()       : "N/A",
					                       c.getCreditHours(),
					                       c.getSemester()          != null ? c.getSemester()          : "N/A",
					                       c.isMandatory(),
					                       c.isElective(),
					                       c.getPrerequisites()     != null ? c.getPrerequisites()     : "None",
					                       c.getCourseObjectives()  != null ? c.getCourseObjectives()  : "N/A"
			                       ))
			                       .collect( Collectors.joining("\n"));
	
	return String.format("""
                You are an expert academic advisor for a university.
                Recommend the most suitable courses for the student below.
                
                %s
                
                AVAILABLE COURSES (%d total):
                %s
                
                INSTRUCTIONS:
                1. Recommend exactly %d courses from the list above.
                2. NEVER recommend the same course twice — each recommendation must be a DIFFERENT course.
                3. If there are fewer available courses than requested, recommend only what is available.
                4. Prioritize mandatory courses the student should take first.
                5. Consider the student's GPA, class level, language, hobbies, and focus area.
                6. Avoid courses whose prerequisites the student has likely not met.
                7. Assign each course a matchScore from 1 (poor fit) to 10 (perfect fit).
                
                Respond ONLY with valid JSON — no markdown, no explanation outside the JSON.
                Use this EXACT structure:
                {
                  "recommendations": [
                    {
                      "courseId": "<UUID string>",
                      "courseCode": "<code>",
                      "courseName": "<name>",
                      "department": "<dept>",
                      "reason": "<personalized reason for this student>",
                      "matchScore": <1-10>
                    }
                  ],
                  "overallReasoning": "<brief strategy summary>"
                }
                """,
			studentSection,
			availableCourses.size(),
			courseSection,
			numberOfRecommendations
	);
}
}