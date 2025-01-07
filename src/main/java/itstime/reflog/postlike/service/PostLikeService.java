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
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
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
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final MyPageRepository myPageRepository;

    @Transactional
    public void togglePostLike(Long memberId,Long postId, String postType){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

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
    public List<CommunityDto.CombinedCategoryResponse> getTopLikeCommunityPosts(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        List<Object[]> postLikesTop = new ArrayList<>(postLikeRepository.findAllCommunityPostLikesTop());
        postLikesTop.addAll(postLikeRepository.findAllRetrospectPostLikesTop());

        //좋아요 수인 object[1]이 Object 타입이기 떄문에 Long 타입으로 바꿔 준 후 비교 정렬
        postLikesTop.sort((o1, o2) -> Long.compare((Long) o2[2], (Long) o1[2]));

        //이 중 상위 세개만 가져옴
        List<Object[]> postLikesTopThree = postLikesTop.stream().limit(3).toList();

        //반환하는 배열 초기화
        List<CommunityDto.CombinedCategoryResponse> combinedCategoryResponses = new ArrayList<>();

        //상위 세개에 대해 반환 객체로 만들어 배열에 저장
        for (int i = 0; i<postLikesTopThree.size(); i++){
            if (postLikesTopThree.get(i)[0].equals("community")) {

                Community community = communityRepository.findById((Long) postLikesTopThree.get(i)[1])
                        .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));                String nickname = myPageRepository.findByMember(community.getMember())
                        .map(MyPage::getNickname)
                        .orElse("닉네임 없음");
                //좋아요 있는지 없는지 플래그
                Boolean isLike = postLikeRepository.findByMemberAndCommunity(member, community).isPresent();

                //게시물마다 좋아요 총 갯수 반환
                int totalLike = getSumCommunityPostLike(community);

                combinedCategoryResponses.add(CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike));

            }else if (postLikesTopThree.get(i)[0].equals("retrospect")) {

                Retrospect retrospect = retrospectRepository.findById((Long)postLikesTopThree.get(i)[1])
                        .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                String nickname = myPageRepository.findByMember(retrospect.getMember())
                        .map(MyPage::getNickname)
                        .orElse("닉네임 없음");
                //좋아요 있는지 없는지 플래그
                Boolean isLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect).isPresent();

                //게시물마다 좋아요 총 갯수 반환
                int totalLike = getSumRetrospectPostLike(retrospect);

                combinedCategoryResponses.add(CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike));
            }
        }
        return combinedCategoryResponses;
    }
}
