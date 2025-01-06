package itstime.reflog.postlike.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.repository.PostLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public void togglePostLike(Long memberId,Long communityId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

        //커뮤니티, 멤버 id와 일치하는 좋아요 가져오기
        PostLike postLike = postLikeRepository.findByMemberAndCommunity(member, community)
                .orElse(null);
        if (postLike != null){
            //좋아요가 이미 있다면 테이블 삭제
            postLikeRepository.delete(postLike);
        } else {
            //좋아요가 없다면 테이블 생성
            PostLike newPostLike = PostLike.builder()
                    .member(member)
                    .community(community)
                    .build();

            postLikeRepository.save(newPostLike);
        }

    }

    //게시물마다 좋아요 갯수 항상 0이상이므로 int로
    @Transactional
    public int getSumPostLike(Community community){
        return postLikeRepository.countByCommunity(community);
    }
}
