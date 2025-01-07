package itstime.reflog.community.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.postlike.service.PostLikeService;
import itstime.reflog.comment.domain.Comment;
import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.domain.UploadedFile;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.community.repository.UploadedFileRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final MyPageRepository myPageRepository;
    private final RetrospectRepository retrospectRepository;
    private final CommentRepository commentRepository;
    private final PostLikeService postLikeService;
    private final PostLikeRepository postLikeRepository;
    private final MemberServiceHelper memberServiceHelper;


    @Transactional
    public void createCommunity(String memberId, CommunityDto.CommunitySaveOrUpdateRequest dto) {
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        Community community = Community.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .postTypes(dto.getPostTypes())
                .learningTypes(dto.getLearningTypes())
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        dto.getFileUrls().forEach(tempUrl -> {
            try {
                // S3 파일 이동
                String fileKey = amazonS3Manager.extractFileKeyFromUrl(tempUrl);
                String newKey = fileKey.replace("temporary/", "community/");
                amazonS3Manager.moveFile(fileKey, newKey);

                // DB에 파일 정보 저장
                UploadedFile uploadedFile = UploadedFile.builder()
                        .fileName(newKey.substring(newKey.lastIndexOf("/") + 1))
                        .fileUrl(amazonS3Manager.getFileUrl(newKey))
                        .community(community)
                        .build();
                uploadedFileRepository.save(uploadedFile);
            } catch (AmazonS3Exception e) {
                throw new GeneralException(ErrorStatus._S3_INVALID_URL);
            } catch (Exception e) {
                // 기타 예상치 못한 오류 처리
                throw new GeneralException(ErrorStatus._S3_FILE_OPERATION_FAILED);
            }
        });

        communityRepository.save(community);

    }

    @Transactional
    public CommunityDto.CommunityResponse getCommunity(Long postId, String postType) {
        // 1. 게시글 조회 & 댓글 조회
        PostType postType1 = PostType.valueOf(postType);

        Community community = null;
        Retrospect retrospect = null;
        List<Comment> comments = null;

        if (postType1 == PostType.COMMUNITY) {
            community = communityRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));
            comments = commentRepository.findAllByCommunityOrderByCreatedAtDesc(community);
        } else if (postType1 == PostType.RETROSPECT) {
            retrospect = retrospectRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));
            comments = commentRepository.findAllByRetrospectOrderByCreatedAtDesc(retrospect);
        } else {
            throw new GeneralException(ErrorStatus._INVALID_POST_TYPE);
        }

        // 2. 댓글 정리
        List<CommentDto.CommentResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseList.add(0,
                    new CommentDto.CommentResponse(
                            comment.getId(),
                            comment.getMember().getName(),
                            comment.getContent(),
                            comment.getParent() != null ? comment.getParent().getId() : null,
                            comment.getCreatedAt()));
        }

        return new CommunityDto.CommunityResponse(commentResponseList);
    }

    @Transactional
    public void updateCommunity(Long communityId, CommunityDto.CommunitySaveOrUpdateRequest dto) {
        // 1. Community 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

        // 2. 기존 파일 제거 및 새 파일 처리
        List<UploadedFile> existingFiles = uploadedFileRepository.findByCommunityId(communityId);
        existingFiles.forEach(file -> uploadedFileRepository.delete(file));

        // 새 파일 처리
        dto.getFileUrls().forEach(tempUrl -> {
            try {
                // S3 파일 이동
                String fileKey = amazonS3Manager.extractFileKeyFromUrl(tempUrl);
                String newKey = fileKey.replace("temporary/", "community/");
                amazonS3Manager.moveFile(fileKey, newKey);

                // 새 파일 정보 저장
                UploadedFile uploadedFile = UploadedFile.builder()
                        .fileName(newKey.substring(newKey.lastIndexOf("/") + 1))
                        .fileUrl(amazonS3Manager.getFileUrl(newKey))
                        .community(community)
                        .build();
                uploadedFileRepository.save(uploadedFile);
            } catch (AmazonS3Exception e) {
                throw new GeneralException(ErrorStatus._S3_INVALID_URL);
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus._S3_FILE_OPERATION_FAILED);
            }
        });

        // 3. Community 업데이트
        community.update(dto.getTitle(), dto.getContent(), dto.getPostTypes(), dto.getLearningTypes());

        // 4. 저장
        communityRepository.save(community);
    }

    @Transactional
    public void deleteCommunity(Long communityId) {
        // 1. Community 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

        // 2. 관련된 파일 삭제
        List<UploadedFile> files = uploadedFileRepository.findByCommunityId(communityId);
        files.forEach(file -> {
            try {
                // S3에서 파일 삭제 (fileUrl 바로 사용)
                amazonS3Manager.deleteImage(file.getFileUrl());
            } catch (AmazonS3Exception e) {
                throw new GeneralException(ErrorStatus._S3_FILE_OPERATION_FAILED);
            }
        });

        // 3. DB에서 파일 정보 삭제
        uploadedFileRepository.deleteAll(files);

        // 4. Community 삭제
        communityRepository.delete(community);
    }

    //커뮤니티 게시글 필터링
    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getFilteredCommunity(String memberId, List<String> postTypes, List<String> learningTypes) {

        Member member = memberServiceHelper.findMemberByUuid(memberId);

        List<Community> communities;

        //학습유형에 기타가 있는 경우
        if (learningTypes != null && learningTypes.contains("기타")) {
            String typePrefix = "기타:%";
            String remainingLearningType = learningTypes.stream()
                    .filter(type -> !"기타".equals(type))
                    .findFirst()
                    .orElse(null); //나머지 유형이 없는 경우 null

            communities = communityRepository.findCommunitiesByLearningTypePrefix(postTypes, typePrefix, remainingLearningType);
        } else {
            communities = communityRepository.findByLearningTypesAndPostTypes(postTypes, learningTypes);
        }

        //커뮤니티 response형태로 반환 닉네임 추가
        List<CommunityDto.CombinedCategoryResponse> responses = communities.stream()
                .map(community -> {
                    //마이페이지 레포지토리에서 닉네임 반환
                    String nickname = myPageRepository.findByMember(community.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");

                    //좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndCommunity(member, community).isPresent();

                    //게시물마다 좋아요 총 갯수 반환
                    int totalLike = postLikeService.getSumCommunityPostLike(community);

                    return CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike);
                })
                .collect(Collectors.toList());

        //글 유형에 회고일지가 있는 경우
        if (postTypes != null && postTypes.contains("회고일지")) {
            List<Retrospect> retrospects = retrospectRepository.findByVisibilityIsTrue();
            List<CommunityDto.CombinedCategoryResponse> retrospectResponses = retrospects.stream()
                    .map(retrospect -> {
                        String nickname = myPageRepository.findByMember(retrospect.getMember())
                                .map(MyPage::getNickname)
                                .orElse("닉네임 없음");

                        //좋아요 있는지 없는지 플래그
                        Boolean isLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect).isPresent();

                        //게시물마다 좋아요 총 갯수 반환
                        int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);

                        return CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike);
                    })
                    .collect(Collectors.toList());
            responses.addAll(retrospectResponses); // 두 리스트 합치기(회고일지, 커뮤니티)
        }

        return responses;
    }

    //커뮤니티 게시물 검색
    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getSearchedCommunity(String memberId, String title) {

        Member member = memberServiceHelper.findMemberByUuid(memberId);

        //커뮤니티 게시물 중 키워드가 일치하는 게시물 찾기
        List<Community> communities = communityRepository.searchCommunitiesByTitleContaining(title);

        List<CommunityDto.CombinedCategoryResponse> responses = communities.stream()
                .map(community -> {
                    String nickname = myPageRepository.findByMember(community.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");

                    //좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndCommunity(member, community).isPresent();

                    //게시물마다 좋아요 총 갯수 반환
                    int totalLike = postLikeService.getSumCommunityPostLike(community);

                    return CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike);
                })
                .collect(Collectors.toList());

        //회고일지 게시물 중 키워드가 일치하는 게시물 찾기
        List<Retrospect> retrospects = retrospectRepository.findByTitleContainingAndVisibilityIsTrue(title);

        List<CommunityDto.CombinedCategoryResponse> retrospectResponses = retrospects.stream()
                .map(retrospect -> {
                    String nickname = myPageRepository.findByMember(retrospect.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");
                    //좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect).isPresent();

                    //게시물마다 좋아요 총 갯수 반환
                    int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);

                    return CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike);
                })
                .collect(Collectors.toList());
        responses.addAll(retrospectResponses); // 두 리스트 합치기(회고일지, 커뮤니티)

        return responses;
    }

    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getAllCommunity(String memberId) {
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        List<Community> communities = communityRepository.findAllByOrderByCreatedAtDesc();
        List<Retrospect> retrospects = retrospectRepository.findAllByVisibilityTrueOrderByCreatedDateDesc();

        //회고일지, 커뮤니티 리스트를 합쳐 하나의 리스트로
        List<CommunityDto.CombinedCategoryResponse> responses = retrospects.stream().map(
                        retrospect -> {
                            String nickname = myPageRepository.findByMember(retrospect.getMember())
                                    .map(MyPage::getNickname)
                                    .orElse("닉네임 없음");
                            //좋아요 있는지 없는지 플래그
                            Boolean isLike = postLikeRepository.findByMemberAndRetrospect(member, retrospect).isPresent();

                            //게시물마다 좋아요 총 갯수 반환
                            int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);

                            return CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike);
                        })
                .collect(Collectors.toList());

        List<CommunityDto.CombinedCategoryResponse> communityResponses = communities.stream()
                .map(community -> {
                    String nickname = myPageRepository.findByMember(community.getMember())
                            .map(MyPage::getNickname)
                            .orElse("닉네임 없음");

                    //좋아요 있는지 없는지 플래그
                    Boolean isLike = postLikeRepository.findByMemberAndCommunity(member, community).isPresent();

                    //게시물마다 좋아요 총 갯수 반환
                    int totalLike = postLikeService.getSumCommunityPostLike(community);

                    return CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike);
                })
                .collect(Collectors.toList());

        responses.addAll(communityResponses);

        //최신 순으로 정렬해주기 : reversed() !
        responses.sort(Comparator.comparing(CommunityDto.CombinedCategoryResponse::getCreatedDate).reversed());

        return responses;
    }

}
