package itstime.reflog.community.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.dto.MyPageDto;
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

import static itstime.reflog.mission.domain.Badge.KING_OF_COMMUNICATION;

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
    private final MissionService missionService;
    private final MemberServiceHelper memberServiceHelper;
    private final CommunityConverter communityConverter;


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

        // null 체크 및 기본값 처리
        List<String> fileUrls = dto.getFileUrls() != null ? dto.getFileUrls() : Collections.emptyList();

        fileUrls.forEach(tempUrl -> {
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

        // 미션
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        missionService.incrementMissionProgress(member.getId(), myPage, KING_OF_COMMUNICATION);

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
        List<CommunityDto.CombinedCategoryResponse> responses = communityConverter.communityResponseConverter(communities, member);

        //글 유형에 회고일지가 있는 경우
        if (postTypes != null && postTypes.contains("회고일지")) {
            List<Retrospect> retrospects = retrospectRepository.findByVisibilityIsTrue();
            List<CommunityDto.CombinedCategoryResponse> retrospectResponses = communityConverter.retrospectResponseConverter(retrospects, member);
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

        List<CommunityDto.CombinedCategoryResponse> responses = communityConverter.communityResponseConverter(communities, member);
        //회고일지 게시물 중 키워드가 일치하는 게시물 찾기
        List<Retrospect> retrospects = retrospectRepository.findByTitleContainingAndVisibilityIsTrue(title);

        List<CommunityDto.CombinedCategoryResponse> retrospectResponses = communityConverter.retrospectResponseConverter(retrospects, member);
        responses.addAll(retrospectResponses); // 두 리스트 합치기(회고일지, 커뮤니티)

        return responses;
    }

    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getAllCommunity(String memberId) {
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        List<Community> communities = communityRepository.findAllByOrderByCreatedAtDesc();
        List<Retrospect> retrospects = retrospectRepository.findAllByVisibilityTrueOrderByCreatedDateDesc();

        //회고일지, 커뮤니티 리스트를 합쳐 하나의 리스트로
        List<CommunityDto.CombinedCategoryResponse> responses = communityConverter.retrospectResponseConverter(retrospects, member);

        List<CommunityDto.CombinedCategoryResponse> communityResponses = communityConverter.communityResponseConverter(communities, member);

        responses.addAll(communityResponses);

        //최신 순으로 정렬해주기 : reversed() !
        responses.sort(Comparator.comparing(CommunityDto.CombinedCategoryResponse::getCreatedDate).reversed());

        return responses;
    }

    @Transactional
    public List<MyPageDto.MyPagePostResponse> getPostsByProfile(String nickname) {
        // 1. 멤버 조회
        MyPage myPage = myPageRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        Member member = myPage.getMember();

        // 2. 이 프로필이 작성한 커뮤니티 글 전체조회
        List<Community> communityList = communityRepository.findAllByMemberOrderByIdDesc(member);

        // 3. 이 프로필이 커뮤니티 글 정리
        List<MyPageDto.MyPagePostResponse> responses = communityList.stream()
                .map(community -> {

                    List<String> postTypes = new ArrayList<>(community.getPostTypes()); // 강제 초기화
                    List<String> learningTypes = new ArrayList<>(community.getLearningTypes()); // 강제 초기화

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumCommunityPostLike(community);
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByCommunity(community);

                    return MyPageDto.MyPagePostResponse.fromCommunity(community, postTypes, learningTypes,totalLike, commentCount);
                })
                .collect(Collectors.toList());

        // 4. 이 프로필이 회고일지 글 전체조회
        List<Retrospect> retrospectList = retrospectRepository.findAllByMemberOrderByIdDesc(member);

        // 5. 이 프로필이 회고일지 글 정리
        List<MyPageDto.MyPagePostResponse> retrospectResponses = retrospectList.stream()
                .map(retrospect -> {

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByRetrospect(retrospect);

                    return MyPageDto.MyPagePostResponse.fromRetrospect(retrospect, totalLike, commentCount);
                }).toList();

        responses.addAll(retrospectResponses);
        return responses;
    }
}
