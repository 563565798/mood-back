package cn.jun.dev.controller;

import cn.jun.dev.common.Result;
import cn.jun.dev.dto.MoodShareCommentDTO;
import cn.jun.dev.service.MoodShareCommentService;
import cn.jun.dev.vo.MoodShareCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share/comment")
public class MoodShareCommentController {

    @Autowired
    private MoodShareCommentService commentService;

    @PostMapping
    public Result<Void> addComment(@RequestBody @Validated MoodShareCommentDTO dto) {
        commentService.addComment(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    @GetMapping("/{shareId}")
    public Result<List<MoodShareCommentVO>> getComments(@PathVariable Long shareId) {
        return Result.success(commentService.getComments(shareId));
    }
}
