package com.myhousekeepingservice.service.impl;

import com.myhousekeepingservice.entity.Comment;
import com.myhousekeepingservice.mapper.CommentMapper;
import com.myhousekeepingservice.service.ICommentService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* <p>
*    评价 服务实现类
* </p>
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
