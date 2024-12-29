package itstime.reflog.mission.domain;

public enum Badge {
    FIRST_MEETING(1),
    RETROSPECTIVE_GURU(7),
    RETROSPECTIVE_MANIA(30),
    KING_OF_COMMUNICATION(3),
    WEEKLY_REPORTER(2),
    MONTHLY_REPORTER(2),
    HABIT_STARTER(5),
    MOTIVATION_STARTER(3),
    POWER_OF_HEART(5),
    RETROSPECTIVE_STARTER(1),
    RETROSPECTIVE_REVIEWER(2),
    POWER_OF_COMMENTS(3);

    Badge(int targetNum) {
        this.targetNum = targetNum;
    }
    private final int targetNum;

    public int getTargetNum() {
        return targetNum;
    }
}

