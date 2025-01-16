package itstime.reflog.community.service;

import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.postlike.service.PostLikeService;
import itstime.reflog.retrospect.domain.Retrospect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //final로 설정한 것만 의존성 주임
public class CommunityConverter {

    private final MyPageRepository myPageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<CommunityDto.CombinedCategoryResponse> communityResponseConverter(List<Community> communities, Member member) {

        List<CommunityDto.CombinedCategoryResponse> responses = communities.stream()
                .map(community -> {
                    //마이페이지 레포지토리에서 닉네임 반환
                    String nickname = myPageRepository.findByMember(community.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");

                    List<String> communityPostTypes = new ArrayList<>(community.getPostTypes()); // 강제 초기화
                    List<String> communityLearningTypes = new ArrayList<>(community.getLearningTypes()); // 강제 초기화

                    //좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findLikeByMemberAndCommunity(member, community).isPresent();

                    //게시물마다 좋아요 총 갯수 반환
                    int totalLike = postLikeService.getSumCommunityPostLike(community);

                    //게시물마다 댓글 수 반환
                    Long totalComment = commentRepository.countByCommunity(community);
                    return CommunityDto.CombinedCategoryResponse.fromCommunity(community,communityPostTypes, communityLearningTypes, nickname, isLike, totalLike, totalComment);
                })
                .collect(Collectors.toList());

        return responses;
    }

    @Transactional(readOnly = true)
    public List<CommunityDto.CombinedCategoryResponse> retrospectResponseConverter(List<Retrospect> retrospects, Member member) {

        List<CommunityDto.CombinedCategoryResponse> responses = retrospects.stream().map(
                        retrospect -> {
                            String nickname = myPageRepository.findByMember(retrospect.getMember())
                                    .map(MyPage::getNickname)
                                    .orElse("닉네임 없음");
                            //좋아요 있는지 없는지 플래그
                            Boolean isLike = postLikeRepository.findBookmarkByMemberAndRetrospect(member, retrospect).isPresent();

                            //게시물마다 좋아요 총 갯수 반환
                            int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);

                            //게시물마다 댓글 수 반환
                            Long totalComment = commentRepository.countByRetrospect(retrospect);

                            return CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike, totalComment);
                        })
                .collect(Collectors.toList());

        return responses;
    }

}
