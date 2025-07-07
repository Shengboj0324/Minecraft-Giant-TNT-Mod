package com.yourname.villageraimod.ai;

public enum VillagerEmotion {
    HAPPY(0.8f, "I feel wonderful today!"),
    CONTENT(0.6f, "Everything seems fine."),
    NEUTRAL(0.5f, "How can I help you?"),
    ANNOYED(0.3f, "Please leave me alone."),
    ANGRY(0.1f, "Get away from me!"),
    FEARFUL(0.2f, "I'm scared of you!"),
    LOVE(0.9f, "I adore you so much!"),
    TRUST(0.7f, "I believe in you."),
    SUSPICIOUS(0.3f, "I don't trust you..."),
    GRATEFUL(0.8f, "Thank you for everything!");

    private final float positivity;
    private final String defaultMessage;

    VillagerEmotion(float positivity, String defaultMessage) {
        this.positivity = positivity;
        this.defaultMessage = defaultMessage;
    }

    public float getPositivity() {
        return positivity;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public boolean isPositive() {
        return positivity > 0.5f;
    }

    public boolean isNegative() {
        return positivity < 0.5f;
    }

    public static VillagerEmotion fromPositivity(float positivity) {
        if (positivity >= 0.85f) return LOVE;
        if (positivity >= 0.75f) return HAPPY;
        if (positivity >= 0.65f) return TRUST;
        if (positivity >= 0.55f) return CONTENT;
        if (positivity >= 0.45f) return NEUTRAL;
        if (positivity >= 0.35f) return ANNOYED;
        if (positivity >= 0.25f) return SUSPICIOUS;
        if (positivity >= 0.15f) return FEARFUL;
        return ANGRY;
    }
} 