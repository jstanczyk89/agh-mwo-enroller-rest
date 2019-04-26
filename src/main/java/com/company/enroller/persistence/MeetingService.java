package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getEveryMeeting() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findMeetingById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
	}

	public Meeting addNewMeeting(Meeting newMeeting) {
		Transaction transaction = (Transaction) connector.getSession().beginTransaction();
		connector.getSession().save(newMeeting);
		transaction.commit();
		return newMeeting;
	}

	public void addParticipantToMeeting(long id, Participant newParticipantInTheMeeting) {
		Meeting existingMeeting = findMeetingById(id);
		existingMeeting.addParticipant(newParticipantInTheMeeting);
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(existingMeeting);
		transaction.commit();
	}

	public Participant findParticipantByLogin(String login) {
		return (Participant) connector.getSession().get(Participant.class, login);
	}

	public void deleteThatMeeting(Meeting meetingForDeletion) {
		Transaction transaction = (Transaction) connector.getSession().beginTransaction();
		connector.getSession().delete(meetingForDeletion);
		transaction.commit();
	}

	public void updateMeeting(Meeting meetingForUpdate) {
		Transaction transaction = (Transaction) connector.getSession().beginTransaction();
		connector.getSession().merge(meetingForUpdate);
		transaction.commit();
	}

	public void deleteParticipantFromMeeting(long id, Participant participantForDelition) {
		Meeting meetingForParticipantDeletion = findMeetingById(id);
		meetingForParticipantDeletion.removeParticipant(participantForDelition);
		Transaction transaction = (Transaction) connector.getSession().beginTransaction();
		connector.getSession().delete(participantForDelition);
		transaction.commit();
	}
	
	//Tested + some "Clean Code" names

}
