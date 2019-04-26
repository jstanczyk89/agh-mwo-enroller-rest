package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@Autowired
	MeetingService participantService;
	
	//Tested + some "Clean Code" names  

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getEveryMeeting() {
		Collection<Meeting> everyMeeting = meetingService.getEveryMeeting();
		return new ResponseEntity<Collection<Meeting>>(everyMeeting, HttpStatus.OK);
	}
	
	//Tested + some "Clean Code" names

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getOneMeeting(@PathVariable("id") long id) {
		Meeting oneMeeting = meetingService.findMeetingById(id);
		if (oneMeeting == null) {
			return new ResponseEntity("A meeting with specified id doesn't exist.",HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(oneMeeting, HttpStatus.OK);
	}
	
	//Tested + some "Clean Code" names

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting newMeeting) {
		Meeting foundMeeting = meetingService.findMeetingById(newMeeting.getId());
		if (foundMeeting != null) {
			return new ResponseEntity("Unable to create. A meeting with id " + newMeeting.getId() + " already exist.",
					HttpStatus.CONFLICT);
		}
		meetingService.addNewMeeting(newMeeting);
		return new ResponseEntity<Meeting>(newMeeting, HttpStatus.CREATED);
	}
	
	//Tested + some "Clean Code" names

	@RequestMapping(value = "/{id}/participants/{participantLogin}", method = RequestMethod.PUT)
	public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id,
			@PathVariable("participantLogin") String login) {
		Meeting existingMeeting = meetingService.findMeetingById(id);
		Participant newParticipantInTheMeeting = meetingService.findParticipantByLogin(login); // znalezć przyczynę czemu nie importuje z
																			// participantService :( - no idea why
		if (existingMeeting == null) {
			return new ResponseEntity("Unable to update. A meeting with specified id doesn't exist.",
					HttpStatus.NOT_FOUND);
		} else if (newParticipantInTheMeeting == null) {
			return new ResponseEntity("Unable to update. A participant with specified login doesn't exist.",
					HttpStatus.NOT_FOUND);
		} else {
			meetingService.addParticipantToMeeting(id, newParticipantInTheMeeting);
			return new ResponseEntity<Meeting>(existingMeeting, HttpStatus.OK);
		}

	}
	
	//Tested + some "Clean Code" names

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting wantedMeeting = meetingService.findMeetingById(id);
		if (wantedMeeting == null) {
			return new ResponseEntity("Unable to find. A meeting with specified id doesn't exist.",
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Participant>>(wantedMeeting.getAllParticipants(), HttpStatus.OK);
	}
	
	// Need testing

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findMeetingById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to find. A meeting with id " + meeting.getId() + " doesn't exist.", HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
	}
	
	// Need testing
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updateMeeting) {
			 Meeting meeting = meetingService.findMeetingById(id);
		     if (meeting == null) {
		         return new ResponseEntity("Unable to find. A meeting with id " + meeting.getId() + " doesn't exist.", HttpStatus.NOT_FOUND);
		     }
		     meeting.setId(updateMeeting.getId());
		     participantService.updateMeeting(meeting);
		     return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
	
	// Need testing
	
	@RequestMapping(value = "/{id}/participants/{participantLogin}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("id") long id,
			@PathVariable("participantLogin") String login) {
		Meeting meeting = meetingService.findMeetingById(id);
		Participant participant = participantService.findParticipantByLogin(login); 
		if (meeting == null) {
			return new ResponseEntity("Unable to update. A meeting with id " + id + " does not exist.",
					HttpStatus.NOT_FOUND);
		} else if (participant == null) {
			return new ResponseEntity("Unable to update. A participant with login " + login + " does not exist.",
					HttpStatus.NOT_FOUND);
		} else {
			meetingService.deleteParticipantFromMeeting(id, participant);
			return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
		}

	}
}
