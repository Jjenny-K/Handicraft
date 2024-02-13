package com.activity_service.service;

import com.activity_service.domain.dto.NewsfeedCreateRequestDto;
import com.activity_service.domain.entity.*;
import com.activity_service.domain.type.ActivityType;
import com.activity_service.exception.CustomApiException;
import com.activity_service.exception.ErrorCode;
import com.activity_service.repository.*;
//import com.activity_service.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HeartService {

    private static final Logger logger = LoggerFactory.getLogger((HeartService.class));

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostHeartRepository postHeartRepository;
    private final CommentHeartRepository commentHeartRepository;

    public HeartService(PostRepository postRepository,
                        CommentRepository commentRepository,
                        PostHeartRepository postHeartRepository,
                        CommentHeartRepository commentHeartRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postHeartRepository = postHeartRepository;
        this.commentHeartRepository = commentHeartRepository;
    }

    // 게시글 좋아요
    @Transactional
    public PostHeart postHeart(Long postId) {
//        CustomUser user = SecurityUtil.getCurrentUsername()
//                .flatMap(userRepository::findOneWithAuthoritiesWithProFileImageByEmail)
//                .orElseThrow(() -> new BadCredentialsException("로그인 유저 정보가 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.NOT_FOUND_POST));

        /**
         * TODO : user_service, api gateway, ... - userId 값 연동 필요 (임의 사용자 지정)
         * userId = 1
         */

        long userId = 1;

        // 중복 좋아요 방지
        if (postHeartRepository.findPostHeart(userId, post).isPresent())
            throw new CustomApiException(ErrorCode.DUPLICATED_POST_HEART);

        PostHeart postHeart = PostHeart.builder()
                .userId(userId)
                .post(post)
                .build();

        PostHeart savedPostHeart = postHeartRepository.save(postHeart);

        NewsfeedCreateRequestDto newsfeedCreateRequestDto = NewsfeedCreateRequestDto.builder()
                .userId(userId)
                .activityType(ActivityType.POST_HEART)
                .activityId(savedPostHeart.getId())
                .relatedUserId(post.getUserId())
                .build();

        /**
         * TODO : newsfeed_service - newsfeedCreateRequestDto create 연동 필요(임시 삭제)
         */

//        newsfeedService.createNewsfeed(newsfeedCreateRequestDto);

        return savedPostHeart;
    }

    // 게시글 좋아요
    @Transactional
    public CommentHeart commentHeart(Long commentId) {
//        CustomUser user = SecurityUtil.getCurrentUsername()
//                .flatMap(userRepository::findOneWithAuthoritiesWithProFileImageByEmail)
//                .orElseThrow(() -> new BadCredentialsException("로그인 유저 정보가 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(ErrorCode.NOT_FOUND_COMMENT));

        /**
         * TODO : user_service, api gateway, ... - userId 값 연동 필요 (임의 사용자 지정)
         * userId = 1
         */

        long userId = 1;

        // 중복 좋아요 방지
        if (commentHeartRepository.findCommentHeart(userId, comment).isPresent())
            throw new CustomApiException(ErrorCode.DUPLICATED_COMMENT_HEART);

        CommentHeart commentHeart = CommentHeart.builder()
                .userId(userId)
                .comment(comment)
                .build();

        CommentHeart savedCommentHeart = commentHeartRepository.save(commentHeart);

        NewsfeedCreateRequestDto newsfeedCreateRequestDto = NewsfeedCreateRequestDto.builder()
                .userId(userId)
                .activityType(ActivityType.COMMENT_HEART)
                .activityId(savedCommentHeart.getId())
                .relatedUserId(comment.getPost().getUserId())
                .build();

        /**
         * TODO : newsfeed_service - newsfeedCreateRequestDto create 연동 필요(임시 삭제)
         */

//        newsfeedService.createNewsfeed(newsfeedCreateRequestDto);

        return savedCommentHeart;
    }

}