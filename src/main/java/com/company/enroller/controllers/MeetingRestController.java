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

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
		Meeting foundMeeting = meetingService.findById(meeting.getId());
		if (foundMeeting != null) {
			return new ResponseEntity("Unable to create. A meeting with id " + meeting.getId() + " already exist.",
					HttpStatus.CONFLICT);
		}
		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}/participants/{participantLogin}", method = RequestMethod.PUT)
	public ResponseEntity<?> addParticipant(@PathVariable("id") long id,
			@PathVariable("participantLogin") String login) {
		Meeting meeting = meetingService.findById(id);
		Participant participant = participantService.findByLogin(login); // znalezć przyczynę czemu nie importuje z
																			// participantService ^|_O_|^
		if (meeting == null) {
			return new ResponseEntity("Unable to update. A meeting with id " + id + " does not exist.",
					HttpStatus.NOT_FOUND);
		} else if (participant == null) {
			return new ResponseEntity("Unable to update. A participant with login " + login + " does not exist.",
					HttpStatus.NOT_FOUND);
		} else {
			meetingService.addParticipant(id, participant);
			return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/{id}/participant", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to find. A meeting with id " + meeting.getId() + " doesn't exist.",
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to find. A meeting with id " + meeting.getId() + " doesn't exist.", HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updateMeeting) {
			 Meeting meeting = meetingService.findById(id);
		     if (meeting == null) {
		         return new ResponseEntity("Unable to find. A meeting with id " + meeting.getId() + " doesn't exist.", HttpStatus.NOT_FOUND);
		     }
		     meeting.setId(updateMeeting.getId());
		     participantService.updateMeeting(meeting);
		     return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/participants/{participantLogin}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("id") long id,
			@PathVariable("participantLogin") String login) {
		Meeting meeting = meetingService.findById(id);
		Participant participant = participantService.findByLogin(login); 
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