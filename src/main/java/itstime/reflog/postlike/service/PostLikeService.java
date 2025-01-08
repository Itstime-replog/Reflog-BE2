package itstime.reflog.postlike.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static itstime.reflog.mission.domain.Badge.POWER_OF_HEART;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final MissionService missionService;
    private final MyPageRepository myPageRepository;




    @Transactional
    public void togglePostLike(String memberId, Long postId, String postType){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        PostLike postLike;

        //1. 글 유형에 따라 다른 엔티티에서 테이블 가져오기
        if (PostType.COMMUNITY == PostType.valueOf(postType)){
            Community community = communityRepository.findById(postId)
                    .orElseThrow(()-> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

            //커뮤니티, 멤버 id와 일치하는 좋아요 가져오기
            postLike = postLikeRepository.findByMemberAndCommunity(member, community)
                    .orElse(null);

            //2.좋아요 존재 여부 확인
            if (postLike != null){
                //좋아요가 이미 있다면 테이블 삭제
                postLikeRepository.delete(postLike);
            } else {
                //좋아요가 없다면 테이블 생성
                PostLike newPostLike = PostLike.builder()
                        .member(member)
                        .community(community)
                        .postType(PostType.COMMUNITY)
                        .build();

                postLikeRepository.save(newPostLike);

                // 미션
                missionService.incrementMissionProgress(member.getId(), myPage, POWER_OF_HEART);
            }
        }
        else if (PostType.RETROSPECT == PostType.valueOf(postType)){
            Retrospect retrospect = retrospectRepository.findById(postId)
                    .orElseThrow(()-> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));

            //회고일지, 멤버 id와 일치하는 좋아요 가져오기
            postLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect)
                    .orElse(null);

            //2.좋아요 존재 여부 확인
            if (postLike != null){
                //좋아요가 이미 있다면 테이블 삭제
                postLikeRepository.delete(postLike);
            } else {
                //좋아요가 없다면 테이블 생성
                PostLike newPostLike = PostLike.builder()
                        .member(member)
                        .retrospect(retrospect)
                        .postType(PostType.RETROSPECT)
                        .build();

                postLikeRepository.save(newPostLike);

                // 미션
                missionService.incrementMissionProgress(member.getId(), myPage, POWER_OF_HEART);
            }
        }
        //3. enum으로 설정하지 않은 글 유형이 올 경우 에러 출력
        else {
            throw new GeneralException(ErrorStatus._POSTLIKE_BAD_REQUEST);
        }

    }

    //게시물마다 좋아요 갯수 항상 0이상이므로 int로
    @Transactional
    public int getSumCommunityPostLike(Community community){
        return postLikeRepository.countByCommunity(community);
    }

    @Transactional
    public int getSumRetrospectPostLike(Retrospect retrospect){
        return postLikeRepository.countByRetrospect(retrospect);
    }
}
