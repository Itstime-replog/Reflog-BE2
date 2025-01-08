package itstime.reflog.postlike.service;

import itstime.reflog.postlike.domain.PopularPost;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.repository.PopularPostRepository;
import itstime.reflog.postlike.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularScheduler {

    private final PostLikeRepository postLikeRepository;
    private final PopularPostRepository popularPostRepository;

    @Scheduled(cron = "0 56 9 * * *")
    public void scheduledPopularPosts() {

        //1. 이전에 있던 인기글 목록 지우기
        popularPostRepository.deleteAll();

        //2. 커뮤니티, 회고일지 좋아요 순으로 각각 가져오기
        List<Object[]> postLikesTop = new ArrayList<>(postLikeRepository.findCommunityByPostLikeTop());
        postLikesTop.addAll(postLikeRepository.findARetrospectPostLikesTop());

        //좋아요 수인 object[1]이 Object 타입이기 떄문에 Long 타입으로 바꿔 준 후 비교 정렬
        postLikesTop.sort((o1, o2) -> Long.compare((Long) o2[2], (Long) o1[2]));

        //이 중 상위 세개만 가져옴
        List<Object[]> postLikesTopThree = postLikesTop.stream().limit(3).toList();

        //가져온 데이터 저장
        for (Object[] postLike : postLikesTopThree) {

            PopularPost popularPost = PopularPost.builder()
                    .postId((Long) postLike[1])
                    .postType(PostType.valueOf((String) postLike[0]))
                    .build();
            popularPostRepository.save(popularPost);
        }
    }
}
