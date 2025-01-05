package itstime.reflog.postlike.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.goal.dto.DailyGoalDto;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.dto.PostLikeDto;
import itstime.reflog.postlike.repository.PostLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public void createPostLike(Member member, Community community){

        PostLike postLike = PostLike.builder()
                .isLike(false)  // 처음 생성할 때는 false 설정
                .member(member)
                .community(community)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void updatePostLike(Long memberId, Long communityId, PostLikeDto.PostLikeSaveOrUpdateRequest dto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByMemberAndCommunity(member, community)
                .orElse(null);

        if (postLike != null) {
            // 이미 존재하는 경우 상태 반전
            postLike.toggleLike();
        } else {
            // 존재하지 않으면 새로 생성
            postLike = PostLike.builder()
                    .isLike(false)  // 처음 생성할 때는 false 설정
                    .member(member)
                    .community(community)
                    .build();
        }
        postLikeRepository.save(postLike);
    }
}
