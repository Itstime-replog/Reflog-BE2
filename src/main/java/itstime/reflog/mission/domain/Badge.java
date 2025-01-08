package itstime.reflog.mission.domain;

public enum Badge {
    FIRST_MEETING(1), // 첫 만남
    RETROSPECTIVE_GURU(7), // 회고쟁이
    RETROSPECTIVE_MANIA(30), // 회고매니아
    KING_OF_COMMUNICATION(3), // 소통의 왕
    WEEKLY_REPORTER(2), // 주간 리포터
    MONTHLY_REPORTER(2), // 월간 리포터
    HABIT_STARTER(5), // 습관의 시작
    MOTIVATION_STARTER(3), // 동기의 시작
    POWER_OF_HEART(5), // 하트의 힘
    RETROSPECTIVE_STARTER(1), // 회고스타터
    RETROSPECTIVE_REVIEWER(2), // 회고리뷰어
    POWER_OF_COMMENTS(3); // 댓글의 힘

    Badge(int targetNum) {
        this.targetNum = targetNum;
    }
    private final int targetNum;

    public int getTargetNum() {
        return targetNum;
    }
}

