package edu.byu.cs240.breed34.familymapclient.client.models;

/**
 * The relationship between two family members.
 */
public enum Relationship {
    FATHER,
    MOTHER,
    SPOUSE,
    CHILD;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        switch (this) {
            case FATHER:
                return "Father";
            case MOTHER:
                return  "Mother";
            case SPOUSE:
                return  "Spouse";
            case CHILD:
                return "Child";
            default:
                return null;
        }
    }
}
