package edu.byu.cs240.breed34.familymapclient.client.models;

import models.Person;

/**
 * Defines a family member for a person.
 */
public class FamilyMember extends Person {
    /**
     * The relationship of the given person to a selected person.
     */
    private Relationship relationship;

    public FamilyMember(Person person, Relationship relationship) {
        super(person.getPersonID(),
                person.getAssociatedUsername(),
                person.getFirstName(),
                person.getLastName(),
                person.getGender());

        this.relationship = relationship;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        FamilyMember member = (FamilyMember)obj;
        return  super.equals(obj) &&
                this.relationship.equals(member.relationship);
    }

    public Relationship getRelationship() {
        return relationship;
    }
}
