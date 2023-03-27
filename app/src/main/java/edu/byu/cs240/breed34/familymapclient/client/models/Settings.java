package edu.byu.cs240.breed34.familymapclient.client.models;

/**
 * Contains the user settings for the application.
 */
public class Settings {
    /**
     * Whether to show lines connecting a person's
     * life events.
     */
    private boolean showLifeStoryLines;

    /**
     * Whether to show lines connecting a person's
     * family tree by generation.
     */
    private boolean showFamilyTreeLines;

    /**
     * Whether to show lines connecting an event to
     * the birth event of the person's spouse.
     */
    private boolean showSpouseLines;

    /**
     * Whether to show events on the father's side.
     */
    private boolean showFatherSide;

    /**
     * Whether to show events on the mother's side.
     */
    private boolean showMotherSide;

    /**
     * Whether to show events for males.
     */
    private boolean showMaleEvents;

    /**
     * Whether to show events for females.
     */
    private boolean showFemaleEvents;

    public Settings() {
        // Default settings.
        showLifeStoryLines = true;
        showFamilyTreeLines = true;
        showSpouseLines = false;
        showFatherSide = true;
        showMotherSide = true;
        showMaleEvents = true;
        showFemaleEvents = true;
    }

    public boolean showLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean showFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean showSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public boolean showFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean showMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean showMaleEvents() {
        return showMaleEvents;
    }

    public void setShowMaleEvents(boolean showMaleEvents) {
        this.showMaleEvents = showMaleEvents;
    }

    public boolean showFemaleEvents() {
        return showFemaleEvents;
    }

    public void setShowFemaleEvents(boolean showFemaleEvents) {
        this.showFemaleEvents = showFemaleEvents;
    }
}
