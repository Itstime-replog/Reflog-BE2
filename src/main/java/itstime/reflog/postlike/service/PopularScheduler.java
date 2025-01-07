package itstime.reflog.postlike.service;

import itstime.reflog.postlike.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularScheduler {
    private final PostLikeService postLikeService;
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledPopularPosts(Long memberId) {
        postLikeService.getTopLikeCommunityPosts(memberId);
    }
}
