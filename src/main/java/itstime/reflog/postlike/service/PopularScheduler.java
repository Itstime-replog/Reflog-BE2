package itstime.reflog.postlike.service;

import itstime.reflog.postlike.domain.PopularPost;
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

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledPopularPosts() {

        List<Object[]> postLikesTop = new ArrayList<>(postLikeRepository.findAllCommunityPostLikesTop());
        postLikesTop.addAll(postLikeRepository.findAllRetrospectPostLikesTop());

        //좋아요 수인 object[1]이 Object 타입이기 떄문에 Long 타입으로 바꿔 준 후 비교 정렬
        postLikesTop.sort((o1, o2) -> Long.compare((Long) o2[2], (Long) o1[2]));

        //이 중 상위 세개만 가져옴
        List<Object[]> postLikesTopThree = postLikesTop.stream().limit(3).toList();

        for (Object[] postLike : postLikesTopThree) {
            PopularPost popularPost = PopularPost.builder()
                    .postId((Long) postLike[1])
                    .postType(PostType.valueOf((String) postLike[0]))
                    .build();
            popularPostRepository.save(popularPost);
        }
    }
}
