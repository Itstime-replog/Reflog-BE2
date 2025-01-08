package itstime.reflog.postlike.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.postlike.domain.PopularPost;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.repository.PopularPostRepository;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final MyPageRepository myPageRepository;
    private final PopularPostRepository popularPostRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final MemberRepository memberRepository;

    @Transactional
    public void togglePostLike(String memberId, Long postId, String postType){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

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

    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getTopLikeCommunityPosts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 반환하는 배열 초기화
        List<CommunityDto.CombinedCategoryResponse> combinedCategoryResponses = new ArrayList<>();

        List<PopularPost> postLikesTopThree = popularPostRepository.findAll();

        postLikesTopThree.forEach(popularPost -> {
            CommunityDto.CombinedCategoryResponse response = switch (popularPost.getPostType()) {
                case COMMUNITY -> {
                    Community community = communityRepository.findById(popularPost.getPostId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                    String nickname = myPageRepository.findByMember(community.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");
                    // 좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndCommunity(member, community).isPresent();
                    // 게시물마다 좋아요 총 갯수 반환
                    int totalLike = getSumCommunityPostLike(community);

                    //switch에서 값 반환 yield
                    yield CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike);
                }
                case RETROSPECT -> {
                    Retrospect retrospect = retrospectRepository.findById(popularPost.getPostId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                    String nickname = myPageRepository.findByMember(retrospect.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");
                    // 좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect).isPresent();
                    // 게시물마다 좋아요 총 갯수 반환
                    int totalLike = getSumRetrospectPostLike(retrospect);

                    yield CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike);
                }
                default -> throw new GeneralException(ErrorStatus._POST_NOT_FOUND); // 예외 처리
            };

            combinedCategoryResponses.add(response);
        });

        return combinedCategoryResponses;
    }
}
